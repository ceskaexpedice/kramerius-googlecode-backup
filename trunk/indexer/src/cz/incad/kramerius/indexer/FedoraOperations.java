package cz.incad.kramerius.indexer;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.FedoraNamespaces;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.resourceindex.IResourceIndex;
import cz.incad.kramerius.resourceindex.ResourceIndexService;
import cz.incad.kramerius.utils.XMLUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;
import dk.defxws.fedoragsearch.server.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fedora.api.FedoraAPIA;
import org.fedora.api.MIMETypedStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FedoraOperations {

    private static final Logger logger =
            Logger.getLogger(FedoraOperations.class.getName());
    //protected String fgsUserName;
    protected String indexName_;
    public byte[] foxmlRecord;
    protected String dsID;
    protected byte[] ds;
    protected String dsText;
    protected String[] params = null;
    String foxmlFormat;
    FedoraAccess fa;
    IResourceIndex rindex;

    public FedoraOperations() throws Exception {
        fa = new FedoraAccessImpl(KConfiguration.getInstance());
        foxmlFormat = KConfiguration.getInstance().getConfiguration().getString("FOXMLFormat");
    }

    public void updateIndex(String action, String value, ArrayList<String> requestParams) throws java.rmi.RemoteException, Exception {
        logger.log(Level.INFO, "updateIndex action={0} value={1}", new Object[]{action, value});

        SolrOperations ops = new SolrOperations(this);
        ops.updateIndex(action, value, requestParams);
    }

    public byte[] getAndReturnFoxmlFromPid(String pid) throws java.rmi.RemoteException, Exception {
        logger.log(Level.FINE, "getAndReturnFoxmlFromPid pid={0}", pid);

        try {
            return fa.getAPIM().export(pid, foxmlFormat, "public");
        } catch (Exception e) {
            throw new Exception("Fedora Object " + pid + " not found. ", e);
        }
    }

    public void getFoxmlFromPid(String pid) throws java.rmi.RemoteException, Exception {

        logger.log(Level.INFO, "getFoxmlFromPid pid={0}", pid);

        try {
            foxmlRecord = fa.getAPIM().export(pid, foxmlFormat, "public");

        } catch (Exception e) {
            throw new Exception("Fedora Object " + pid + " not found. ", e);
        }
    }

    public int getPdfPagesCount(String pid, String dsId) throws Exception {
        ds = null;
        if (dsId != null) {
            try {
                FedoraAPIA apia = fa.getAPIA();
                MIMETypedStream mts = apia.getDatastreamDissemination(pid,
                        dsId, null);
                if (mts == null) {
                    return 1;
                }
                ds = mts.getStream();
                return (new TransformerToText().getPdfPagesCount(ds) + 1);

            } catch (Exception e) {
                throw new Exception(e.getClass().getName() + ": " + e.toString());
            }
        }
        return 1;
    }

    private List<String> getTreePredicates() {
        return Arrays.asList(KConfiguration.getInstance().getPropertyList("fedora.treePredicates"));
    }
    
    public int getRelsIndex(String pid) throws Exception {
        ArrayList<String> p = getParentsArray(pid);
        String uuid;
        int relsindex = 0;
        if (!p.isEmpty()) {
            for (String s : p) {
                Document relsExt = fa.getRelsExt(s);
                Element descEl = XMLUtils.findElement(relsExt.getDocumentElement(), "Description", FedoraNamespaces.RDF_NAMESPACE_URI);
                List<Element> els = XMLUtils.getElements(descEl);
                int i = 0;
                for (Element el : els) {
                    if (getTreePredicates().contains(el.getLocalName())) {
                        if (el.hasAttribute("rdf:resource")) {
                            uuid = el.getAttributes().getNamedItem("rdf:resource").getNodeValue().split("uuid:")[1];
                            if(uuid.equals(pid)) relsindex = Math.max(relsindex, i);
                            i++;
                        }
                    }
                }
            }
        } else {
            //pid_paths.add(old.get(i));
        }
        return relsindex;
    }

    public ArrayList<String> getPidPaths(String pid) {
        logger.info("getPidPaths");
        ArrayList<String> pid_paths = new ArrayList<String>();
        pid_paths.add(pid);
        getPidPaths(pid_paths);
        for (String s : pid_paths) {
            logger.info(s);
        }
        return pid_paths;
    }

    private void getPidPaths(ArrayList<String> pid_paths) {
        String first;
        boolean changed = false;
        ArrayList<String> old = new ArrayList<String>(pid_paths);
        pid_paths.clear();
        for (int i = 0; i < old.size(); i++) {
            first = old.get(i).split("/")[0];
            ArrayList<String> p = getParentsArray(first);
            if (!p.isEmpty()) {
                changed = true;
                for (String s : p) {
                    pid_paths.add(s + "/" + old.get(i));
                }
            } else {
                pid_paths.add(old.get(i));
            }
        }
        if (changed) {
            getPidPaths(pid_paths);
        }
    }

    public String getParents(String pid) {
        ArrayList<String> l = getParentsArray(pid);
        StringBuilder sb = new StringBuilder();
        for (String s : l) {
            sb.append(s).append(";");
        }
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    public ArrayList<String> getParentsArray(String pid) {
        try {

            //logger.info("getParents: " + pid);
            if (rindex == null) {
                rindex = ResourceIndexService.getResourceIndexImpl();
            }
            return rindex.getParentsPids(pid);

        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private String removeDiacritic(String old) {
        char[] o = {'á', 'à', 'č', 'ď', 'ě', 'é', 'í', 'ľ', 'ň', 'ó', 'ř', 'r', 'š', 'ť', 'ů', 'ú', 'u', 'u', 'ý', 'ž', 'Á', 'À', 'Č', 'Ď', 'É', 'Ě', 'Í', 'Ĺ', 'Ň', 'Ó', 'Ř', 'Š', 'Ť', 'Ú', 'Ů', 'Ý', 'Ž'};
        char[] n = {'a', 'a', 'c', 'd', 'e', 'e', 'i', 'l', 'n', 'o', 'r', 'r', 's', 't', 'u', 'u', 'u', 'u', 'y', 'z', 'A', 'A', 'C', 'D', 'E', 'E', 'I', 'L', 'N', 'O', 'R', 'S', 'T', 'U', 'U', 'Y', 'Z'};

        String newStr = old;
        for (int i = 0; i < o.length; i++) {
            newStr = newStr.replace(o[i], n[i]);
        }
        newStr = newStr.replace(" ", "");
        return newStr;
    }

    public String prepareCzech(String s) throws Exception {
        return removeDiacritic(s).toLowerCase().replace("ch", "hz");
    }

    public String getDatastreamText(String pid, String dsId, String pageNum) throws Exception {

        StringBuffer dsBuffer = new StringBuffer();
        String mimetype = "";
        ds = null;
        if (dsId != null) {
            try {
                FedoraAPIA apia = fa.getAPIA();
                MIMETypedStream mts = apia.getDatastreamDissemination(pid,
                        dsId, null);
                if (mts == null) {
                    return "";
                }
                ds = mts.getStream();
                mimetype = mts.getMIMEType();
            } catch (Exception e) {
                throw new Exception(e.getClass().getName() + ": " + e.toString());
            }
        }
        if (ds != null) {
            dsBuffer = (new TransformerToText().getText(ds, mimetype, pageNum));
        } else {
            logger.fine("ds is null");
        }
        logger.log(Level.FINE,
                "getDatastreamText  pid={0} dsId={1} mimetype={2} dsBuffer={3}",
                new Object[]{pid, dsId, mimetype, dsBuffer.toString()});
        return dsBuffer.toString();
    }
}
