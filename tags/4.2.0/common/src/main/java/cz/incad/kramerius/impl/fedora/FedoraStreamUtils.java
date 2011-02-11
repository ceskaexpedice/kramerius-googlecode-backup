/*
 * Copyright (C) 2010 Pavel Stastny
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
package cz.incad.kramerius.impl.fedora;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.utils.XMLUtils;

public class FedoraStreamUtils {

    public static String getStreamPath(FedoraAccess fedoraAccess, String uuid) throws IOException, XPathExpressionException {
        Document profileDoc = fedoraAccess.getImageFULLProfile(uuid);
//        return getDsCreate(profileDoc);
        return null;
    }

    public static String getDsCreate(Document profileDoc) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile("/datastreamProfile/dsCreateDate");
        Node oneNode = (Node) expr.evaluate(profileDoc, XPathConstants.NODE);
        if (oneNode != null) {
            Element elm = (Element) oneNode;
            String mimeType = elm.getTextContent();
            if ((mimeType != null) && (!mimeType.trim().equals(""))) {
                mimeType = mimeType.trim();
                return mimeType;
            }
        }
        return null;
    }
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, DatatypeConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("/home/pavels/profile.xml"));
        String createDate = getDsCreate(document);
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(createDate);
        int year = xmlGregorianCalendar.getYear();
        int month = xmlGregorianCalendar.getMonth();
        int day = xmlGregorianCalendar.getDay();
        int hour = xmlGregorianCalendar.getHour();
        int minute = xmlGregorianCalendar.getMinute();
        
        System.out.println(xmlGregorianCalendar.toGregorianCalendar().getTime());
        System.out.println("datastreams/"+year+"/"+month+day+"/"+hour+"/"+minute);
        
    }
}
