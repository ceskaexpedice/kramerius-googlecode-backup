package cz.incad.Kramerius.backend.guice;

import java.util.Locale;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.inject.Inject;
import com.google.inject.Provider;

import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.service.TextsService;
import cz.incad.kramerius.service.impl.ResourceBundleServiceImpl;

public class LocalesProvider implements Provider<Locale>{

	public static final String CLIENT_LOCALE = "client_locale";
	
	
	//private HttpServletRequest request;
	private Provider<HttpServletRequest> provider;
	private Logger logger;
	private TextsService textsService;
	
	@Inject
	public LocalesProvider(Provider<HttpServletRequest> provider, Logger logger, TextsService textsService) {
		super();
		this.provider = provider;
		this.logger = logger;
		this.textsService = textsService;
	}

	
	@Override
	public Locale get() {
		HttpServletRequest request = this.provider.get();
		HttpSession session = request.getSession(true);
		String parameter = request.getParameter("language");
		if (parameter != null) {
			Locale foundLocale = this.textsService.findLocale(parameter);
			if (foundLocale == null) {
				return getDefault(request);
			}
			session.setAttribute(CLIENT_LOCALE,foundLocale);
			return foundLocale;
		} else if (session.getAttribute(CLIENT_LOCALE) != null) {
			return (Locale) session.getAttribute(CLIENT_LOCALE);
		} else {
			return getDefault(request);
		}
	}


	private Locale getDefault(HttpServletRequest request) {
		logger.info("Provider "+request.getQueryString()+" 0x"+Integer.toHexString(System.identityHashCode(request)));
		Locale locale = request.getLocale();
		logger.info("client locale "+locale);
		return locale;
	}
}
