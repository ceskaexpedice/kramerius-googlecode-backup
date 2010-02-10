package cz.incad.kramerius.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gwt.user.server.Base64Utils;


/**
 * Umoznuje se dotazovat na fedoru, ktera potrebuje autentizaci
 * @author pavels
 */
public class RESTHelper {


	public InputStream inputStream(String urlString, String user, String pass) throws IOException {
		URL url = new URL(urlString);
		String userPassword = user + ":" + pass;
		String encoded = Base64Utils.toBase64(userPassword.getBytes()); 
		URLConnection uc = url.openConnection();
		uc.setRequestProperty ("Authorization", "Basic " + encoded);
		return uc.getInputStream();
	}
	
}
