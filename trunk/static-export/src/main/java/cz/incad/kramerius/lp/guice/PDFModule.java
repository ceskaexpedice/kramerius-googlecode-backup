package cz.incad.kramerius.lp.guice;

import java.sql.Connection;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.pdf.GeneratePDFService;
import cz.incad.kramerius.pdf.impl.GeneratePDFServiceImpl;
import cz.incad.kramerius.utils.conf.KConfiguration;


public class PDFModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FedoraAccess.class).annotatedWith(Names.named("rawFedoraAccess")).to(FedoraAccessImpl.class).in(Scopes.SINGLETON);
		bind(FedoraAccess.class).annotatedWith(Names.named("securedFedoraAccess")).to(FedoraAccessImpl.class).in(Scopes.SINGLETON);
		bind(GeneratePDFService.class).to(GeneratePDFServiceImpl.class).in(Scopes.SINGLETON);
	}
	
}
