package cz.incad.Kramerius;

import static cz.incad.kramerius.utils.IOUtils.copyStreams;
import static cz.incad.utils.IKeys.UUID_PARAMETER;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPathExpressionException;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import cz.incad.Kramerius.AbstractImageServlet.OutputFormats;
import cz.incad.Kramerius.backend.guice.GuiceServlet;
import cz.incad.Kramerius.views.ApplicationURL;
import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.intconfig.InternalConfiguration;
import cz.incad.kramerius.security.SecurityException;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.utils.imgs.KrameriusImageSupport.ScalingMethod;
import cz.incad.utils.IKeys;

/**
 * Servlet na ziskavani malych nahledu - filmovy pas
 * 
 * @author pavels
 */
public class SmallThumbnailImageServlet extends AbstractImageServlet {

	public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(SmallThumbnailImageServlet.class.getName());

	public static final String PAGE_PARAMETER = "page";
	public static final String RAWDATA_PARAMETER = "rawdata";

	private static final String DEFAULT_MIMETYPE = "image/jpeg";

	@Override
	public void init() throws ServletException {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		OutputFormats outputFormat = null;
		String uuid = req.getParameter(UUID_PARAMETER);
		String outputFormatParam = req.getParameter(OUTPUT_FORMAT_PARAMETER);
		if (outputFormatParam != null) {
			outputFormat = OutputFormats.valueOf(outputFormatParam);
		}
		try {
			if (outputFormat == null) {
				BufferedImage image = rawThumbnailImage(uuid, 0);
				Rectangle rectangle = new Rectangle(image.getWidth(null), image.getHeight(null));
				BufferedImage scale = scale(image, rectangle, req);
				if (scale != null) {
                    setDateHaders(uuid, resp);
                    setResponseCode(uuid, req, resp);
					writeImage(req, resp, scale, OutputFormats.JPEG);
				} else resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			} else {

				InputStream is = this.fedoraAccess.getSmallThumbnail(uuid);
				if (outputFormat.equals(OutputFormats.RAW)) {
					String mimeType = this.fedoraAccess.getSmallThumbnailMimeType(uuid);
					if (mimeType == null) mimeType = DEFAULT_MIMETYPE;
					resp.setContentType(mimeType);
                    setDateHaders(uuid, resp);
					setResponseCode(uuid, req, resp);
					copyStreams(is, resp.getOutputStream());
				
				} else {
					BufferedImage rawImage = rawThumbnailImage(uuid,0);
                    setDateHaders(uuid, resp);
                    setResponseCode(uuid, req, resp);
					writeImage(req, resp, rawImage, outputFormat);
				}
			}
		} catch(SecurityException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}


	public KConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(KConfiguration configuration) {
		this.configuration = configuration;
	}

	public FedoraAccess getFedoraAccess() {
		return fedoraAccess;
	}

	public void setFedoraAccess(FedoraAccess fedoraAccess) {
		this.fedoraAccess = fedoraAccess;
	}
	
	
	@Override
	public ScalingMethod getScalingMethod() {
		KConfiguration config = KConfiguration.getInstance();
		ScalingMethod method = ScalingMethod.valueOf(config.getProperty(
				"thumbImage.scalingMethod", "BICUBIC_STEPPED"));
		return method;
	}


	@Override
	public boolean turnOnIterateScaling() {
		KConfiguration config = KConfiguration.getInstance();
		boolean highQuality = config.getConfiguration().getBoolean(
				"thumbImage.iterateScaling", true);
		return highQuality;
	}

	public static String thumbImageServlet(HttpServletRequest request) {
		return ApplicationURL.urlOfPath(request, InternalConfiguration.get().getProperties().getProperty("servlets.mapping.thmbImage"));
	}
	
}
