package cz.incad.kramerius.lp.guice;

import java.sql.Connection;
import java.util.Locale;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.SolrAccess;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.impl.SolrAccessImpl;
import cz.incad.kramerius.pdf.GeneratePDFService;
import cz.incad.kramerius.pdf.impl.GeneratePDFServiceImpl;
import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.service.TextsService;
import cz.incad.kramerius.service.impl.ResourceBundleServiceImpl;
import cz.incad.kramerius.service.impl.TextsServiceImpl;
import cz.incad.kramerius.utils.conf.KConfiguration;


public class PDFModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FedoraAccess.class).annotatedWith(Names.named("rawFedoraAccess")).to(FedoraAccessImpl.class).in(Scopes.SINGLETON);
		bind(FedoraAccess.class).annotatedWith(Names.named("securedFedoraAccess")).to(FedoraAccessImpl.class).in(Scopes.SINGLETON);
		bind(SolrAccess.class).to(SolrAccessImpl.class).in(Scopes.SINGLETON);
		bind(GeneratePDFService.class).to(GeneratePDFServiceImpl.class).in(Scopes.SINGLETON);
		bind(Locale.class).toProvider(ArgumentLocalesProvider.class);

		bind(TextsService.class).to(TextsServiceImpl.class).in(Scopes.SINGLETON);
		bind(ResourceBundleService.class).to(ResourceBundleServiceImpl.class).in(Scopes.SINGLETON);

	}
	
}
