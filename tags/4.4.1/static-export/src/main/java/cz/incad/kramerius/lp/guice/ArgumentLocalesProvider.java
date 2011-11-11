package cz.incad.kramerius.lp.guice;

import java.util.Locale;

import com.google.inject.Provider;



public class ArgumentLocalesProvider implements Provider<Locale> {
	
	public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(ArgumentLocalesProvider.class.getName());
	
	public static final String ISO3COUNTRY_KEY = "ISO3COUNTRY";
	public static final String ISO3LANG_KEY = "ISO3LANG";
	
	private String iso3country;
	private String iso3lang;
	
	
	public ArgumentLocalesProvider() {
		super();
		this.iso3country = System.getProperty(ISO3COUNTRY_KEY);
		this.iso3lang = System.getProperty(ISO3LANG_KEY);
	}
	

	@Override
	public Locale get() {
		Locale[] availableLocales = Locale.getAvailableLocales();
		for (Locale locale : availableLocales) {
			if ((locale.getISO3Country().equals(this.iso3country)) &&  (locale.getISO3Language().equals(this.iso3lang)))  return locale;
		}
		LOGGER.info("returning default locale ");
		return Locale.getDefault();
	}
	
}
