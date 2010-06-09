package cz.incad.kramerius.service.impl;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpression;

import org.fedora.api.MIMETypedStream;
import org.fedora.api.RelationshipTuple;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.service.PolicyService;
import cz.incad.kramerius.utils.conf.KConfiguration;

public class PolicyServiceImpl implements PolicyService {
    public static final Logger LOGGER = Logger.getLogger(PolicyServiceImpl.class.getName());

    @Inject
    @Named("securedFedoraAccess")
    FedoraAccess fedoraAccess;
    @Inject
    KConfiguration configuration;

    @Override
    public void setPolicy(String pid, String policyName) {
        Set<String> pids = fedoraAccess.getPids(pid);
        for (String s : pids) {
        	String p = s.replace(INFO, "");
            setPolicyForNode(p, policyName);
        }
    }

    private void setPolicyForNode(String pid, String policyName) {
        setPolicyDC(pid, policyName);
        setPolicyRELS_EXT(pid, policyName);
        setPolicyPOLICY(pid, policyName);
    }

    private void setPolicyDC(String pid, String policyName) {
        MIMETypedStream stream = fedoraAccess.getAPIA().getDatastreamDissemination(pid, "DC", null);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(stream.getStream()));
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            xpath.setNamespaceContext(new NamespaceContext() {

                @SuppressWarnings("unchecked")
                @Override
                public Iterator getPrefixes(String arg0) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public String getPrefix(String arg0) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public String getNamespaceURI(String prefix) {
                    if (prefix == null)
                        throw new NullPointerException("Null prefix");
                    else if ("dc".equals(prefix))
                        return "http://purl.org/dc/elements/1.1/";

                    return XMLConstants.XML_NS_URI;
                }
            });
            XPathExpression expr = xpath.compile("//dc:rights");
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                node.setTextContent("policy:" + policyName);
            }
            
            if (((doc.getFeature("Core", "3.0")) == null)
                    || ((doc.getFeature("LS", "3.0")) == null)) {
                throw new UnsupportedOperationException("DOM3 unsupported");
            }
            DOMImplementation domImpl = doc.getImplementation();
            DOMImplementationLS domImplLS = (DOMImplementationLS) domImpl.getFeature("LS", "3.0");
                   
            LSSerializer ser = domImplLS.createLSSerializer();
            DOMConfiguration conf = (DOMConfiguration)ser;
            conf.setParameter("xml-declaration", false);
            LSOutput lso = domImplLS.createLSOutput();
            lso.setEncoding("UTF-8");
            StringWriter swr = new StringWriter();
            lso.setCharacterStream(swr);
            ser.write(doc, lso);
            fedoraAccess.getAPIM().modifyDatastreamByValue(pid, "DC", null, null, null, null, swr.getBuffer().toString().getBytes("UTF-8"), null, null, "", false);
        } catch (Throwable t) {
            LOGGER.severe("Error while setting DC policy" + t);
            throw new RuntimeException(t);
        }
    }
    
    private static final String POLICY_PREDICATE = "http://www.nsdl.org/ontologies/relationships#policy";
    private static final String INFO = "info:fedora/";
    
    private void setPolicyRELS_EXT(String pid, String policyName) {
        for (RelationshipTuple t:fedoraAccess.getAPIM().getRelationships(INFO+pid, POLICY_PREDICATE)){
            fedoraAccess.getAPIM().purgeRelationship(INFO+pid, POLICY_PREDICATE, t.getObject(), true, null);
        }
        fedoraAccess.getAPIM().addRelationship(INFO+pid, POLICY_PREDICATE, "policy:"+policyName, true, null);
    }
    
    private void setPolicyPOLICY(String pid, String policyName) {
        fedoraAccess.getAPIM().modifyDatastreamByReference(pid, "POLICY", null, null, null, null, "http://local.fedora.server/fedora/get/policy:" + policyName + "/POLICYDEF", null, null, null, false);
    }
    
    /**
     * test
     */
    public static void main(String[] args) {
        PolicyServiceImpl inst = new PolicyServiceImpl();
        inst.fedoraAccess = new FedoraAccessImpl(null);
        inst.configuration = KConfiguration.getKConfiguration();
        inst.setPolicy("uuid:0eaa6730-9068-11dd-97de-000d606f5dc6", "public");
    }
}
