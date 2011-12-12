package cz.incad.Kramerius.backend.guice;

import java.sql.Connection;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.MostDesirable;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.impl.MostDesirableImpl;
import cz.incad.kramerius.pdf.GeneratePDFService;
import cz.incad.kramerius.pdf.impl.GeneratePDFServiceImpl;
import cz.incad.kramerius.processes.ProcessScheduler;
import cz.incad.kramerius.processes.database.JNDIConnectionProvider;
import cz.incad.kramerius.processes.impl.ProcessSchedulerImpl;
import cz.incad.kramerius.security.SecuredFedoraAccessImpl;
import cz.incad.kramerius.security.SecurityAcceptor;
import cz.incad.kramerius.service.DeleteService;
import cz.incad.kramerius.service.ExportService;
import cz.incad.kramerius.service.PolicyService;
import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.service.TextsService;
import cz.incad.kramerius.service.impl.DeleteServiceImpl;
import cz.incad.kramerius.service.impl.ExportServiceImpl;
import cz.incad.kramerius.service.impl.PolicyServiceImpl;
import cz.incad.kramerius.service.impl.ResourceBundleServiceImpl;
import cz.incad.kramerius.service.impl.TextsServiceImpl;
import cz.incad.kramerius.utils.conf.KConfiguration;

/**
 * Base kramerius module 
 * @author pavels
 */
public class BaseModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FedoraAccess.class).annotatedWith(Names.named("rawFedoraAccess")).to(FedoraAccessImpl.class).in(Scopes.SINGLETON);
		bind(FedoraAccess.class).annotatedWith(Names.named("securedFedoraAccess")).to(SecuredFedoraAccessImpl.class).in(Scopes.SINGLETON);
		bind(GeneratePDFService.class).to(GeneratePDFServiceImpl.class);
		bind(KConfiguration.class).toInstance(KConfiguration.getInstance());
		bind(Connection.class).toProvider(JNDIConnectionProvider.class);
		bind(Locale.class).toProvider(LocalesProvider.class);

		bind(ProcessScheduler.class).to(ProcessSchedulerImpl.class).in(Scopes.SINGLETON);
		
		bind(DeleteService.class).to(DeleteServiceImpl.class).in(Scopes.SINGLETON);
		bind(ExportService.class).to(ExportServiceImpl.class).in(Scopes.SINGLETON);
		bind(PolicyService.class).to(PolicyServiceImpl.class).in(Scopes.SINGLETON);
		bind(TextsService.class).to(TextsServiceImpl.class).in(Scopes.SINGLETON);
		bind(ResourceBundleService.class).to(ResourceBundleServiceImpl.class).in(Scopes.SINGLETON);
		
		bind(SecurityAcceptor.class).to(RequestSecurityAcceptor.class);
		bind(LocalizationContext.class).toProvider(CustomLocalizedContextProvider.class);
		
		bind(MostDesirable.class).to(MostDesirableImpl.class);
	}
}