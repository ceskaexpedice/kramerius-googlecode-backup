package cz.incad.kramerius.processes.os.impl.windows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import cz.incad.kramerius.processes.LRProcessDefinition;
import cz.incad.kramerius.processes.LRProcessManager;
import cz.incad.kramerius.processes.impl.AbstractLRProcessImpl;
import cz.incad.kramerius.utils.conf.KConfiguration;

public class WindowsLRProcessImpl extends AbstractLRProcessImpl {

	public WindowsLRProcessImpl(LRProcessDefinition definition,
			LRProcessManager manager, KConfiguration configuration) {
		super(definition, manager, configuration);
		this.setStartTime(System.currentTimeMillis());
	}

	@Override
	protected void stopMeOsDependent() {
		try {
			LOGGER.info("Killing process "+getPid());
			// taskkill /PID  <pid>
			List<String> command = new ArrayList<String>();
			command.add("taskkill");
			command.add("/PID");
			command.add(getPid());
			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder.start();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
