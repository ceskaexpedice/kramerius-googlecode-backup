package com.qbizm.kramerius.imptool.poc.convertor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.lizardtech.djvu.DjVuOptions;
import com.lizardtech.djvu.DjVuPage;
import com.lizardtech.djvubean.DjVuImage;
import com.qbizm.kramerius.imp.jaxb.ContentLocationType;
import com.qbizm.kramerius.imp.jaxb.DatastreamType;
import com.qbizm.kramerius.imp.jaxb.DatastreamVersionType;
import com.qbizm.kramerius.imp.jaxb.DigitalObject;
import com.qbizm.kramerius.imp.jaxb.ObjectPropertiesType;
import com.qbizm.kramerius.imp.jaxb.PropertyType;
import com.qbizm.kramerius.imp.jaxb.StateType;
import com.qbizm.kramerius.imp.jaxb.XmlContentType;
import com.qbizm.kramerius.imptool.poc.utils.UUIDManager;
import com.qbizm.kramerius.imptool.poc.utils.XSLTransformer;
import com.qbizm.kramerius.imptool.poc.valueobj.ConvertorConfig;
import com.qbizm.kramerius.imptool.poc.valueobj.DublinCore;
import com.qbizm.kramerius.imptool.poc.valueobj.ImageMetaData;
import com.qbizm.kramerius.imptool.poc.valueobj.ImageRepresentation;
import com.qbizm.kramerius.imptool.poc.valueobj.RelsExt;
import com.qbizm.kramerius.imptool.poc.valueobj.ServiceException;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import cz.incad.kramerius.utils.imgs.KrameriusImageSupport;

public abstract class BaseConvertor {
    static{
        //disable djvu convertor verbose logging
        DjVuOptions.out = new java.io.PrintStream ( new java.io.OutputStream() { public void write(int b){} });
    }

    protected final Logger log = Logger.getLogger(BaseConvertor.class);

    /**
     * Chybove hlasky
     */
    protected static final String WARN_FILE_DOESNT_EXIST = "Referenced file not found";

    protected static final String WARN_PAGE_INDEX = "Page index missing.";

    protected static final String WARN_MUR_EMPTY_UID = "MonographUnitRepresentation - empty UID";

    /**
     * Validacni pattern na PID FOXML objektu - prevzato z fedory
     */
    protected static final String PID_PATTERN = "([A-Za-z0-9]|-|\\.)+:(([A-Za-z0-9])|-|\\.|~|_|(%[0-9A-F]{2}))+";

    /**
     * Prefix pid
     */
    protected static final String PID_PREFIX = "uuid:";

    /**
     * Nazvy modelu
     */
    protected static final String MODEL_MONOGRAPH = "model:monograph";

    protected static final String MODEL_MONOGRAPH_UNIT = "model:monographunit";

    protected static final String MODEL_PERIODICAL = "model:periodical";

    protected static final String MODEL_PERIODICAL_VOLUME = "model:periodicalvolume";

    protected static final String MODEL_PERIODICAL_ITEM = "model:periodicalitem";

    protected static final String MODEL_INTERNAL_PART = "model:internalpart";

    protected static final String MODEL_PAGE = "model:page";

    private static final String CUSTOM_MODEL_PREFIX = "kramerius";

    /**
     * Nazvy a konstanty datastreamu
     */
    private static final String STREAM_ID_TXT = "TEXT_OCR";

    private static final String STREAM_ID_IMG = "IMG_FULL";

    private static final String STREAM_ID_THUMB = "IMG_THUMB";

    private static final String STREAM_ID_POLICY = "POLICY";
    private static final String STREAM_ID_POLICY_DEF = "POLICYDEF";

    private static final String STREAM_ID_MODS = "BIBLIO_MODS";

    private static final String STREAM_ID_RELS_EXT = "RELS-EXT";

    private static final String STREAM_VERSION_SUFFIX = ".0";

    private static final String SUFFIX_TXT = "txt";

    protected static final String XSL_PATH = "";

    protected static final String DONATOR_ID = "***Donator NF***";

    protected static final String DONATOR_PID = "donator:norway";

    protected static final String POLICY_PUBLIC = "policy:public";

    protected static final String POLICY_PRIVATE = "policy:private";

    // Kramerius 3 visibility constants

    public static final int PFLAG_PUBLIC = 1;

    public static final int PFLAG_PRIVATE = 2;

    public static final int PFLAG_INHERIT = 3;

    
    private static final String NS_DC = "http://purl.org/dc/elements/1.1/";
    /**
     * Atributy
     */
    protected final ConvertorConfig config;

    private final DocumentBuilder docBuilder;

    private final Map<String, String> mimeMap = new TreeMap<String, String>();

    private SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private int objectCounter;

    public BaseConvertor(ConvertorConfig config) throws ServiceException {
        this.config = config;

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            this.docBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ServiceException(e);
        }

        mimeMap.put("txt", "text/plain");
        mimeMap.put("djvu", "image/vnd.djvu");
        mimeMap.put("jpg", "image/jpeg");
        mimeMap.put("jpeg", "image/jpeg");
        mimeMap.put("jp2", "image/jp2");
        mimeMap.put("jpx", "image/jpx");
    }

    /**
     * Prida property digitalnimu objektu
     * 
     * @param digitalObject
     * @param name
     * @param value
     */
    private void setProperty(DigitalObject digitalObject, String name, String value) {
        PropertyType pt = new PropertyType();
        pt.setNAME(name);
        pt.setVALUE(value);
        digitalObject.getObjectProperties().getProperty().add(pt);
    }

    /**
     * Nastavi atributy spolecne pro vsechny digitalni objekty
     * 
     * @param digitalObject
     */
    protected void setCommonProperties(DigitalObject digitalObject, String pid, String title) {
        digitalObject.setPID(pid);
        digitalObject.setVERSION("1.1");

        if (digitalObject.getObjectProperties() == null) {
            digitalObject.setObjectProperties(new ObjectPropertiesType());
        }

        setProperty(digitalObject, "info:fedora/fedora-system:def/model#label", title!=null?title.substring(0,Math.min(255, title.length())):"null");
        setProperty(digitalObject, "info:fedora/fedora-system:def/model#state", "Active");
        setProperty(digitalObject, "info:fedora/fedora-system:def/model#ownerId", "fedoraAdmin");

        String timestamp = this.timestampFormat.format(new Date());
        setProperty(digitalObject, "info:fedora/fedora-system:def/model#createdDate", timestamp);
        setProperty(digitalObject, "info:fedora/fedora-system:def/view#lastModifiedDate", timestamp);
    }

    /**
     * Vytvori streamy pro foxml objekt
     * 
     * @param foxmlObject
     * @param sourceObject
     * @param dc
     * @param re
     * @param xslFile
     * @param files
     * @throws ServiceException
     */
    protected final void setCommonStreams(DigitalObject foxmlObject, Object sourceObject, DublinCore dc, RelsExt re, String xslFile, String policyID, ImageRepresentation[] files)
            throws ServiceException {
        // /== DUBLIN CORE
        DatastreamType dcStream = this.createDublinCoreStream(dc);
        foxmlObject.getDatastream().add(dcStream);
        // \== DUBLIN CORE

        // /== BASE64 stream
        this.addBase64Streams(foxmlObject, files);
        // \== BASE64 stream

        // /== BIBLIO_MODS stream
        DatastreamType biblioModsStream = this.createBiblioModsStream(sourceObject, xslFile);
        foxmlObject.getDatastream().add(biblioModsStream);
        // \== BIBLIO_MODS stream

        // /== RELS-EXT stream
        DatastreamType relsExtStream = this.createRelsExtStream(re);
        foxmlObject.getDatastream().add(relsExtStream);
        // \== RELS-EXT stream

        // /== POLICY stream
        if (policyID != null) {
            DatastreamType policyStream = this.createPolicyStream(policyID);
            foxmlObject.getDatastream().add(policyStream);
        }
        // \== POLICY stream
    }

    /**
     * Pripoji k XML elementu XML potomka
     * 
     * @param d
     * @param parent
     * @param name
     * @param value
     * @return
     */
    private Element appendChild(Document d, Node parent, String name, String value) {
        Element e = d.createElement(name);
        e.setTextContent(value);
        parent.appendChild(e);
        return e;
    }
    
    private Element appendChildNS(Document d, Node parent,String prefix, String name, String value) {
        Element e = d.createElementNS(prefix,name);
        e.setTextContent(value);
        parent.appendChild(e);
        return e;
    }

    /**
     * Ulozeni digitalniho objektu
     * 
     * @param foxmlObject
     * @throws ServiceException
     */
    protected void marshalDigitalObject(DigitalObject foxmlObject) throws ServiceException {
        String fileName = foxmlObject.getPID().substring(foxmlObject.getPID().lastIndexOf(':') + 1) + ".xml";
        this.marshalDigitalObject(foxmlObject, getConfig().getExportFolder(), fileName);
    }

    /**
     * Ulozeni digitalniho objektu
     * 
     * @param foxmlObject
     * @throws ServiceException
     */
    private void marshalDigitalObject(DigitalObject foxmlObject, String directory, String file) throws ServiceException {
        if (!Pattern.matches(PID_PATTERN, foxmlObject.getPID())) {
            throw new ServiceException("Invalid PID format: " + foxmlObject.getPID());
        }
        try {
            long start = System.currentTimeMillis();
            String path = directory + System.getProperty("file.separator") + file;
            getConfig().getMarshaller().marshal(foxmlObject, new File(path));
            long end = System.currentTimeMillis();
            if (log.isDebugEnabled()) {
                log.debug("Marshal: time=" + ((end - start)) + "ms ; file=" + file);
            }
            objectCounter++;
        } catch (JAXBException jaxbe) {
            throw new ServiceException(jaxbe);
        }
    }

    /**
     * Vytvori rels-ext datastream
     * 
     * @param foxmlPage
     * @param dcStream
     * @throws ServiceException
     */
    private DatastreamType createDublinCoreStream(DublinCore dc) throws ServiceException {
        DatastreamType stream = new DatastreamType();
        stream.setID("DC");
        stream.setSTATE(StateType.A);
        stream.setVERSIONABLE(false);
        stream.setCONTROLGROUP("X");

        DatastreamVersionType version = new DatastreamVersionType();
        version.setCREATED(this.getCurrentXMLGregorianCalendar());
        version.setFORMATURI("http://www.openarchives.org/OAI/2.0/oai_dc/");
        version.setID("DC" + STREAM_VERSION_SUFFIX);
        version.setLABEL("Dublin Core Record for this object");
        version.setMIMETYPE("text/xml");
        stream.getDatastreamVersion().add(version);

        XmlContentType xmlContent = new XmlContentType();
        version.setXmlContent(xmlContent);

        Document document = docBuilder.newDocument();

        Element root = document.createElementNS("http://www.openarchives.org/OAI/2.0/oai_dc/", "oai_dc:dc");
        // root.setAttributeNS(
        // "http://www.w3.org/2001/XMLSchema-instance",
        // "xsi:schemaLocation",
        // "http://www.openarchives.org/OAI/2.0/oai_dc/ "
        // + "http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
        root.setAttribute("xmlns:dc", NS_DC);

        this.appendChildNS(document, root,NS_DC , "dc:title", dc.getTitle());

        if (dc.getCreator() != null) {
            for (String creator : dc.getCreator()) {
                this.appendChildNS(document, root, NS_DC, "dc:creator", creator);
            }
        }
        if (dc.getPublisher() != null) {
            for (String publisher : dc.getPublisher()) {
                this.appendChildNS(document, root,NS_DC, "dc:publisher", publisher);
            }
        }
        if (dc.getContributor() != null) {
            for (String contributor : dc.getContributor()) {
                this.appendChildNS(document, root,NS_DC, "dc:contributor", contributor);
            }
        }
        if (dc.getIdentifier() != null) {
            for (String identifier : dc.getIdentifier()) {
                this.appendChildNS(document, root,NS_DC, "dc:identifier", identifier);
            }
        }
        if (dc.getSubject() != null) {
            for (String subject : dc.getSubject()) {
                this.appendChildNS(document, root,NS_DC, "dc:subject", subject);
            }
        }
        if (dc.getDate() != null) {
            this.appendChildNS(document, root,NS_DC, "dc:date", dc.getDate());
        }
        if (dc.getLanguage() != null) {
            this.appendChildNS(document, root,NS_DC, "dc:language", dc.getLanguage());
        }
        if (dc.getDescription() != null) {
            this.appendChildNS(document, root, NS_DC, "dc:description", dc.getDescription());
        }
        if (dc.getFormat() != null) {
            this.appendChildNS(document, root,NS_DC, "dc:format", dc.getFormat());
        }
        if (dc.getType() != null) {
            this.appendChildNS(document, root, NS_DC,"dc:type", dc.getType());
        }
        if (dc.getRights() != null) {
            this.appendChildNS(document, root, NS_DC, "dc:rights", dc.getRights());
        }

        xmlContent.getAny().add(root);

        return stream;
    }

    /**
     * Vytvori MODS datastream pomoci xsl transformace
     * 
     * @param page
     * @return
     * @throws ServiceException
     */
    private DatastreamType createBiblioModsStream(Object page, String xslFile) throws ServiceException {
        DatastreamType stream = new DatastreamType();
        stream.setID(STREAM_ID_MODS);
        stream.setCONTROLGROUP("X");
        stream.setSTATE(StateType.A);
        stream.setVERSIONABLE(false);

        DatastreamVersionType version = new DatastreamVersionType();
        version.setID(STREAM_ID_MODS + STREAM_VERSION_SUFFIX);
        version.setLABEL("BIBLIO_MODS description of current object");
        version.setMIMETYPE("text/xml");
        version.setCREATED(getCurrentXMLGregorianCalendar());

        if (StringUtils.isEmpty(xslFile)) {
            return stream;
        }

        try {
            ByteArrayOutputStream sourceOut = new ByteArrayOutputStream();

            InputStream stylesheet = this.getClass().getClassLoader().getResourceAsStream(XSL_PATH + xslFile);

            getConfig().getMarshaller().marshal(page, sourceOut);

            // if (log.isDebugEnabled()) {
            // log.debug("XML source: " + sourceOut.toString());
            // }

            ByteArrayInputStream sourceIn = new ByteArrayInputStream(sourceOut.toByteArray());

            // ByteArrayOutputStream result = new ByteArrayOutputStream();
            Document mods = XSLTransformer.transform(sourceIn, stylesheet);
            Element root = mods.getDocumentElement();

            // if (log.isDebugEnabled()) {
            // log.debug("XSLT output: " +
            // XSLTransformer.documentToString(mods));
            // }
            XmlContentType xmlContent = new XmlContentType();
            xmlContent.getAny().add(root);

            version.setXmlContent(xmlContent);
            // version.setBinaryContent(result.toByteArray());

            // if (log.isDebugEnabled()) {
            // log.debug("XSLT result: " + result.toString());
            // }
        } catch (JAXBException e) {
            throw new ServiceException(e);
        }

        stream.getDatastreamVersion().add(version);

        return stream;
    }

    /**
     * Vytvori base64 datastreamy pro dany objekt
     * 
     * @param foxmlObject
     * @param files
     * @throws ServiceException
     */
    private void addBase64Streams(DigitalObject foxmlObject, ImageRepresentation[] files) throws ServiceException {
        if (files != null) {
            for (ImageRepresentation f : files) {
                if (f != null) {
                    File imageFile = new File(getConfig().getImportFolder() + System.getProperty("file.separator") + f.getFilename());
                    if (imageFile.exists() && imageFile.canRead()) {
                        DatastreamType base64Stream = this.createBase64Stream(f.getFilename());
                        foxmlObject.getDatastream().add(base64Stream);
                        if (isImage(f.getFilename())) {
                            DatastreamType thumbnailStream = this.createThumbnailStream(f.getFilename());
                            if (thumbnailStream != null) {
                                foxmlObject.getDatastream().add(thumbnailStream);
                            }
                        }

                        if (f.getImageMetaData() != null) {
                            DatastreamType imageAdmStream = this.createImageMetaStream(getBase64StreamId(f.getFilename()) + "_ADM", f.getImageMetaData());
                            foxmlObject.getDatastream().add(imageAdmStream);
                        }
                    } else {
                        log.warn(WARN_FILE_DOESNT_EXIST + ": " + f.getFilename());
                    }
                }
            }
        }
    }

    /**
     * Ziska priponu ze jmena souboru
     * 
     * @param filename
     * @return
     */
    private String getSuffix(String filename) {
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * Je zadany soubor obrazek?
     */
    private boolean isImage(String filename) {
        return !SUFFIX_TXT.equals(getSuffix(filename));
    }

    private String getBase64StreamId(String filename) {
        return (isImage(filename)) ? STREAM_ID_IMG : STREAM_ID_TXT;
    }

    private String getImageMime(String filename) {
        return mimeMap.get(getSuffix(filename));
    }

    /**
     * Vytvori datastream obsahujici base64 zakodovana binarni data
     * 
     * @param pageHref
     * @return stream
     */
    private DatastreamType createBase64Stream(String filename) throws ServiceException {
        try {
            String streamId = getBase64StreamId(filename);

            DatastreamType stream = new DatastreamType();
            stream.setID(streamId);
            stream.setCONTROLGROUP("M");
            stream.setVERSIONABLE(false);
            stream.setSTATE(StateType.A);

            DatastreamVersionType version = new DatastreamVersionType();
            version.setCREATED(getCurrentXMLGregorianCalendar());
            version.setID(streamId + STREAM_VERSION_SUFFIX);

            version.setMIMETYPE(getImageMime(filename));

            // long start = System.currentTimeMillis();

            File pageFile = new File(getConfig().getImportFolder() + System.getProperty("file.separator") + filename);
            byte[] binaryContent = FileUtils.readFileToByteArray(pageFile);

            version.setBinaryContent(binaryContent);

            // if (log.isDebugEnabled()) {
            // log.debug("Binary attachment: time(read)="
            // + (end - start)
            // + "ms; filesize="
            // + (pageFile.length() / 1024)
            // + "kB; file="
            // + pageFile.getName());
            // }
            stream.getDatastreamVersion().add(version);

            return stream;
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Vytvori datastream POLICY obsahujici odkaz na objekt s XACML pravidly ve
     * streamu POLICYDEF
     * 
     * @param policyID
     *            identifikator odkazovaneho objektu
     * @return stream
     */
    private DatastreamType createPolicyStream(String policyID) throws ServiceException {
        DatastreamType stream = new DatastreamType();
        stream.setID(STREAM_ID_POLICY);
        stream.setCONTROLGROUP("E");
        stream.setVERSIONABLE(false);
        stream.setSTATE(StateType.A);

        DatastreamVersionType version = new DatastreamVersionType();
        version.setCREATED(getCurrentXMLGregorianCalendar());
        version.setID(STREAM_ID_POLICY + STREAM_VERSION_SUFFIX);

        version.setMIMETYPE("text/xml");
        ContentLocationType location = new ContentLocationType();
        location.setTYPE("URL");
        location.setREF("http://local.fedora.server/fedora/get/" + policyID + "/" + STREAM_ID_POLICY_DEF);
        version.setContentLocation(location);
        stream.getDatastreamVersion().add(version);

        return stream;
    }

    /**
     * Vytvori datastream obsahujici base64 zakodovana binarni data
     * 
     * @param pageHref
     * @return stream
     */
    private DatastreamType createThumbnailStream(String filename) throws ServiceException {
        try {
            String streamId = STREAM_ID_THUMB;

            DatastreamType stream = new DatastreamType();
            stream.setID(streamId);
            stream.setCONTROLGROUP("M");
            stream.setVERSIONABLE(false);
            stream.setSTATE(StateType.A);

            DatastreamVersionType version = new DatastreamVersionType();
            version.setCREATED(getCurrentXMLGregorianCalendar());
            version.setID(streamId + STREAM_VERSION_SUFFIX);

            version.setMIMETYPE("image/jpeg");

            // long start = System.currentTimeMillis();

            byte[] binaryContent = scaleImage(getConfig().getImportFolder() + System.getProperty("file.separator") + filename, 0, 128);
            if (binaryContent.length == 0) {
                return null;
            }

            version.setBinaryContent(binaryContent);

            // if (log.isDebugEnabled()) {
            // log.debug("Binary attachment: time(read)="
            // + (end - start)
            // + "ms; filesize="
            // + (pageFile.length() / 1024)
            // + "kB; file="
            // + pageFile.getName());
            // }
            stream.getDatastreamVersion().add(version);

            return stream;
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    private byte[] scaleImage(String fileName, int page, int height) throws IOException, MalformedURLException {
    	
        Image img = ImageIO.read(new File(fileName));
        if (img == null) {
            try{
                
                com.lizardtech.djvu.Document doc = new com.lizardtech.djvu.Document(new File(fileName).toURI().toURL());
                doc.setAsync(true);
                DjVuPage[] p = new DjVuPage[1];
                // read page from the document - index 0, priority 1, favorFast true
                p[0] = doc.getPage(0, 1, true);
                p[0].setAsync(false);
                DjVuImage djvuImage = new DjVuImage(p, true);
    
                Rectangle pageBounds = djvuImage.getPageBounds(page);
                Image[] images = djvuImage.getImage(new JPanel(), new Rectangle(pageBounds.width, pageBounds.height));
                if (images.length == 1) {
                    img = images[0];
                }
            }catch (Throwable t){
                log.warn("Unsupported image type", t);
            }
        }
        if (img != null) {
            Image scaledImage = scaleByHeight(img, height);

            BufferedImage bufImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics gr = bufImage.getGraphics();
            gr.drawImage(scaledImage, 0, 0, null);
            gr.dispose();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufImage, "jpg", outputStream);
            return outputStream.toByteArray();
        }
        return new byte[0];
    }

    private Image scaleByHeight(Image img, int height) {
        int nHeight = height;
        ImageObserver observer = new ImageObserver() {

            public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                return false;
            }
        };
        double div = (double) img.getHeight(observer) / (double) nHeight;
        double nWidth = (double) img.getWidth(observer) / div;
        Image scaledImage = KrameriusImageSupport.scale(img, (int)nWidth, nHeight);
        return scaledImage;
    }
    
    private static final String NS_ADM =  "http://www.qbizm.cz/kramerius-fedora/image-adm-description";

    private DatastreamType createImageMetaStream(String id, ImageMetaData data) throws ServiceException {
        DatastreamType stream = new DatastreamType();
        stream.setID(id);
        stream.setCONTROLGROUP("X");
        stream.setSTATE(StateType.A);
        stream.setVERSIONABLE(false);

        DatastreamVersionType version = new DatastreamVersionType();
        version.setID(id + STREAM_VERSION_SUFFIX);
        version.setLABEL("Image administrative metadata");
        version.setMIMETYPE("text/xml");
        version.setCREATED(getCurrentXMLGregorianCalendar());

        XmlContentType xmlContent = new XmlContentType();
        version.setXmlContent(xmlContent);

        Document document = docBuilder.newDocument();

        Element root = document.createElementNS(NS_ADM, "adm:Description");

        if (!StringUtils.isEmpty(data.getUrn())) {
            this.appendChildNS(document, root,NS_ADM, "adm:URN", data.getUrn());
        }
        if (!StringUtils.isEmpty(data.getSici())) {
            this.appendChildNS(document, root,NS_ADM, "adm:SICI", data.getSici());
        }
        if (!StringUtils.isEmpty(data.getScanningDevice())) {
            this.appendChildNS(document, root,NS_ADM, "adm:ScanningDevice", data.getScanningDevice());
        }
        if (!StringUtils.isEmpty(data.getScanningParameters())) {
            this.appendChildNS(document, root,NS_ADM, "adm:ScanningParameters", data.getScanningParameters());
        }
        if (!StringUtils.isEmpty(data.getOtherImagingInformation())) {
            this.appendChildNS(document, root,NS_ADM, "adm:OtherImagingInformation", data.getOtherImagingInformation());
        }

        xmlContent.getAny().add(root);

        stream.getDatastreamVersion().add(version);
        return stream;
    }

    private static final String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String NS_FEDORA = "info:fedora/fedora-system:def/model#";
    private static final String NS_KRAMERIUS = "http://www.nsdl.org/ontologies/relationships#";
    private static final String NS_OAI = "http://www.openarchives.org/OAI/2.0/";
    /**
     * Vytvori rels-ext datastream
     * 
     * @param foxmlPage
     * @param dcStream
     * @throws ServiceException
     */
    private DatastreamType createRelsExtStream(RelsExt relsExt) throws ServiceException {
        DatastreamType stream = new DatastreamType();
        stream.setID(STREAM_ID_RELS_EXT);
        stream.setCONTROLGROUP("X");
        stream.setVERSIONABLE(false);
        stream.setSTATE(StateType.A);

        DatastreamVersionType version = new DatastreamVersionType();
        version.setCREATED(getCurrentXMLGregorianCalendar());
        version.setFORMATURI("info:fedora/fedora-system:FedoraRELSExt-1.0");
        version.setLABEL("RDF Statements about this object");
        version.setMIMETYPE("application/rdf+xml");
        version.setID(STREAM_ID_RELS_EXT + STREAM_VERSION_SUFFIX);

        XmlContentType xmlContent = new XmlContentType();
        version.setXmlContent(xmlContent);

        Document document = docBuilder.newDocument();

        Element root = document.createElementNS(NS_RDF, "rdf:RDF");
        
        root.setAttribute("xmlns:fedora-model", NS_FEDORA);
        root.setAttribute("xmlns:" + CUSTOM_MODEL_PREFIX, NS_KRAMERIUS);
        root.setAttribute("xmlns:oai", NS_OAI);

        Element description = this.appendChildNS(document, root,NS_RDF, "rdf:Description", "");
        description.setAttributeNS(NS_RDF, "rdf:about", "info:fedora/" + relsExt.getPid());

        String modelPrefix;
        String relNs;
        for (RelsExt.Relation rel : relsExt.getRelations()) {
            if (RelsExt.HAS_MODEL.equals(rel.getKey())){
                modelPrefix =  "fedora-model";
                relNs = NS_FEDORA;
            }else if(RelsExt.ITEM_ID.equals(rel.getKey())){
                modelPrefix =  "oai";
                relNs = NS_OAI;
            }else{
                modelPrefix = CUSTOM_MODEL_PREFIX;
                relNs = NS_KRAMERIUS;
            }
            Element relElement = this.appendChildNS(document, description,relNs, modelPrefix + ":" + rel.getKey(), rel.isLiteral() ? rel.getId() : "");
            if (!rel.isLiteral()) {
                relElement.setAttributeNS(NS_RDF,"rdf:resource", "info:fedora/" + rel.getId());
            }
        }

        xmlContent.getAny().add(root);

        stream.getDatastreamVersion().add(version);
        return stream;
    }

    private XMLGregorianCalendar getCurrentXMLGregorianCalendar() {
        Calendar now = Calendar.getInstance();
        return XMLGregorianCalendarImpl.createDateTime(now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.HOUR_OF_DAY), now
                .get(Calendar.MINUTE), now.get(Calendar.SECOND));
    }

    protected String trimNull(String s) {
        return (s == null) ? "" : s;
    }

    protected String first(List<? extends Object> list) {
        return (list == null || list.size()==0 || list.get(0) == null) ? StringUtils.EMPTY : list.get(0).toString();
    }

    protected <T> T firstItem(List<T> list) {
        return (list == null || list.size()==0 || list.get(0) == null) ? null : list.get(0);
    }
    
    protected String concat(List<String> list) {
        if (list == null) return null;
        StringBuffer sb = new StringBuffer();
        for (String st : list){
            if (st != null){
                sb.append(st);
            }
        }
        return sb.length()>0?sb.toString():null;
    }

    /**
     * @param re
     * @param piFrom
     * @param piTo
     * @param pageIdMap
     */
    protected void processPageIndex(RelsExt re, Integer piFrom, Integer piTo, Map<String, String> pageIdMap) {
        for (Map.Entry<String, String> e : pageIdMap.entrySet()) {
            String pageNumberStr = e.getKey().replaceAll("[^0-9]", "");
            if (NumberUtils.isDigits(pageNumberStr)) {
                Integer pageNumber = Integer.valueOf(pageNumberStr);
                if (pageNumber.compareTo(piFrom) >= 0 && pageNumber.compareTo(piTo) <= 0) {
                    re.addRelation(RelsExt.IS_ON_PAGE, e.getValue(), false);
                }
            }
        }
    }

    protected String generateUUID() throws ServiceException {
        String uuid = UUIDManager.generateUUID().toString();
        if (log.isDebugEnabled()) {
            log.debug("Generated new UUID: " + uuid);
        }
        return uuid;
    }

    public ConvertorConfig getConfig() {
        return config;
    }

    public int getObjectCounter() {
        return objectCounter;
    }

    /**
     * extracts filename from filename with or without sigla
     * 
     * @param siglaName
     * @param removeDefaultOnly
     * @return
     */
    protected String removeSigla(String siglaName) {
        String fileName = siglaName;
        if (siglaName.matches("^[a-zA-Z]{3}\\d{3}_.*")) {
            fileName = siglaName.substring(7);
        }
        return fileName;
    }

    /**
     * Vytvori digitalni objekt dle zadanych parametru vcetne datastreamu
     * 
     * @param sourceObject
     * @param pid
     * @param title
     * @param creator
     * @param publisher
     * @param contributor
     * @param re
     * @param xslFile
     * @param files
     * @param foxmlObject
     * @throws ServiceException
     */
    protected DigitalObject createDigitalObject(Object sourceObject, String pid, String title, DublinCore dc, RelsExt re, String xslFile, ImageRepresentation[] files,
            boolean isPublic) throws ServiceException {

        if (log.isInfoEnabled()) {
            log.info(sourceObject.getClass().getSimpleName() + ": title=" + title + "; pid=" + pid);
        }

        
        
        DigitalObject foxmlObject = new DigitalObject();

        this.setCommonProperties(foxmlObject, pid, title);

        String policyID = isPublic ? POLICY_PUBLIC : POLICY_PRIVATE;
        dc.setRights(policyID);
        re.addRelation(RelsExt.POLICY, policyID, true);

        this.setCommonStreams(foxmlObject, sourceObject, dc, re, xslFile, policyID, files);

        return foxmlObject;
    }

    protected boolean isPublic(String pid, boolean parentPublic, String tableName) {
        boolean retval = parentPublic;

        Connection con = config.getDbConnection();
        if (con != null) {
            int oldPublic = PFLAG_PRIVATE;
            Statement st = null;
            ResultSet rs = null;
            try {
                st = con.createStatement();
                rs = st.executeQuery("select cc.publicflag from " + tableName
                        + " t left outer join customizablecomponent cc on t.id_cc = cc.id where t.ui_uniqueidentifierurntype = \'" + pid + "\'");
                if (rs.next()) {
                    oldPublic = rs.getInt(1);
                }
            } catch (SQLException ex) {
                log.error("Error in reading visibility",ex );
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (st != null) {
                        st.close();
                    }
                    // con.close(); connection will be closed in the Main class
                } catch (SQLException eex) {

                }
            }
            if (oldPublic != PFLAG_INHERIT) {
                retval = (oldPublic == PFLAG_PUBLIC);
            }
        }
        return retval;
    }

    protected void convertHandle(String pid, DublinCore dc, RelsExt re) {
        String handle = null;

        Connection con = config.getDbConnection();
        if (con != null) {
            Statement st = null;
            ResultSet rs = null;
            try {
                st = con.createStatement();
                rs = st.executeQuery("select handle from handle where resourceuuid = \'" + pid + "\'");
                if (rs.next()) {
                    handle = rs.getString(1);
                }
            } catch (SQLException ex) {
                log.error("Error in reading handle",ex );
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (st != null) {
                        st.close();
                    }
                    // con.close(); connection will be closed in the Main class
                } catch (SQLException eex) {

                }
            }
        }
        dc.addQualifiedIdentifier(RelsExt.HANDLE, handle);
        re.addRelation(RelsExt.HANDLE, handle, true);
    }
    
    protected String pid(String uuid){
        return PID_PREFIX + uuid;
    }

}
