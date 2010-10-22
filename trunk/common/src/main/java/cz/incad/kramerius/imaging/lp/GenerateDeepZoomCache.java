package cz.incad.kramerius.imaging.lp;

import java.io.IOException;
import java.util.Arrays;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import cz.incad.kramerius.imaging.DeepZoomCacheService;
import cz.incad.kramerius.imaging.lp.guice.GenerateDeepZoomCacheModule;

public class GenerateDeepZoomCache {

    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(GenerateDeepZoomCache.class.getName());

    public static void main(String[] args) throws IOException {
        System.out.println("Generate deep zoom cache :" + Arrays.asList(args));
        if (args.length == 1) {
            Injector injector = Guice.createInjector(new GenerateDeepZoomCacheModule());
            DeepZoomCacheService service = injector.getInstance(Key.get(DeepZoomCacheService.class, Names.named("memoryCacheForward")));
            service.prepareCacheForUUID(args[0]);
            LOGGER.info("Process finished");
        } else {
            LOGGER.severe("generate cache class <uuid>");
        }
    }
}
