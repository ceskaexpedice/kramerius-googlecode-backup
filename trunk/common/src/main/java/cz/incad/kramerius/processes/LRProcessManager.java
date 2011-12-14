package cz.incad.kramerius.processes;

import java.util.List;
import java.util.concurrent.locks.Lock;

import cz.incad.kramerius.security.User;


/**
 * Manages LR processes
 * @author pavels
 */
public interface LRProcessManager {

	/**
	 * Register new lr process
	 * @param lp
	 * @param sessionKey TODO
	 */
	public void registerLongRunningProcess(LRProcess lp, String sessionKey);
	
	/**
	 * Returns lr process with given uuid
	 * @param uuid
	 * @return
	 */
	public LRProcess getLongRunningProcess(String uuid);

	/**
	 * Return all lr processes
	 * @return
	 */
	public List<LRProcess> getLongRunningProcesses();

	/**
	 * Retrun planned processes 
	 * @param howMany How many processes should be returned
	 * @return
	 */
	public List<LRProcess> getPlannedProcess(int howMany);
	
	/**
	 * Return planned processes for given rules
	 * @param ordering Ordering column
	 * @param typeOfOrdering Type of ordering (ASC, DESC)
	 * @param offset offset
	 * @return
	 */
	public List<LRProcess> getLongRunningProcessesAsGrouped(LRProcessOrdering ordering,TypeOfOrdering typeOfOrdering, LRProcessOffset offset,LRPRocessFilter filter);

	public List<LRProcess> getLongRunningProcessesAsFlat(LRProcessOrdering ordering,TypeOfOrdering typeOfOrdering, LRProcessOffset offset);

	/**
	 * Returns all processes for given state
	 * @param state
	 * @return
	 */
	public List<LRProcess> getLongRunningProcesses(States state);
	
	
	/**
	 * Returns all process by given token
	 * @param token
	 * @return
	 */
    public List<LRProcess> getLongRunningProcessesByToken(String token);
	
	/**
	 * Returns number of running processes
	 * @return
	 */
	public int getNumberOfLongRunningProcesses();
	
	/**
	 * Update state 
	 * @param lrProcess
	 */
	public void updateLongRunningProcessState(LRProcess lrProcess);
	
	/**
	 * Update name
	 * @param lrProcess
	 */
	public void updateLongRunningProcessName(LRProcess lrProcess);
	
	/**
	 * Update PID
	 * @param lrProcess
	 */
	public void updateLongRunningProcessPID(LRProcess lrProcess);
	
	
	/**
	 * Update started date
	 * @param lrProcess
	 */
	public void updateLongRunningProcessStartedDate(LRProcess lrProcess);

	
	/**
	 * Update mappings between process and sessionKey 
	 * @param lrProcess Started processs
	 * @param sessionKey key represents logged user
	 */
	public void updateTokenMapping(LRProcess lrProcess, String sessionKey);
	
	/**
	 * Returns session key associated with process
	 * @param token Token associated with process
	 * @return
	 */
	public String getSessionKey(String token);
	
	/**
	 * Returns true if there is any association betweeen process and session key
	 * @param sessionKey Session key -> logged user
	 * @return
	 */
    public boolean isSessionKeyAssociatedWithProcess(String sessionKey);

	
	/**
	 * Delete process
	 * @param uuid
	 */
	public void deleteLongRunningProcess(LRProcess lrProcess);
	
    public void deleteBatchLongRunningProcess(LRProcess longRunningProcess);
	
	
	
	public Lock getSynchronizingLock();

}