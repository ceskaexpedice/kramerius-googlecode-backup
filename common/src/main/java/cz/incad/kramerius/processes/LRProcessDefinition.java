package cz.incad.kramerius.processes;

import java.util.List;

/**
 * This is process definition
 * @author pavels
 */
public interface LRProcessDefinition {
	
	/**
	 * Returns description of process
	 * @return
	 */
	public String getDescription();
	
	/**
	 * Main class. At the moment only java processes are supported !
	 * @return
	 */
	public String getMainClass();
	
	/**
	 * Parameters to process
	 * @return
	 */
	public List<String> getParameters();

	/**
	 * Libraries for the processs
	 * @return
	 */
	public String getLibsDir();

	/**
	 * Definition identification
	 * @return
	 */
	public String getId();
	
	/**
	 * Factory method that create new process
	 * @return
	 */
	public LRProcess createNewProcess();
	
	/**
	 * Factory method that crate some old process from given information
	 * @param uuid UUID of process UUID of process 
	 * @param pid PID of proccess PID of process
	 * @param start Timestamp when process has been started
	 * @return
	 */
	public LRProcess loadProcess(String uuid, String pid, long start, States state, String name);
	
	/**
	 * Returns file error stream file 
	 * @return
	 */
	public String getErrStreamFolder();
	
	/**
	 * Returns standard stream file
	 * @return
	 */
	public String getStandardStreamFolder();

}
