package cz.incad.kramerius.indexer;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.FedoraNamespaceContext;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.utils.DCUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Alberto
 * Handles and manages the fields not cirectly present in doc FOXML
 * 
 * <xsl:param name="PAGESCOUNT" select="1"/>
<xsl:param name="PAGENUM" select="0"/>

<xsl:param name="PARENT_TITLE" select="''"/>
<xsl:param name="PARENT_PID" select="''"/>
<xsl:param name="PARENT_MODEL" select="''"/>
<xsl:param name="PATH" select="''"/>
<xsl:param name="PID_PATH" select="''"/>
<xsl:param name="ROOT_TITLE" select="''"/>
<xsl:param name="ROOT_MODEL" select="''"/>
<xsl:param name="ROOT_PID" select="''"/>
<xsl:param name="LANGUAGE" select="''"/>
<xsl:param name="LEVEL" select="''"/>
<xsl:param name="RELS_EXT_INDEX" select="1"/>
<xsl:param name="PARENTS" select="''"/>
 */
public class ExtendedFields {

    private static final Logger logger = Logger.getLogger(ExtendedFields.class.getName());
    private String root_title;
    private int relsExtIndex;
    private ArrayList<String> pid_paths;
    private ArrayList<String> model_paths;
    private FedoraOperations fo;
    FedoraAccess fa;
    HashMap<String, String> models_cache;
    HashMap<String, String> dates_cache;
    HashMap<String, String> root_title_cache;
    String datum;
    String rok;
    String datum_begin;
    String datum_end;
    String xPathStr;
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    XPathExpression expr;
    private final String prefix = "//mods:mods/";

    public ExtendedFields(FedoraOperations fo) throws IOException {
        this.fo = fo;
        this.fa = new FedoraAccessImpl(KConfiguration.getInstance());
        models_cache = new HashMap<String, String>();
        dates_cache = new HashMap<String, String>();
        root_title_cache = new HashMap<String, String>();
        xpath.setNamespaceContext(new FedoraNamespaceContext());
    }

    public void setFields(String pid) throws Exception {
        pid_paths = new ArrayList<String>();
        pid_paths = fo.getPidPaths(pid);
        relsExtIndex = fo.getRelsIndex(pid);
        model_paths = new ArrayList<String>();
        for (String s : pid_paths) {
            model_paths.add(getModelPath(s));
        }
        setRootTitle();
        setDate();
    }

    private String getModelPath(String pid_path) throws IOException {
        String[] pids = pid_path.split("/");
        StringBuilder model_path = new StringBuilder();

        String model;
        for (String s : pids) {
            if (models_cache.containsKey(s)) {
                model_path.append(models_cache.get(s)).append("/");
            } else {
                model = fa.getKrameriusModelName(s);
                model_path.append(model).append("/");
                models_cache.put(s, model);
            }
        }
        return model_path.deleteCharAt(model_path.length()-1).toString();
    }

    public HashMap<String, String> toArray() {
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        return paramsMap;
    }

    public String toXmlString(int pageNum) {
        StringBuilder sb = new StringBuilder();
        for (String s : pid_paths) {
            sb.append("<field name=\"pid_path\">").append(s).append(pageNum == 0 ? "" : "/@" + pageNum).append("</field>");
            String[] pids = s.split("/");
            if(pids.length==1){
                sb.append("<field name=\"parent_pid\">").append(pids[0]).append("</field>");
            }else{
                sb.append("<field name=\"parent_pid\">").append(pids[pids.length - 2]).append("</field>");
            }
            
        }
        for (String s : model_paths) {
            sb.append("<field name=\"model_path\">").append(s).append("</field>");
        }
        int level = pid_paths.get(0).split("/").length - 1;
        if(pageNum != 0) level++;
        sb.append("<field name=\"rels_ext_index\">").append(relsExtIndex).append("</field>");
        sb.append("<field name=\"root_title\">").append(root_title).append("</field>");
        sb.append("<field name=\"root_pid\">").append(pid_paths.get(0).split("/")[0]).append("</field>");
        sb.append("<field name=\"level\">").append(level).append("</field>");
        sb.append("<field name=\"datum\">").append(datum).append("</field>");
        if (!rok.equals("")) {
            sb.append("<field name=\"rok\">").append(rok).append("</field>");
        }
        if (!datum_begin.equals("")) {
            sb.append("<field name=\"datum_begin\">").append(datum_begin).append("</field>");
        }
        if (!datum_end.equals("")) {
            sb.append("<field name=\"datum_end\">").append(datum_end).append("</field>");
        }
        return sb.toString();
    }

    private void setRootTitle() throws Exception {
        String root_pid = pid_paths.get(0).split("/")[0];
        if(root_title_cache.containsKey(root_pid)){
            root_title = root_title_cache.get(root_pid);
        }else{
            Document doc = fa.getDC(root_pid);
            root_title =  DCUtils.titleFromDC(doc);
            root_title_cache.put(root_pid, root_title);
        }
    }

    private void setDate() throws Exception {
        datum = "";
        rok = "";
        datum_begin = "";
        datum_end = "";
        for (int j = 0; j < pid_paths.size(); j++) {
            String[] pid_path = pid_paths.get(j).split("/");
            for (int i = pid_path.length - 1; i > -1; i--) {
                String pid = pid_path[i];
                Document foxml = fa.getBiblioMods(pid);
                if (dates_cache.containsKey(pid)) {
                    datum = dates_cache.get(pid);
                    parseDatum(datum);
                    return;
                }  
                xPathStr = prefix + "mods:part/mods:date/text()";
                expr = xpath.compile(xPathStr);
                Node node = (Node) expr.evaluate(foxml, XPathConstants.NODE);

                if (node != null) {
                    datum = node.getNodeValue();
                    parseDatum(datum);
                    dates_cache.put(pid, datum);
                    return;
                } else {
                    xPathStr = prefix + "mods:originInfo[@transliteration='publisher']/mods:dateIssued/text()";
                    expr = xpath.compile(xPathStr);
                    node = (Node) expr.evaluate(foxml, XPathConstants.NODE);
                    if (node != null) {
                        datum = node.getNodeValue();
                        parseDatum(datum);
                        dates_cache.put(pid, datum);
                        return;
                    }
                }
            }
        }
        
    }

    private void parseDatum(String datumStr) {
        Integer dataInt;
        try {
            dataInt = Integer.parseInt(datumStr);
            rok = datumStr;
        } catch (NumberFormatException ex) {
        }
        //Datum muze byt typu 1906 - 1945
        if (datumStr.contains("-")) {

            try {
                String begin = datumStr.split("-")[0].trim();
                String end = datumStr.split("-")[1].trim();
                dataInt = Integer.parseInt(begin);
                dataInt = Integer.parseInt(end);
                datum_begin = begin;
                datum_end = end;
            } catch (Exception ex) {
            }
        }

        //Datum je typu dd.mm.yyyy
        try {
            DateFormat formatter = new SimpleDateFormat("dd.mm.yyyy");
            DateFormat outformatter = new SimpleDateFormat("yyyy");
            Date dateValue = formatter.parse(datumStr);

            rok = outformatter.format(dateValue);
        } catch (Exception e) {
        }
    }
}
