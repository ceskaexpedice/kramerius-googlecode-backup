package cz.incad.Kramerius;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

import cz.incad.Kramerius.backend.guice.GuiceServlet;
import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.SolrAccess;
import cz.incad.kramerius.intconfig.InternalConfiguration;
import cz.incad.kramerius.utils.ApplicationURL;
import cz.incad.kramerius.utils.RESTHelper;
import cz.incad.kramerius.utils.XMLUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.utils.pid.LexerException;
import cz.incad.kramerius.utils.pid.PIDParser;
import cz.incad.kramerius.utils.solr.SolrUtils;

/**
 * This is support for persistent URL
 * @author pavels
 */
public class HandleServlet extends GuiceServlet {

	private static final long serialVersionUID = 1L;

	public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(HandleServlet.class.getName());
	
	@Inject
	transient KConfiguration kConfiguration;

	@Inject
	SolrAccess solrAccess;
	
	
	@Override
    public void init() throws ServletException {
        super.init();
    }



    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }



    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String pidPath = null;
			String pid = null;
			String path = null;
			
			String requestURL = req.getRequestURL().toString();
			String handle = disectHandle(requestURL);
			Document parseDocument = HandleType.createType(handle).dataFromSolr(handle, solrAccess);
		    
			pidPath = SolrUtils.disectPidPath(parseDocument);
		    pid = SolrUtils.disectPid(parseDocument);
		    path = SolrUtils.disectPath(parseDocument);
		    String applicationCotext = ApplicationURL.applicationContextPath(req);
		    String redirectUrl=  "/"+applicationCotext+"/item.jsp?pid="+pid+"&pid_path="+pidPath+"&path="+path;
		    resp.sendRedirect(redirectUrl);
		    
		} catch (XPathExpressionException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);;
			resp.sendError(500);
		}
	}



    public static String disectHandle(String requestURL) {
		//"dvju"
		try {
			StringBuffer buffer = new StringBuffer();
			URL url = new URL(requestURL);
			String path = url.getPath();
			String application = path;
			StringTokenizer tokenizer = new StringTokenizer(path,"/");
			if (tokenizer.hasMoreTokens()) application = tokenizer.nextToken();
			String handleServlet = path;
			if (tokenizer.hasMoreTokens()) handleServlet = tokenizer.nextToken();
			// check handle servlet
			while(tokenizer.hasMoreTokens()) {
				buffer.append(tokenizer.nextElement());
				if (tokenizer.hasMoreTokens()) buffer.append("/");
			}
			return buffer.toString();
		} catch (MalformedURLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return "<no handle>";
		}
		
	}
	
	enum HandleType {
		UUID {
            @Override
            Document dataFromSolr(String pid, SolrAccess solrAccess) throws IOException {
                try {
                    PIDParser parser = new PIDParser(pid);
                    parser.objectPid();
                    return solrAccess.getSolrDataDocumentByUUID(parser.getObjectId());
                } catch (LexerException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(),e);
                    return solrAccess.getSolrDataDocumentByUUID(pid);
                }
            }
		},
		KRAMERIUS3{
            @Override
            Document dataFromSolr(String handle, SolrAccess solrAccess) throws IOException {
                return solrAccess.getSolrDataDocumentByHandle(handle);
            }
		};		
	
		//abstract String construct(String handle);
		abstract Document dataFromSolr(String handle, SolrAccess solrAccess) throws IOException;
		
		public static HandleType createType(String handle) {
			if (handle.toLowerCase().startsWith("uuid:")) {
				return UUID;
			} else {
				return KRAMERIUS3;
			}
		}
	}
	
}
