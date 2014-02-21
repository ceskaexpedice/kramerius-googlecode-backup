package cz.incad.kramerius.processes;

import cz.incad.kramerius.utils.conf.KConfiguration;

import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;

/**
 * Scheduler is able to start new process
 * @author pavels
 */
public class NextSchedulerTask extends TimerTask {

	public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(NextSchedulerTask.class.getName());
	
	private ProcessScheduler processScheduler;
	private LRProcessManager lrProcessManager;
	private DefinitionManager definitionManager;
	
	public NextSchedulerTask(LRProcessManager lrProcessManager, DefinitionManager definitionManager, ProcessScheduler processScheduler,  long interval) {
		super();
		this.lrProcessManager = lrProcessManager;
		this.definitionManager = definitionManager;
		this.processScheduler = processScheduler;
	}

	@Override
	public void run() {
		try {
			// neco delej
            LOGGER.fine("Process scheduler started.");

			definitionManager.load();
            long start = System.currentTimeMillis();
			List<LRProcess> plannedProcess = lrProcessManager.getPlannedProcess(allowRunningProcesses());
            long end = System.currentTimeMillis();
			LOGGER.fine("Planned processes: "+plannedProcess.size()+ " ("+(end-start)+" ms)");
			if (!plannedProcess.isEmpty()) {
				int runningProcesses = 0;
                start = System.currentTimeMillis();
				List<LRProcess> longRunningProcesses = this.lrProcessManager.getLongRunningProcessesAsFlat(null, null, null);
                end = System.currentTimeMillis();
                LOGGER.fine("Total processes: "+longRunningProcesses.size()+ " ("+(end-start)+" ms)");
				for (LRProcess lrProcess : longRunningProcesses) {
					if (lrProcess.getProcessState().equals(States.RUNNING)) {
						runningProcesses +=1;
					}
				}
                LOGGER.fine("Running processes: " +runningProcesses);
				if (runningProcesses < allowRunningProcesses()) {
					LRProcess lrProcess = plannedProcess.get(0);
					lrProcess.startMe(false, this.processScheduler.getApplicationLib(), this.processScheduler.getAdditionalJarFiles());
                    LOGGER.fine("Started process: "+lrProcess);
				}
			}  else {
				//LOGGER.fine("No planned process found");
			}
			this.processScheduler.scheduleNextTask();
		} catch(Throwable e) {
			this.processScheduler.shutdown();
			LOGGER.log(Level.SEVERE,e.getMessage(), e);
		}
        LOGGER.fine("Process scheduler finished.");
		
	}

	private int allowRunningProcesses() {
		String aProcess = KConfiguration.getInstance().getProperty("processQueue.activeProcess","1");
		return Integer.parseInt(aProcess);
	}
}
