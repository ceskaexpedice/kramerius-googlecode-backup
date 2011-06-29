package org.kramerius;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.qbizm.kramerius.imptool.poc.Main;
import com.qbizm.kramerius.imptool.poc.valueobj.ServiceException;

import cz.incad.kramerius.service.impl.IndexerProcessStarter;
import cz.incad.kramerius.utils.IOUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;

public class Download {



    /**
     * @param args
     */
    public static void main(String[] args) {
        replicatePeriodicals();

    }

    public static void replicateMonographs(){
        initLogfiles();
        Download download = new Download();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(KConfiguration.getInstance().getProperty("migration.monographs"))));
        } catch (FileNotFoundException e) {
            log.severe("Monographs file list not found: "+e);
            return;
        }
        try {
            for (String line; (line = reader.readLine()) != null;) {
                if ("".equals(line)) continue;
                log.info("Downloading "+line);
                Replication rep = Download.createReplication(DocType.MONOID, line, null);
                processReplication( download,  rep);
            }
            reader.close();
        } catch (IOException e) {
            log.severe("Exception reading document list file: " + e);
            throw new RuntimeException(e);
        }
        closeLogfiles();
    }



    public static void replicatePeriodicals(){
        initLogfiles();
        Download download = new Download();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(KConfiguration.getInstance().getProperty("migration.periodicals"))));
        } catch (FileNotFoundException e) {
            log.severe("Periodicals file list not found: "+e);
            return;
        }
        try {
            for (String line; (line = reader.readLine()) != null;) {
                if ("".equals(line)) continue;
                log.info("Downloading "+line);
                StringTokenizer st = new StringTokenizer(line,";");
                String id = st.nextToken();
                String volume = st.nextToken();
                Replication rep = Download.createReplication(DocType.ISSN, id,volume);
                processReplication( download,  rep);
            }
            reader.close();
        } catch (IOException e) {
            log.severe("Exception reading document list file: " + e);
            throw new RuntimeException(e);
        }
        closeLogfiles();
    }

    private static void processReplication(Download download, Replication rep){
        try{
            download.replicateAll(rep);
            boolean visible = Boolean.parseBoolean(KConfiguration.getInstance().getProperty("convert.defaultRights","false"));
            String uuid = Main.convert(KConfiguration.getInstance().getProperty("migration.directory"), KConfiguration.getInstance().getProperty("migration.target.directory"), true, visible, rep.getID());
            Import.ingest(KConfiguration.getInstance().getProperty("ingest.url"), KConfiguration.getInstance().getProperty("ingest.user"), KConfiguration.getInstance().getProperty("ingest.password"), KConfiguration.getInstance().getProperty("migration.target.directory"));
            logSuccess(rep.getID(), uuid);
            if (!KConfiguration.getInstance().getConfiguration().getBoolean("ingest.skip",false)){
                startIndexing(rep.getID(), uuid);
            }
        }catch (Exception t){
            if (rep!=null){
                logFailed(rep.getID(), t);
            }
        }
    }


    private static Writer successWriter;
    private static Writer failedWriter;

    private static void initLogfiles(){
        try {
            successWriter = new FileWriter("replication-success.txt");
            failedWriter = new FileWriter("replication-failed.txt");
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void logSuccess(String ID, String uuid){
        try {
            successWriter.append(ID+"\t"+uuid);
            successWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void logFailed(String ID, Throwable t){
        try {
            failedWriter.append(ID+"\t"+t+"\n");
            failedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void closeLogfiles(){
        try {
            successWriter.close();
            failedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Logger log = Logger.getLogger(Download.class.getName());

    /**
     * Start indexing of the document through the LRprocesses rest api
     * @param title
     * @param processedPath
     */
    public static void startIndexing(String title, String processedPath){
        if (processedPath == null)
            return;
        int uuidStart = processedPath.indexOf("&pid_path=")+10;
        int uuidEnd = processedPath.indexOf("/",uuidStart);
        if (uuidEnd == -1){
            uuidEnd = processedPath.indexOf("&path=",uuidStart);
        }
        String uuid = processedPath.substring(uuidStart, uuidEnd);
        IndexerProcessStarter.spawnIndexer(false, title, uuid);
    }

    /** buffer size used when data from remote connection are written to disc */
    private static final int bufferSize = 4096;

    /** replication directory (from configuration) */
    private final String replicationDirectoryName ;

    public static final String CONV_SUFFIX = "-converted";

    /** temporary file for metadata replication */
    private static String replicationMetadataFileName = "metadata.xml";

    /** temporary file for document names listing */
    private static String replicationDocumentListFileName = "documents.txt";


    public static enum DocType {
        ISSN, ISBN, MONOID
    }

    public Download(){
        replicationDirectoryName = KConfiguration.getInstance().getProperty("migration.directory");
    }

    static Replication createReplication(DocType doctype, String docId, String volumeId) {
        Replication repl = new Replication(docId+(volumeId==null?"":(":"+volumeId)));

        repl.setMode(3);
        repl.setRightsRestriction(1);
        repl.setOverwriteDocuments(false);

        Institution inst = new Institution();
        inst.setSigla(KConfiguration.getInstance().getProperty("k3.replication.sigla"));
        inst.setReplicationOutLogin(KConfiguration.getInstance().getProperty("k3.replication.login"));
        inst.setReplicationOutPassword(KConfiguration.getInstance().getProperty("k3.replication.password"));
        inst.setReplicationURL(KConfiguration.getInstance().getProperty("k3.replication.url"));
        repl.setInstitution(inst);

        switch (doctype) {
        case ISSN:
            repl.setIssn(docId);
            repl.setDtd(Replication.DTD_PERIODICAL);
            repl.setPeriodicalVolumeDate(volumeId);
            break;
        case ISBN:
            repl.setIsbn(docId);
            repl.setDtd(Replication.DTD_MONOGRAPH);
            break;
        case MONOID:
            try {
                repl.setMonographId(Long.decode(docId));
            } catch (NumberFormatException e) {
                log.log(Level.SEVERE, "Wrong monograph ID:", e);
            }
            repl.setDtd(Replication.DTD_MONOGRAPH);
            break;
        }

        repl.setCreationTime(new Timestamp(System.currentTimeMillis()));

        return repl;
    }



    public void replicateAll(Replication repl) {
        clearReplicationDirectory();
        saveRemoteMetadata(repl);
        saveRemoteDocuments(repl);

        // logReplication(repl, imp.buildLog());

    }

    private void saveRemoteMetadata(Replication repl) {
        ReplicationURL fromURL = new ReplicationURL(repl, ReplicationURL.ACTION_METADATA);
        log.fine("saveRemoteMetadata URL: " + fromURL);
        File toFile = new File(replicationDirectoryName, replicationMetadataFileName);
        copyRemote(fromURL.toString(), toFile);
    }

    /**
     * Get documents from remote system and save them into temporary files.
     *
     * @param repl
     * @throws ServiceException
     * @throws RemoteException
     */
    private void saveRemoteDocuments(Replication repl) {
        ReplicationURL fromURL = new ReplicationURL(repl, ReplicationURL.ACTION_DOCUMENT_LIST);
        log.fine("documentList URL: " + fromURL);
        File docListFile = new File(replicationDirectoryName, replicationDocumentListFileName);
        copyRemote(fromURL.toString(), docListFile);

        fromURL = new ReplicationURL(repl, ReplicationURL.ACTION_DOCUMENT);
        Map documents = getDocuments(docListFile);
        Set names = documents.keySet();

        String last_replicated_name = null;
        String last_replicated_id = null;

        for (Iterator it = names.iterator(); it.hasNext();) {
            String name = (String) it.next();
            String id = (String) documents.get(name);
            log.fine("Get remote document ...  id: " + id + ", name: " + name);
            File toFile = new File(replicationDirectoryName, name);
            // id in URL is required!! (without it sometimes is ok, sometimes
            // not, depending on first id
            // selected for name in RepositoryService.getDocumentFile)
            if (!toFile.exists()) {
                if (last_replicated_name != null) {
                    copyRemote(fromURL.toString(last_replicated_name, last_replicated_id), new File(replicationDirectoryName, last_replicated_name));
                    last_replicated_name = null;
                    last_replicated_id = null;
                }
                copyRemote(fromURL.toString(name, id), toFile);
            } else {
                last_replicated_name = name;
                last_replicated_id = id;
            }
        }
        if (last_replicated_name != null) {
            copyRemote(fromURL.toString(last_replicated_name, last_replicated_id), new File(replicationDirectoryName, last_replicated_name));
            last_replicated_name = null;
            last_replicated_id = null;
        }
        docListFile.delete();
    }

    /**
     * Get document ids and names from documentListFile and save them in Map
     * (names as keys, ids as values). Each file line contains info about one
     * document: "id name"
     *
     * @param documentListFile
     * @return
     * @throws ServiceException
     */
    private Map getDocuments(File documentListFile) {
        Map documents = new LinkedHashMap();
        try {
            BufferedReader in = new BufferedReader(new FileReader(documentListFile));
            for (String line; (line = in.readLine()) != null;) {
                // log.debug("line: " + line);
                StringTokenizer st = new StringTokenizer(line);
                String id = st.nextToken();
                // log.debug("id: " + id);
                String name = st.nextToken();
                // log.debug("name" + name);
                documents.put(name, id);
            }
            in.close();
        } catch (IOException e) {
            log.severe("Exception reading document list file: " + e);
            throw new RuntimeException(e);
        }
        return documents;
    }

    private void copyRemote(String fromURL, File toFileName) {
        OutputStream os = null;
        InputStream is = null;
        try {
            is = getRemoteInputStream(fromURL);
            os = new FileOutputStream(toFileName);
            byte[] buf = new byte[bufferSize];
            for (int byteRead; (byteRead = is.read(buf, 0, bufferSize)) >= 0;) {
                os.write(buf, 0, byteRead);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            log.severe("IOException in copyRemote: " + e);
            throw new RuntimeException(e);
        }
    }

    static InputStream getRemoteInputStream(String fromURL)  {
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(fromURL);
            conn = (HttpURLConnection) url.openConnection();

            if (conn instanceof HttpsURLConnection) {
                SSLContext sc = SSLContext.getInstance("SSL");
                TrustManager[] tm = { new MyX509TrustManager() };
                sc.init(null, tm, new java.security.SecureRandom());

                HttpsURLConnection connSec = (HttpsURLConnection) conn;
                connSec.setSSLSocketFactory(sc.getSocketFactory());
                connSec.setHostnameVerifier(new MyHostnameVerifier());
            }
            if (conn.getResponseCode() == 200) {
                if (!conn.getContentType().startsWith("text/html")) {
                    is = new BufferedInputStream(conn.getInputStream());
                } else {
                    Reader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String remoteErrorMessage = "";
                    char[] buf = new char[bufferSize];
                    for (; r.read(buf, 0, bufferSize) >= 0;)
                        remoteErrorMessage += new String(buf);
                    log.severe("Bad content type text/html in getRemoteInputStream()");
                    log.severe(remoteErrorMessage);
                    throw new RuntimeException("Some URL parameter missing or wrong or no access rights ...");
                }
            } else {
                log.fine("Remote document not found: " + fromURL);
                throw new FileNotFoundException("Remote document not found: " + fromURL);
            }
        } catch (NoSuchAlgorithmException e) {
            if (conn != null)
                conn.disconnect();
            log.severe("NoSuchAlgorithmException in getRemoteInputStream: " + e);
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            if (conn != null)
                conn.disconnect();
            log.severe("KeyManagementException in getRemoteInputStream: " + e);
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            if (conn != null)
                conn.disconnect();
            throw new RuntimeException (e);
        } catch (IOException e) {
            if (conn != null)
                conn.disconnect();
            log.severe("IOException in getRemoteInputStream: " + e);
            throw new RuntimeException(e);
        }

        return is;
    }

    private static class MyX509TrustManager implements X509TrustManager {

        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String str) {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String str) {
        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private static class MyHostnameVerifier implements HostnameVerifier {

        public boolean verify(String urlHostname, SSLSession session) {
            // return
            // java.net.InetAddress.getByName(urlHostname).equals(java.net.InetAddress.getByName(certHostname));
            return true;
        }
    }

    void clearReplicationDirectory() {
        File replicationDirectory = IOUtils.checkDirectory(replicationDirectoryName);
        IOUtils.cleanDirectory(replicationDirectory);
    }

    static class ReplicationURL {

        public static final String ACTION_METADATA = "metadata";

        public static final String ACTION_DOCUMENT_LIST = "documentList";

        public static final String ACTION_DOCUMENT = "document";

        public static final String ACTION_REPLICATION_INFO = "replicationInfo";

        public static final String ACTION_PERIODICAL_VOLUME_LIST = "periodicalVolumeList";

        private String baseURL;

        private String sigla;

        private String login;

        private String pwd;

        private String action;

        private int mode;

        private String issn;

        private String isbn;

        private String monographId;

        private String volume;

        private String item;

        private int rights;

        public ReplicationURL(Replication repl, String action) {
            this.baseURL = repl.getInstitution().getReplicationURL();
            this.sigla = repl.getInstitution().getSigla();
            if (!this.baseURL.endsWith("/"))
                this.baseURL += "/";
            this.login = repl.getInstitution().getReplicationOutLogin();
            this.pwd = repl.getInstitution().getReplicationOutPassword();
            this.action = action;
            this.mode = repl.getMode();
            this.issn = repl.getIssn() == null ? "" : "" + repl.getIssn();
            this.isbn = repl.getIsbn() == null ? "" : "" + repl.getIsbn();
            this.monographId = repl.getMonographId() == null ? "" : "" + repl.getMonographId();
            this.volume = repl.getPeriodicalVolumeDate() == null ? "":repl.getPeriodicalVolumeDate();
            this.item = repl.getPeriodicalItemDate() == null ? "":repl.getPeriodicalItemDate();
            this.rights = repl.getRightsRestriction();
        }

        public String toString() {

            String URL = baseURL + "replicationServlet?" + "action=" + action + "&mode=" + mode
            + "&isbn=" + URLEncode(isbn)
            + "&monographId=" + URLEncode(monographId)
            + "&issn=" + URLEncode(issn)
            + "&volume=" + URLEncode(volume)
            + "&item=" + URLEncode(item)
            + "&rights=" + rights
            + "&sigla=" + URLEncode(sigla)
            + "&login=" + URLEncode(login)
            + "&pwd=" + URLEncode(pwd);

            return URL;
        }

        public String toString(String documentName, String documentId) {

            String URL = baseURL + "document/" + URLEncode(documentName) + "?id=" + URLEncode(documentId) + "&sigla=" + URLEncode(sigla) + "&login=" + URLEncode(login) + "&pwd="
                    + URLEncode(pwd);
            return URL;
        }
    }

    /**
     * Encodes string for transport within URL. UTF-8 encoding scheme is always
     * used.
     */
    static String URLEncode(String stringToEncode) {
        String encodedString = null;

        try {
            return URLEncoder.encode(stringToEncode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Encoding UTF-8 is supported
        }

        return encodedString;
    }

    public static class Replication extends DataAction {

        public Replication(String id){
            this.id = id;
        }

        public String getID(){

            return id;
        }


        public String getIssn() {
            return issn;
        }

        public void setIssn(String ISSN) {
            this.issn = ISSN;
        }

        public String getPeriodicalVolumeDate() {
            return periodicalVolumeDate;
        }

        public void setPeriodicalVolumeDate(String periodicalVolumeDate) {
            this.periodicalVolumeDate = periodicalVolumeDate;
        }

        public String getPeriodicalItemDate() {
            return periodicalItemDate;
        }

        public void setPeriodicalItemDate(String periodicalItemDate) {
            this.periodicalItemDate = periodicalItemDate;
        }

        private Institution institution;

        private String issn;

        private String isbn;

        private Long monographId;

        private String periodicalVolumeDate;

        private String periodicalItemDate;

        private long museumObjectId;

        private String id;

        /**
         * @return
         */
        public Institution getInstitution() {
            return institution;
        }

        /**
         * @param institution
         */
        public void setInstitution(Institution institution) {
            this.institution = institution;
        }

        /**
         * @return
         */
        public String getIsbn() {
            return isbn;
        }

        /**
         * @return
         */
        public Long getMonographId() {
            return monographId;
        }

        /**
         * @param string
         */
        public void setIsbn(String string) {
            isbn = string;
        }

        /**
         * @param l
         */
        public void setMonographId(Long l) {
            monographId = l;
        }

        /**
         * @return Returns the museumObjectId.
         */
        public long getMuseumObjectId() {
            return museumObjectId;
        }

        /**
         * @param museumObjectId
         *            The museumObjectId to set.
         */
        public void setMuseumObjectId(long museumObjectId) {
            this.museumObjectId = museumObjectId;
        }

    }

    public static class Institution {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSigla() {
            return sigla;
        }

        public void setSigla(String sigla) {
            this.sigla = sigla;
        }

        public String getCompetentPerson() {
            return competentPerson;
        }

        public void setCompetentPerson(String competentPerson) {
            this.competentPerson = competentPerson;
        }

        public String getCompetentPersonPhone() {
            return competentPersonPhone;
        }

        public void setCompetentPersonPhone(String competentPersonPhone) {
            this.competentPersonPhone = competentPersonPhone;
        }

        public String getCompetentPersonEmail() {
            return competentPersonEmail;
        }

        public void setCompetentPersonEmail(String competentPersonEmail) {
            this.competentPersonEmail = competentPersonEmail;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Timestamp getInsertionDate() {
            return insertionDate;
        }

        public void setInsertionDate(Timestamp insertionDate) {
            this.insertionDate = insertionDate;
        }

        public String getReplicationURL() {
            return replicationURL;
        }

        public void setReplicationURL(String replicationURL) {
            this.replicationURL = replicationURL;
        }

        public String getReplicationInLogin() {
            return replicationInLogin;
        }

        public void setReplicationInLogin(String replicationInLogin) {
            this.replicationInLogin = replicationInLogin;
        }

        public String getReplicationInPassword() {
            return replicationInPassword;
        }

        public void setReplicationInPassword(String replicationInPassword) {
            this.replicationInPassword = replicationInPassword;
        }

        public String getReplicationOutLogin() {
            return replicationOutLogin;
        }

        public void setReplicationOutLogin(String replicationOutLogin) {
            this.replicationOutLogin = replicationOutLogin;
        }

        public String getReplicationOutPassword() {
            return replicationOutPassword;
        }

        public void setReplicationOutPassword(String replicationOutPassword) {
            this.replicationOutPassword = replicationOutPassword;
        }

        private int id;
        private String sigla;
        private String competentPerson;
        private String competentPersonPhone;
        private String competentPersonEmail;
        private boolean active;
        private Timestamp insertionDate;
        private String replicationURL;
        private String replicationInLogin;
        private String replicationInPassword;
        private String replicationOutLogin;
        private String replicationOutPassword;
        private boolean replicationAllowed;
        private String description;
        private int right; // transient only
        private int rightInherited; // transient only

        /**
         * @return
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param string
         */
        public void setDescription(String string) {
            description = string;
        }

        /**
         * @return
         */
        public boolean isReplicationAllowed() {
            return replicationAllowed;
        }

        /**
         * @param b
         */
        public void setReplicationAllowed(boolean b) {
            replicationAllowed = b;
        }

        /**
         * @return
         */
        public int getRight() {
            return right;
        }

        /**
         * @return
         */
        public int getRightInherited() {
            return rightInherited;
        }

        /**
         * @param i
         */
        public void setRight(int i) {
            right = i;
        }

        /**
         * @param i
         */
        public void setRightInherited(int i) {
            rightInherited = i;
        }

    }

    public static abstract class DataAction {

        public static final Integer DTD_PERIODICAL = new Integer(1);

        public static final Integer DTD_MONOGRAPH = new Integer(2);

        public static final Integer DTD_MUSEUM_OBJECT = new Integer(3);

        public static final int MODE_METADATA_ONLY = 1;

        public static final int MODE_DOCUMENTS_ONLY = 2;

        public static final int MODE_ALL = 3;

        public static final int MODE_DESCRIPTION_METADATA_ONLY = 4;

        public static final int MODE_ADMIN_METADATA = 5;

        public static final int STATUS_QUEUED = 1;

        public static final int STATUS_PROCESSING = 2;

        public static final int STATUS_OK = 3;

        public static final int STATUS_ERROR = 4;

        public static final int STATUS_DONE_WITH_ERRORS = 5;

        /**
         * All document files export
         */
        public static final int RR_ALL = 1;

        /**
         * Only public document files export
         */
        public static final int RR_PUBLIC = 2;

        /**
         * Only private document files export
         */
        public static final int RR_PRIVATE = 3;

        protected int flags;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getMode() {
            return mode;
        }

        public void setMode(int mode) {
            this.mode = mode;
        }

        public int getRightsRestriction() {
            return rightsRestriction;
        }

        public void setRightsRestriction(int rightsRestriction) {
            this.rightsRestriction = rightsRestriction;
        }

        public boolean isOverwriteDocuments() {
            return overwriteDocuments;
        }

        public void setOverwriteDocuments(boolean overwriteDocuments) {
            this.overwriteDocuments = overwriteDocuments;
        }

        public Timestamp getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(Timestamp creationTime) {
            this.creationTime = creationTime;
        }

        public Timestamp getStartTime() {
            return startTime;
        }

        public void setStartTime(Timestamp startTime) {
            this.startTime = startTime;
        }

        public Timestamp getStopTime() {
            return stopTime;
        }

        public void setStopTime(Timestamp stopTime) {
            this.stopTime = stopTime;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        private int id;

        private String description;

        private Integer dtd;

        private StringBuffer logBuffer = new StringBuffer();

        /**
         * Mode of action, some of MODE_xx constants
         */
        private int mode;

        /**
         * Modifier of action, some of RR_xx constants
         */
        private int rightsRestriction;

        private boolean overwriteDocuments;

        private Timestamp creationTime;

        private Timestamp startTime;

        private Timestamp stopTime;

        /**
         * Status of action, some of STATUS_xx constants
         */
        private int status;

        /**
         * log records
         */
        private List logList = new ArrayList();

        /**
         * @return
         */
        public Integer getDtd() {
            return dtd;
        }

        /**
         * @param i
         */
        public void setDtd(Integer i) {
            dtd = i;
        }

        /**
         * Return list of DataActionLog.clazz instances.
         *
         * @return the List<DataActionLog>
         */
        public List getLogList() {
            return logList;
        }

        /**
         * @param logList
         *            the logList to set
         */
        public void setLogList(List logList) {
            this.logList = logList;
        }

        /**
         * @return the logBuffer
         */
        public StringBuffer getLogBuffer() {
            return logBuffer;
        }

        /**
         * Add message into log buffer
         *
         * @param Import
         *            imp
         * @param String
         *            message
         */
        public void addToLog(String message) {
            logBuffer.append(message);
            logBuffer.append("\n");
        }

        /**
         * @return the flags
         */
        public int getFlags() {
            return flags;
        }

        /**
         * @param flags
         *            the flags to set
         */
        public void setFlags(int flags) {
            this.flags = flags;
        }


    }

}
