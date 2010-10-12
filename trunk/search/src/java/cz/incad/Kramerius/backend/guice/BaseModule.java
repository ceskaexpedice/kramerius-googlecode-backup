package cz.incad.Kramerius.backend.guice;

import java.sql.Connection;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import cz.incad.Kramerius.security.RequestIsUserInRoleDecision;
import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.MostDesirable;
import cz.incad.kramerius.imaging.CacheService;
import cz.incad.kramerius.imaging.TileSupport;
import cz.incad.kramerius.imaging.impl.FileSystemCacheServiceImpl;
import cz.incad.kramerius.imaging.impl.SimpleMemoryCacheServiceWrapper;
import cz.incad.kramerius.imaging.impl.TileSupportImpl;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.impl.MostDesirableImpl;
import cz.incad.kramerius.pdf.GeneratePDFService;
import cz.incad.kramerius.pdf.impl.GeneratePDFServiceImpl;
import cz.incad.kramerius.processes.GCScheduler;
import cz.incad.kramerius.processes.ProcessScheduler;
import cz.incad.kramerius.processes.database.Fedora3ConnectionProvider;
import cz.incad.kramerius.processes.database.JNDIConnectionProvider;
import cz.incad.kramerius.processes.database.Kramerius4ConnectionProvider;
import cz.incad.kramerius.processes.impl.GCSchedulerImpl;
import cz.incad.kramerius.processes.impl.ProcessSchedulerImpl;
import cz.incad.kramerius.security.IPaddressChecker;
import cz.incad.kramerius.security.IsUserInRoleDecision;
import cz.incad.kramerius.security.SecuredFedoraAccessImpl;
import cz.incad.kramerius.service.DeleteService;
import cz.incad.kramerius.service.ExportService;
import cz.incad.kramerius.service.METSService;
import cz.incad.kramerius.service.PolicyService;
import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.service.TextsService;
import cz.incad.kramerius.service.impl.DeleteServiceImpl;
import cz.incad.kramerius.service.impl.ExportServiceImpl;
import cz.incad.kramerius.service.impl.METSServiceImpl;
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
		bind(METSService.class).to(METSServiceImpl.class);
		bind(KConfiguration.class).toInstance(KConfiguration.getInstance());
		
		bind(Connection.class).annotatedWith(Names.named("kramerius4")).toProvider(Kramerius4ConnectionProvider.class);
        bind(Connection.class).annotatedWith(Names.named("fedora3")).toProvider(Fedora3ConnectionProvider.class);

		bind(Locale.class).toProvider(LocalesProvider.class);
	
		bind(ProcessScheduler.class).to(ProcessSchedulerImpl.class).in(Scopes.SINGLETON);
		bind(GCScheduler.class).to(GCSchedulerImpl.class).in(Scopes.SINGLETON);
		
		bind(DeleteService.class).to(DeleteServiceImpl.class).in(Scopes.SINGLETON);
		bind(ExportService.class).to(ExportServiceImpl.class).in(Scopes.SINGLETON);
		bind(PolicyService.class).to(PolicyServiceImpl.class).in(Scopes.SINGLETON);
		bind(TextsService.class).to(TextsServiceImpl.class).in(Scopes.SINGLETON);
		bind(ResourceBundleService.class).to(ResourceBundleServiceImpl.class).in(Scopes.SINGLETON);
		//bind(JNDIConnectionProvider.class).toInstance(createKramerius4Provider());
		
		bind(IPaddressChecker.class).to(RequestIPaddressChecker.class);
		bind(IsUserInRoleDecision.class).to(RequestIsUserInRoleDecision.class);

		bind(LocalizationContext.class).toProvider(CustomLocalizedContextProvider.class);
		
		bind(MostDesirable.class).to(MostDesirableImpl.class);
		
		bind(TileSupport.class).to(TileSupportImpl.class);
		bind(CacheService.class).annotatedWith(Names.named("fileSystemCache")).to(FileSystemCacheServiceImpl.class).in(Scopes.SINGLETON);
		bind(CacheService.class).annotatedWith(Names.named("memoryCacheForward")).to(SimpleMemoryCacheServiceWrapper.class).in(Scopes.SINGLETON);
	}
	

	
}
