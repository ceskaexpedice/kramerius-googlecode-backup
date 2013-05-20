/*
 * Copyright (C) 2012 Pavel Stastny
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package cz.incad.kramerius.service.replication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import cz.incad.kramerius.service.ReplicateException;
import cz.incad.kramerius.utils.IOUtils;
import cz.incad.kramerius.utils.XMLUtils;

/**
 * Explore given FOXML data and replace all <code>file:///</code> external referenced datastreams  into internal type datastream (type M)
 * @author pavels
 */
public class ExternalReferencesFilter implements ReplicationServiceFoxmlFilter {

    @Override
    public byte[] filterFoxmlData(byte[] input) throws ReplicateException{
        try {
            Document document = XMLUtils.parseDocument(new ByteArrayInputStream(input), true);
            Element docElement = document.getDocumentElement();
            if (docElement.getLocalName().equals("digitalObject")) {
                List<Element> datastreamsElements = XMLUtils.getElements(docElement, new XMLUtils.ElementsFilter() {
                    
                    @Override
                    public boolean acceptElement(Element elm) {
                        String elmName = elm.getLocalName();
                        return elmName.equals("datastream") && elm.hasAttribute("CONTROL_GROUP") && elm.getAttribute("CONTROL_GROUP").equals("E");
                    }
                });
                
                for (Element datStreamElm : datastreamsElements) {
                    dataStreamVersions(document, datStreamElm);
                }
            } else { 
                throw new ReplicateException("Not valid FOXML");
            }
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLUtils.print(document, bos);
            return bos.toByteArray();
        } catch (ParserConfigurationException e) {
            throw new ReplicateException(e);
        } catch (SAXException e) {
            throw new ReplicateException(e);
        } catch (IOException e) {
            throw new ReplicateException(e);
        } catch (TransformerException e) {
            throw new ReplicateException(e);
        }
    }

    /**
     * @param dataStreamElm
     * @throws ReplicateException 
     */
    private void dataStreamVersions(Document document, Element dataStreamElm) throws ReplicateException {
        List<Element> versions = XMLUtils.getElements(dataStreamElm, new XMLUtils.ElementsFilter() {
            
            @Override
            public boolean acceptElement(Element element) {
                String locName = element.getLocalName();
                return locName.endsWith("datastreamVersion");
            }
        });
        
        for (Element version : versions) {
            Element found = XMLUtils.findElement( version,"contentLocation", version.getNamespaceURI());
            if (found != null && found.hasAttribute("REF")) {
                try {
                    URL url = new URL(found.getAttribute("REF"));
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")) {
                        changeDatastreamVersion(document, dataStreamElm, version, url);
                    }
                } catch (MalformedURLException e) {
                    throw new ReplicateException(e);
                } catch (IOException e) {
                    throw new ReplicateException(e);
                }
            }
        }
    }

    /**
     * @param version
     * @throws IOException 
     */
    private void changeDatastreamVersion(Document document, Element datastream, Element version, URL url) throws IOException {
        InputStream is = null; 
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            URLConnection urlConnection = url.openConnection();
            is = urlConnection.getInputStream();
            IOUtils.copyStreams(is, bos);
            version.setAttribute("SIZE", ""+bos.size());
            version.removeChild(XMLUtils.findElement( version,"contentLocation",version.getNamespaceURI()));
            Element binaryContent = document.createElementNS(version.getNamespaceURI(), "binaryContent");
            document.adoptNode(binaryContent);
            binaryContent.setTextContent(new String(Base64.encodeBase64(bos.toByteArray())));
            version.appendChild(binaryContent);
            
            datastream.setAttribute("CONTROL_GROUP", "M");
            
        } finally {
            IOUtils.tryClose(is);
        }
    }
}
