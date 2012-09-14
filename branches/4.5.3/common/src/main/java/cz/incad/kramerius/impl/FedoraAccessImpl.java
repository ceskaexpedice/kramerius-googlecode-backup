package cz.incad.kramerius.impl;

import static cz.incad.kramerius.utils.FedoraUtils.IMG_FULL_STREAM;
import static cz.incad.kramerius.utils.FedoraUtils.getFedoraDatastreamsList;
import static cz.incad.kramerius.utils.FedoraUtils.getFedoraStreamPath;
import static cz.incad.kramerius.utils.FedoraUtils.getThumbnailFromFedora;
import static cz.incad.kramerius.utils.RESTHelper.openConnection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.fedora.api.FedoraAPIA;
import org.fedora.api.FedoraAPIAService;
import org.fedora.api.FedoraAPIM;
import org.fedora.api.FedoraAPIMService;
import org.fedora.api.ObjectFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.FedoraNamespaceContext;
import cz.incad.kramerius.FedoraNamespaces;
import cz.incad.kramerius.ProcessSubtreeException;
import cz.incad.kramerius.TreeNodeProcessor;
import cz.incad.kramerius.utils.FedoraUtils;
import cz.incad.kramerius.utils.IOUtils;
import cz.incad.kramerius.utils.RESTHelper;
import cz.incad.kramerius.utils.XMLUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.utils.pid.LexerException;
import cz.incad.kramerius.utils.pid.PIDParser;

/**
 * Default implementation of fedoraAccess
 * @see FedoraAccess
 * @author pavels
 */
public class FedoraAccessImpl implements FedoraAccess {

    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(FedoraAccessImpl.class.getName());
    private final KConfiguration configuration;

    private String fedoraVersion;
    private StringTemplateGroup xpaths;
    
    private XPathFactory xPathFactory;
    
    
    @Inject
    public FedoraAccessImpl(KConfiguration configuration) throws IOException {
        super();
        this.configuration = configuration;
        this.xPathFactory = XPathFactory.newInstance();
        // read template
        InputStream stream = FedoraAccessImpl.class.getResourceAsStream("fedora_xpaths.stg");
        String string = IOUtils.readAsString(stream, Charset.forName("UTF-8"), true);
        xpaths = new StringTemplateGroup(new StringReader(string), DefaultTemplateLexer.class);
    }

    

    @Override
    public List<Element> getPages(String pid, boolean deep) throws IOException {
        Document relsExt = getRelsExt(pid);
        return getPages(pid, relsExt.getDocumentElement());
    }

    
    
    @Override
    public String getKrameriusModelName(Document relsExt) throws IOException {
        try {
            Element foundElement = XMLUtils.findElement(relsExt.getDocumentElement(), "hasModel", FedoraNamespaces.FEDORA_MODELS_URI);
            if (foundElement != null) {
                String sform = foundElement.getAttributeNS(FedoraNamespaces.RDF_NAMESPACE_URI, "resource");
                PIDParser pidParser = new PIDParser(sform);
                pidParser.disseminationURI();
                return pidParser.getObjectId();
            } else {
                throw new IllegalArgumentException("cannot find model of given document");
            }
        } catch (DOMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalArgumentException(e);
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getKrameriusModelName(String pid) throws IOException {
        return getKrameriusModelName(getRelsExt(pid));
    }

    @Override
    public Document getRelsExt(String pid) throws IOException {
        try {
            String relsExtUrl = relsExtUrl(KConfiguration.getInstance(), makeSureObjectPid(pid));
            LOGGER.fine("Reading rels ext +" + relsExtUrl);
            InputStream docStream = RESTHelper.inputStream(relsExtUrl, KConfiguration.getInstance().getFedoraUser(), KConfiguration.getInstance().getFedoraPass());

            return XMLUtils.parseDocument(docStream, true);
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }


    private String makeSureObjectPid(String pid) throws LexerException {
        PIDParser pidParser = new PIDParser(pid);
        pidParser.objectPid();
        String sureObjectPid = pidParser.isPagePid() ? pidParser.getParentObjectPid() : pidParser.getObjectPid();
        return sureObjectPid;
    }

    


    @Override
    public List<String> getModelsOfRel(Document relsExt) {
        try {
            throw new UnsupportedOperationException("still unsupported");
//            Element foundElement = XMLUtils.findElement(relsExt.getDocumentElement(), "hasModel", FedoraNamespaces.FEDORA_MODELS_URI);
//            if (foundElement != null) {
//                String sform = foundElement.getAttributeNS(FedoraNamespaces.RDF_NAMESPACE_URI, "resource");
//                PIDParser pidParser = new PIDParser(sform);
//                pidParser.disseminationURI();
//                ArrayList<String> model = RelsExtModelsMap.getModelsOfRelation(pidParser.getObjectId());
//                return model;
//            } else {
//                throw new IllegalArgumentException("cannot find model of ");
//            }
        } catch (DOMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalArgumentException(e);
        }
    }


    @Override
    public List<String> getModelsOfRel(String pid) throws IOException {
        return getModelsOfRel(getRelsExt(pid));
    }

    @Override
    public String getDonator(Document relsExt) {
        try {
            Element foundElement = XMLUtils.findElement(relsExt.getDocumentElement(), "hasDonator", FedoraNamespaces.KRAMERIUS_URI);
            if (foundElement != null) {
                String sform = foundElement.getAttributeNS(FedoraNamespaces.RDF_NAMESPACE_URI, "resource");
                PIDParser pidParser = new PIDParser(sform);
                pidParser.disseminationURI();
                return pidParser.getObjectId();
            } else {
                return "";
            }
        } catch (DOMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalArgumentException(e);
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getDonator(String pid) throws IOException {
        return getDonator(getRelsExt(pid));
    }

    @Override
    public Document getBiblioMods(String pid) throws IOException {
        try {
            String biblioModsUrl = biblioMods(KConfiguration.getInstance(), makeSureObjectPid(pid));
            LOGGER.fine("Reading bibliomods +" + biblioModsUrl);
            InputStream docStream = RESTHelper.inputStream(biblioModsUrl, KConfiguration.getInstance().getFedoraUser(), KConfiguration.getInstance().getFedoraPass());
            return XMLUtils.parseDocument(docStream, true);
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }

    @Override
    public Document getDC(String pid) throws IOException {
        try {
            String dcUrl = dc(KConfiguration.getInstance(), makeSureObjectPid(pid));
            LOGGER.fine("Reading dc +" + dcUrl);
            InputStream docStream = RESTHelper.inputStream(dcUrl, KConfiguration.getInstance().getFedoraUser(), KConfiguration.getInstance().getFedoraPass());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copyStreams(docStream, bos);
            return XMLUtils.parseDocument(new ByteArrayInputStream(bos.toByteArray()), true);
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }
    
    @Override
    public String findFirstViewablePid(String pid) throws IOException{
        final List<String> foundPids = new ArrayList<String>();
        try {
            processSubtree(makeSureObjectPid(pid), new TreeNodeProcessor() {
                
                boolean breakProcess = false;
                int previousLevel = 0;
                

                @Override
                public boolean breakProcessing(String pid, int level) {
                    return breakProcess;
                }

                @Override
                public void process(String pid, int level) throws ProcessSubtreeException {
                    try {
                        if (previousLevel < level || level == 0) {
                            if(FedoraAccessImpl.this.isImageFULLAvailable(pid)) {
                                foundPids.add(pid);
                                breakProcess = true;
                            }
                        } else if (previousLevel > level) {
                            breakProcess = true;
                        } else if ((previousLevel == level) && (level != 0)) {
                            breakProcess = true;
                        }
                        previousLevel = level;
                    } catch (Exception e) {
                        throw new ProcessSubtreeException(e);
                    }
                }
                
            });
        } catch (ProcessSubtreeException e) {
            throw new IOException(e);
        } catch (LexerException e) {
            throw new IOException(e);
        }

        return foundPids.isEmpty() ? null : foundPids.get(0);
    }

    @Override
    public boolean getFirstViewablePath(List<String> pids, List<String> models) throws IOException{
        try {
            String pid = pids.get(pids.size() - 1);
            pid = makeSureObjectPid(pid);
            if(isImageFULLAvailable(pid)){
                return true;
            }
            Document relsExt = getRelsExt(pid);
            Element descEl = XMLUtils.findElement(relsExt.getDocumentElement(), "Description", FedoraNamespaces.RDF_NAMESPACE_URI);
            List<Element> els = XMLUtils.getElements(descEl);
            for(Element el: els){
                if (getTreePredicates().contains( el.getLocalName())) {
                    if(el.hasAttribute("rdf:resource")){
                        pid = el.getAttributes().getNamedItem("rdf:resource").getNodeValue();
                        pids.add(pid);
                        models.add(getKrameriusModelName(pid));
                        //return getFirstViewablePath(pids, models);
                        boolean hit = getFirstViewablePath(pids, models);
                        if(hit){
                            return true;
                        }else{
                            pids.remove(pids.size() - 1);
                            models.remove(pids.size() - 1);
                        }
                    }
                }
             }
            return false;
        } catch (DOMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
        
    }




    @Override
    public List<Element> getPages(String pid, Element rootElementOfRelsExt)
            throws IOException {
        try {
            ArrayList<Element> elms = new ArrayList<Element>();
            String xPathStr = "/RDF/Description/hasPage";
            XPath xpath = this.xPathFactory.newXPath();
            XPathExpression expr = xpath.compile(xPathStr);
            NodeList nodes = (NodeList) expr.evaluate(rootElementOfRelsExt, XPathConstants.NODESET);
            for (int i = 0, lastIndex = nodes.getLength() - 1; i <= lastIndex; i++) {
                Element elm = (Element) nodes.item(i);
                elms.add(elm);
            }
            return elms;
        } catch (XPathExpressionException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }

    @Override
    public InputStream getSmallThumbnail(String pid) throws IOException {
        try {
            pid = makeSureObjectPid(pid);
            HttpURLConnection con = (HttpURLConnection) openConnection(getThumbnailFromFedora(configuration, makeSureObjectPid(pid)), configuration.getFedoraUser(), configuration.getFedoraPass());
            InputStream thumbInputStream = con.getInputStream();
            return thumbInputStream;
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
         }
    }

    @Override
    public Document getSmallThumbnailProfile(String pid) throws IOException {
        try {
            pid = makeSureObjectPid(pid);
            HttpURLConnection con = (HttpURLConnection) openConnection(thumbImageProfile(configuration, makeSureObjectPid(pid)), configuration.getFedoraUser(), configuration.getFedoraPass());
            InputStream stream = con.getInputStream();
            return XMLUtils.parseDocument(stream, true);
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }

    @Override
    public String getSmallThumbnailMimeType(String pid) throws IOException, XPathExpressionException {
        try {
            Document profileDoc = getSmallThumbnailProfile(makeSureObjectPid(pid));
            return disectMimetypeFromProfile(profileDoc,getFedoraVersion());
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }

    public InputStream getImageFULL(String pid) throws IOException {
        try {
            pid = makeSureObjectPid(pid);
            HttpURLConnection con = (HttpURLConnection) openConnection(getFedoraStreamPath(configuration, makeSureObjectPid(pid), IMG_FULL_STREAM), configuration.getFedoraUser(), configuration.getFedoraPass());
            con.connect();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream thumbInputStream = con.getInputStream();
                return thumbInputStream;
            } else {
                throw new IOException("404");
            }
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }

    @Override
    public boolean isImageFULLAvailable(String pid) throws IOException {
        try {
            return isStreamAvailable(makeSureObjectPid(pid), FedoraUtils.IMG_FULL_STREAM);
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }

    public InputStream getFedoraDataStreamsList(String pid ) throws IOException {
        try {
            HttpURLConnection con = (HttpURLConnection) openConnection(getFedoraDatastreamsList(configuration, makeSureObjectPid(pid)), configuration.getFedoraUser(), configuration.getFedoraPass());
            InputStream stream = con.getInputStream();
            return stream;
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }
    
    
    public boolean isStreamAvailable(String pid, String streamName) throws IOException {
        try {
            Document parseDocument = XMLUtils.parseDocument(getFedoraDataStreamsList(makeSureObjectPid(pid)), true);
            return disectDatastreamInListOfDatastreams(parseDocument, streamName, getFedoraVersion());
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (XPathExpressionException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }

    @Override
    public boolean isContentAccessible(String pid) throws IOException {
        return true;
    }

    public String getImageFULLMimeType(String pid) throws IOException, XPathExpressionException {
        try {
            Document profileDoc = getImageFULLProfile(makeSureObjectPid(pid));
            return disectMimetypeFromProfile(profileDoc, getFedoraVersion());
        } catch (LexerException e) {
            throw new IOException(e.getMessage());
        }
    }

    boolean disectDatastreamInListOfDatastreams(Document datastreams, String dsId, String fedoraVersion) throws XPathExpressionException, IOException {
        XPath xpath = this.xPathFactory.newXPath();
        xpath.setNamespaceContext(new FedoraNamespaceContext());
        String templateName = "find_datastream"+FedoraUtils.getVersionCompatibilityPrefix(fedoraVersion);
        StringTemplate xpathTemplate = xpaths.getInstanceOf(templateName);
        xpathTemplate.setAttribute("dsid", dsId);
        String xpathStringExp = xpathTemplate.toString();
        XPathExpression expr = xpath.compile(xpathStringExp);
        Node oneNode = (Node) expr.evaluate(datastreams, XPathConstants.NODE);
        return (oneNode != null);
    }

    String disectMimetypeFromProfile(Document profileDoc, String fedoraVersion)
            throws XPathExpressionException {
        XPathFactory factory = this.xPathFactory;
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new FedoraNamespaceContext());

        String templateName = "find_mimetype"+FedoraUtils.getVersionCompatibilityPrefix(fedoraVersion);
        StringTemplate xpathTemplate = xpaths.getInstanceOf(templateName);
        String xpathStringExp = xpathTemplate.toString();
        XPathExpression expr = xpath.compile(xpathStringExp);

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


    @Override
    public Document getImageFULLProfile(String pid) throws IOException {
        try {
            pid = makeSureObjectPid(pid);
            HttpURLConnection con = (HttpURLConnection) openConnection(fullImageProfile(configuration, pid), configuration.getFedoraUser(), configuration.getFedoraPass());
            InputStream stream = con.getInputStream();
            return XMLUtils.parseDocument(stream, true);
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IOException(e);
        }
    }

    public static String fullImageProfile(KConfiguration configuration, String pid) {
        return dsProfile(configuration, FedoraUtils.IMG_FULL_STREAM, pid);
    }

    public static String thumbImageProfile(KConfiguration configuration, String pid) {
        return dsProfile(configuration, FedoraUtils.IMG_THUMB_STREAM , pid);
    }

    public static String dcProfile(KConfiguration configuration, String pid) {
        return dsProfile(configuration, FedoraUtils.DC_STREAM, pid);
    }

    public static String biblioModsProfile(KConfiguration configuration, String pid) {
        return dsProfile(configuration, FedoraUtils.BIBLIO_MODS_STREAM , pid);
    }

    public static String relsExtProfile(KConfiguration configuration, String pid) {
        return dsProfile(configuration, FedoraUtils.RELS_EXT_STREAM, pid);
    }

    public static String profile(KConfiguration configuration,  String pid) {
        String fedoraObject = configuration.getFedoraHost() + "/objects/" + pid;
        return fedoraObject + "?format=text/xml";
    }

    public static String dsProfile(KConfiguration configuration, String ds, String pid) {
        String fedoraObject = configuration.getFedoraHost() + "/objects/" + pid;
        return fedoraObject + "/datastreams/" + ds + "?format=text/xml";
    }

    public static String dsProfileForPid(KConfiguration configuration, String ds, String pid) {
        String fedoraObject = configuration.getFedoraHost() + "/objects/" + pid;
        return fedoraObject + "/datastreams/" + ds + "?format=text/xml";
    }

    public static String biblioMods(KConfiguration configuration, String pid) {
        String fedoraObject = configuration.getFedoraHost() + "/get/" + pid;
        return fedoraObject + "/BIBLIO_MODS";
    }

    public static String dc(KConfiguration configuration, String pid) {
        String fedoraObject = configuration.getFedoraHost() + "/get/" + pid;
        return fedoraObject + "/DC";
    }

    public static String relsExtUrl(KConfiguration configuration, String pid) {
        String url = configuration.getFedoraHost() + "/get/" + pid + "/"+FedoraUtils.RELS_EXT_STREAM;
        return url;
    }
    private FedoraAPIM APIMport;
    private FedoraAPIA APIAport;
    private ObjectFactory of;

    public FedoraAPIA getAPIA() {
        if (APIAport == null) {
            initAPIA();
        }
        return APIAport;
    }

    public FedoraAPIM getAPIM() {
        if (APIMport == null) {
            initAPIM();
        }
        return APIMport;
    }

    public ObjectFactory getObjectFactory() {
        if (of == null) {
            of = new ObjectFactory();
        }
        return of;
    }

    private void initAPIA() {
        FedoraAPIAService APIAservice = new FedoraAPIAService();
        APIAport = APIAservice.getFedoraAPIAServiceHTTPPort();
        connectFedora((BindingProvider) APIAport,
                KConfiguration.getInstance().getFedoraHost() + "/services/access");
    }

    private void initAPIM() {
        FedoraAPIMService APIMservice = new FedoraAPIMService();
        APIMport = APIMservice.getFedoraAPIMServiceHTTPPort();
        connectFedora((BindingProvider) APIMport,
                KConfiguration.getInstance().getFedoraHost() + "/services/management");
    }
    
    private static void connectFedora(BindingProvider portBinding, String endpointAddress) {
        final String user = KConfiguration.getInstance().getFedoraUser();
        final String pwd = KConfiguration.getInstance().getFedoraPass();
        final Map<String, Object> context = portBinding.getRequestContext();
        context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
        context.put(BindingProvider.USERNAME_PROPERTY, user);
        context.put(BindingProvider.PASSWORD_PROPERTY, pwd);
    }
    
    /*
    private List<String> treePredicates = Arrays.asList(new String[]{
    "http://www.nsdl.org/ontologies/relationships#hasPage",
    "http://www.nsdl.org/ontologies/relationships#hasPart",
    "http://www.nsdl.org/ontologies/relationships#hasVolume",
    "http://www.nsdl.org/ontologies/relationships#hasItem",
    "http://www.nsdl.org/ontologies/relationships#hasUnit"
    });
     */
//    private ArrayList<String> treePredicates;
//
//    private List<String> getTreePredicates() {
//        if (treePredicates == null) {
//            treePredicates = new ArrayList<String>();
//            String prefix = KConfiguration.getInstance().getProperty("fedora.predicatesPrefix");
//            
//            String[] preds = KConfiguration.getInstance().getPropertyList("fedora.treePredicates");
//            for (String s : preds) {
//                LOGGER.log(Level.INFO, prefix+s);
//                treePredicates.add(prefix + s);
//            }
//        }
//        return treePredicates;
//    }
    
    private List<String> getTreePredicates() {
        return Arrays.asList(KConfiguration.getInstance().getPropertyList("fedora.treePredicates"));
    }

    public void processSubtree(String pid, TreeNodeProcessor processor) throws ProcessSubtreeException, IOException {
        try {
            pid = makeSureObjectPid(pid);
            Document relsExt = getRelsExt(pid);
            processSubtreeInternal(pid, relsExt, processor,0);
        } catch (LexerException e) {
            throw new ProcessSubtreeException(e);
        } catch (XPathExpressionException e) {
            throw new ProcessSubtreeException(e);
        }
    }

    public boolean processSubtreeInternal(String pid, Document relsExt, TreeNodeProcessor processor, int level) throws XPathExpressionException, LexerException, IOException, ProcessSubtreeException {
        processor.process(pid, level);
        boolean breakProcessing = processor.breakProcessing(pid,level);
        if (breakProcessing) return breakProcessing;
        if (relsExt == null) return false; 
        XPathFactory factory = this.xPathFactory;
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new FedoraNamespaceContext());
        XPathExpression expr = xpath.compile("/rdf:RDF/rdf:Description/*");
        NodeList nodes = (NodeList) expr.evaluate(relsExt, XPathConstants.NODESET);
        for (int i = 0,ll=nodes.getLength(); i < ll; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element iteratingElm = (Element) node;
                String namespaceURI = iteratingElm.getNamespaceURI();
                if (namespaceURI != null && (namespaceURI.equals(FedoraNamespaces.ONTOLOGY_RELATIONSHIP_NAMESPACE_URI)  || 
                    namespaceURI.equals(FedoraNamespaces.RDF_NAMESPACE_URI))) {
                    String attVal = iteratingElm.getAttributeNS(FedoraNamespaces.RDF_NAMESPACE_URI, "resource");
                    if (!attVal.trim().equals("")) {
                        PIDParser pidParser = new PIDParser(attVal);
                        pidParser.disseminationURI();
                        String objectId = pidParser.getObjectPid();
                        if (pidParser.getNamespaceId().equals("uuid")) {
                            StringBuffer buffer = new StringBuffer();
                            {   // debug print
                                for (int k = 0; k < level; k++) { buffer.append(" "); }
                                LOGGER.fine(buffer.toString()+" processing pid [" +attVal+"]");
                            }
                            Document iterationgRelsExt = getRelsExt(objectId);
                            breakProcessing = processSubtreeInternal(pidParser.getObjectPid(), iterationgRelsExt, processor, level + 1);
                            if (breakProcessing) break;
                        }
                    }
                    
                }
            }
        }
        
        return breakProcessing;
    }
    
    

    public Set<String> getPids(String pid) throws IOException {
        final Set<String> retval = new HashSet<String>();
        try {
            processSubtree(pid, new TreeNodeProcessor() {

                @Override
                public void process(String pid, int level) {
                    retval.add(pid);
                }

                @Override
                public boolean breakProcessing(String pid, int level) {
                    return false;
                }
            });
        } catch (ProcessSubtreeException e) {
            throw new IOException(e);
        }
        return retval;
    }

    @Override
    public InputStream getDataStream(String pid, String datastreamName) throws IOException {
        try {
            pid = makeSureObjectPid(pid);
            String datastream = configuration.getFedoraHost() + "/get/" + pid + "/" + datastreamName;
            HttpURLConnection con = (HttpURLConnection) openConnection(datastream, configuration.getFedoraUser(), configuration.getFedoraPass());
            con.connect();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream thumbInputStream = con.getInputStream();
                return thumbInputStream;
            }
            throw new FileNotFoundException(datastream);
        } catch (LexerException e) {
            throw new IOException(e);
        }
    }

    public InputStream getDsProfileForPIDStream(String pid, String streamName) throws MalformedURLException, IOException {
        try {
            pid = makeSureObjectPid(pid);
            HttpURLConnection con = (HttpURLConnection) openConnection(dsProfileForPid(configuration, streamName, pid), configuration.getFedoraUser(), configuration.getFedoraPass());
            InputStream stream = con.getInputStream();
            return stream;
        } catch (LexerException e) {
            throw new IOException(e);
        }
    }

    
    
    @Override
    public String getMimeTypeForStream(String pid, String datastreamName) throws IOException {
        try {
            Document parseDocument = XMLUtils.parseDocument(getDsProfileForPIDStream(pid, datastreamName), true);
            return disectMimetypeFromProfile(parseDocument, getFedoraVersion());
        } catch (XPathExpressionException e) {
            throw new IOException(e);
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean isFullthumbnailAvailable(String pid) throws IOException {
        return (this.isStreamAvailable(pid, FedoraUtils.IMG_PREVIEW_STREAM));
    }

    @Override
    public InputStream getFullThumbnail(String pid) throws IOException {
        HttpURLConnection con = (HttpURLConnection) openConnection(getFedoraStreamPath(configuration, pid, FedoraUtils.IMG_PREVIEW_STREAM), configuration.getFedoraUser(), configuration.getFedoraPass());
        con.connect();
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream thumbInputStream = con.getInputStream();
            return thumbInputStream;
        } else {
            throw new IOException("404");
        }
    }


    @Override
    public String getFullThumbnailMimeType(String pid) throws IOException,
            XPathExpressionException {
        throw new UnsupportedOperationException("");
    }


    @Override
    public Document getObjectProfile(String pid) throws  IOException {
        try {
            HttpURLConnection con = (HttpURLConnection) openConnection(profile(configuration, pid), configuration.getFedoraUser(), configuration.getFedoraPass());
            con.connect();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = con.getInputStream();
                return XMLUtils.parseDocument(is, true);
            } else {
                throw new IOException("404");
            }
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
            throw new IOException(e);
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
            throw new IOException(e);
        }
    }
    
    @Override
    public Document getStreamProfile(String pid, String stream) throws IOException {
        try {
            HttpURLConnection con = (HttpURLConnection) openConnection(dsProfile(configuration, stream, pid), configuration.getFedoraUser(), configuration.getFedoraPass());
            con.connect();
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = con.getInputStream();
                return XMLUtils.parseDocument(is, true);
            } else {
                throw new IOException("404");
            }
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
            throw new IOException(e);
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
            throw new IOException(e);
        }

    }

    public InputStream getFedoraDescribeStream() throws IOException {
        HttpURLConnection con = (HttpURLConnection) openConnection(FedoraUtils.getFedoraDescribe(configuration), configuration.getFedoraUser(), configuration.getFedoraPass());
        con.connect();
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream stream = con.getInputStream();
            return stream;
        } else {
            throw new IOException("404");
        }
    }
    
    @Override
    public String getFedoraVersion() throws IOException {
        if (fedoraVersion == null) {
            try {
                fedoraVersion = disectFedoraVersionFromStream(getFedoraDescribeStream());
            } catch (XPathExpressionException e) {
                throw new IOException(e);
            } catch (ParserConfigurationException e) {
                throw new IOException(e);
            } catch (SAXException e) {
                throw new IOException(e);
            }
        }
        return fedoraVersion;
    }

    public String  disectFedoraVersionFromStream(InputStream stream) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        // do not use namespaces
        Document parseDocument = XMLUtils.parseDocument(stream, false);
        XPathFactory factory = this.xPathFactory;
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile("/fedoraRepository/repositoryVersion/text()");
        Node oneNode = (Node) expr.evaluate(parseDocument, XPathConstants.NODE);
        return (oneNode != null && oneNode.getNodeType() == Node.TEXT_NODE) ? ((Text)oneNode).getData() : "";
    }
}