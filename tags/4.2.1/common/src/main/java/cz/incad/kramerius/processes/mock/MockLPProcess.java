package cz.incad.kramerius.processes.mock;

import java.io.IOException;
import java.util.Arrays;

import cz.incad.kramerius.processes.impl.ProcessStarter;

public class MockLPProcess {

	public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(MockLPProcess.class.getName());

	
	public static void main(String[] args) throws IOException {
		LOGGER.info("args:"+Arrays.asList(args));
		ProcessStarter.updateName("Jmeno ruze ... ");
		// 1TB  space
		long tb = 1l << 40;
		// 1GB  space
		long gb = 1l << 30;
		long start =System.currentTimeMillis();
		for (long i = 0; i < tb; i++) {
			if ((i%100000000) == 0) {
				//LOGGER.info("iterating "+i);
				LOGGER.info("  diff = "+(System.currentTimeMillis()-start)+"ms and i ="+i);
			}
		}
		LOGGER.info(" stop with "+(System.currentTimeMillis()-start)+"ms");
	}
}