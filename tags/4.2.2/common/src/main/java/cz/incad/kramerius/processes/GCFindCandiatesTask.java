package cz.incad.kramerius.processes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;

public class GCFindCandiatesTask extends TimerTask {

	public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(GCFindCandiatesTask.class.getName());
	
	private LRProcessManager lrProcessManager;
	private GCScheduler gcScheduler;
	
	public GCFindCandiatesTask(LRProcessManager lrProcessManager, DefinitionManager definitionManager, GCScheduler gcScheduler,  long interval) {
		super();
		this.lrProcessManager = lrProcessManager;
		this.gcScheduler = gcScheduler;
	}
	
	@Override
	public void run() {
		Lock lock = lrProcessManager.getSynchronizingLock();
		try {
			lock.lock();

			List<String> gccCandidates = new ArrayList<String>();
			
			PIDList pidList = PIDList.createPIDList();
			List<String> pids = pidList.getProcessesPIDS();
			List<LRProcess> longRunningProcesses = lrProcessManager.getLongRunningProcesses(States.RUNNING);
			for (LRProcess lrProcess : longRunningProcesses) {
				if (lrProcess.getPid() != null) {
					if (!pids.contains(lrProcess.getPid())) {
						gccCandidates.add(lrProcess.getUUID());
					}
				} else {
					LOGGER.info("process "+lrProcess.getUUID()+" has no pid ");
					gccCandidates.add(lrProcess.getUUID());
				}
			}

			if (!gccCandidates.isEmpty()) {
				this.gcScheduler.scheduleCheckFoundGCCandidates(gccCandidates);
			} else {
				this.gcScheduler.scheduleFindGCCandidates();
			}
			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			lock.unlock();
		}
	}
}
