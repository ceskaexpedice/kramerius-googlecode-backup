package cz.incad.kramerius.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import cz.incad.kramerius.utils.conf.KConfiguration;

/**
 * Helper class for determining application URL. 
 * @author pavels
 */
public class ApplicationURL {

	public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(ApplicationURL.class.getName());
	
	
	/**
	 * Returns protocol, server and port 
	 * @param request Processing request
	 * @return
	 */
	public static String getServerAndPort(HttpServletRequest request) {
        try {
            String string = request.getRequestURL().toString();
            URL url = new URL(string);
            return url.getProtocol()+"://"+url.getHost()+":"+url.getPort();
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return "<no url>";
        }
	    
	}

	/**
	 * Returns full application URL
	 * @param request HTTP Request
	 * @return full application URL
	 */
	public static String applicationURL(HttpServletRequest request) {
		try {
			String string = request.getRequestURL().toString();
			URL url = new URL(string);
			String application = applicationContextPath(url);
			
			String port = extractPort(url);
            String aURL = url.getProtocol() + "://" + url.getHost() + port + "/" + application;
			return aURL;
		} catch (MalformedURLException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return "<no url>";
		}
	}
	
	/**
	 * Extracts port from given url or return empty string (if port is < 0)
	 * @param url
	 * @return
	 */
    public static String extractPort(URL url) {
        if (url.getPort() > 0) {
            return ":"+url.getPort();
        } else return "";
    }

    
    /**
     * Returns application context from given url
     * @param url Processing url
     * @return
     */
    public static String applicationContextPath(URL url) {
        String path = url.getPath();
        String application = path;
        StringTokenizer tokenizer = new StringTokenizer(path,"/");
        if (tokenizer.hasMoreTokens()) application = tokenizer.nextToken();
        return application;
    }
    
    /**
     * Returns application context from given reqest
     * @param request Processing request
     * @return
     */
    public static String applicationContextPath(HttpServletRequest request) {
        try {
            String string = request.getRequestURL().toString();
            URL url = new URL(string);
            return applicationContextPath(url);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return "<no url>";
        }
    }

	public static String urlOfPath(HttpServletRequest request,  String path) {
		KConfiguration conf = KConfiguration.getInstance();
		if ((conf.getApplicationURL() != null) && (!conf.getApplicationURL().equals(""))) {
			return conf.getApplicationURL() +path;
		} else {
			return applicationURL(request)+"/"+path;
		}
	}
	
	public static void main(String[] args) throws MalformedURLException {
        String surl = "http://localhost:8080/search/";
        URL url = new URL(surl);
        String file = url.getFile();
        String query = url.getQuery();
        System.out.println(file);
        System.out.println(query);
        
        System.out.println(minus(surl,"?"+query));
	}
	
	public static String minus(String bigger, String smaller) {
	    if (bigger.length() > smaller.length()) {
            return bigger.replace(smaller, "");
	    } else throw new IllegalArgumentException("");
	}

}
