package cz.incad.kramerius.processes.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import cz.incad.kramerius.processes.DefinitionManager;
import cz.incad.kramerius.processes.LRProcess;
import cz.incad.kramerius.processes.LRProcessDefinition;
import cz.incad.kramerius.processes.LRProcessManager;
import cz.incad.kramerius.processes.States;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.utils.conf.KConfiguration;

public abstract class AbstractLRProcessImpl implements LRProcess{

	
	public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(AbstractLRProcessImpl.class.getName());
	
	private LRProcessDefinition definition;
	private LRProcessManager manager;
	private KConfiguration configuration;

	private String pid;
	private long startTime;
	private long plannedTime;
	private String uuid;
	private States state = States.NOT_RUNNING;
	private String name;
	//private int userId;
	
	private String loginname;
	private String firstname;
	private String surname;

	private User user;
	private String loggedUserKey;
	
	private String token;
	private boolean masterProcess;
	
	private List<String> parameters = new ArrayList<String>();
	
	public AbstractLRProcessImpl(
			LRProcessDefinition definition,
			LRProcessManager manager,
			KConfiguration configuration) {
		super();
		this.definition = definition;
		this.manager = manager;
		this.configuration = configuration;
		this.uuid = UUID.randomUUID().toString();
	}

	
	@Override
	public String getDescription() {
		return this.definition.getDescription();
	}

	@Override
	public String getUUID() {
		return this.uuid;
	}
	
	@Override
	public List<String> getParameters() {
		return this.parameters;
	}


	@Override
	public void setParameters(List<String> params) {
		this.parameters = new ArrayList<String>(params);
	}
	

	@Override
	public boolean canBeStopped() {
		return getPid() != null && getProcessState().equals(States.RUNNING);
	}

	@Override
	public long getStartTime() {
		return this.startTime;
	}


	public void planMe() {
		this.state = States.PLANNED;
		this.setPlannedTime(System.currentTimeMillis());
		manager.registerLongRunningProcess(this, getLoggedUserKey());
	}
	
	
	@Override
	public void startMe(boolean wait, String krameriusAppLib, String... additionalJarFiles) {
		try {
			File processWorkingDir = processWorkingDirectory();

			//"java -D"+ProcessStarter.MAIN_CLASS_KEY+"="+mainClass
			// create command
			List<String> command = new ArrayList<String>();
			command.add("java");

			List<String> javaProcessParameters = this.definition.getJavaProcessParameters();
			for (String jpParam : javaProcessParameters) {
				command.add(jpParam);
			}

			command.add("-D"+ProcessStarter.MAIN_CLASS_KEY+"="+this.definition.getMainClass());
			command.add("-D"+ProcessStarter.UUID_KEY+"="+this.uuid);
            command.add("-D"+ProcessStarter.TOKEN_KEY+"="+this.getToken());
			
			
			File standardStreamFile = standardOutFile(processWorkingDir);
			File errStreamFile = errorOutFile(processWorkingDir);

			
			command.add("-D"+ProcessStarter.SOUT_FILE+"="+standardStreamFile.getAbsolutePath());
			command.add("-D"+ProcessStarter.SERR_FILE+"="+errStreamFile.getAbsolutePath());
			command.add(ProcessStarter.class.getName());
			List<String> params = this.definition.getParameters();
			for (String par : params) {
				command.add(par);
			}
			List<String> runtimeParams = this.getParameters();
			for (String par : runtimeParams) {
				command.add(par);
			}
			
			//create CLASSPATH
			StringBuffer buffer = new StringBuffer();
			String libsDirPath = this.definition.getLibsDir();
			if (libsDirPath == null) {
				libsDirPath = krameriusAppLib;
			}

			File libsDir = new File(libsDirPath);
			File[] listFiles = libsDir.listFiles();
			if (listFiles !=  null) {
				for (File file : listFiles) {
					buffer.append(file.getAbsolutePath());
					buffer.append(File.pathSeparator);
				}
			}
			//TODO: co delat pri zmene definice?  
			for (String string : additionalJarFiles) {
                buffer.append(new File(string).getAbsolutePath());
                buffer.append(File.pathSeparator);
            }

			ProcessBuilder processBuilder = new ProcessBuilder(command);
			processBuilder = processBuilder.directory(processWorkingDir);
			
			processBuilder.environment().put(ProcessStarter.CLASSPATH_NAME, buffer.toString());
			this.setStartTime(System.currentTimeMillis());
			this.state = States.RUNNING;
			manager.updateLongRunningProcessState(this);
			manager.updateLongRunningProcessStartedDate(this);
            
			LOGGER.fine(""+command);
			LOGGER.fine(buffer.toString());
			
			Process process = processBuilder.start();
			
			// pokracuje dal.. rozhoduje se, jestli pocka na vysledek procesu
			if (wait) {
				int val = process.waitFor();
				LOGGER.info("return value exiting process '"+val+"'");
			}
			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}


	private File errorOutFile(File processWorkingDir) {
		return new File(createFolderIfNotExists(processWorkingDir+File.separator+this.definition.getErrStreamFolder()),"sterr.err");
	}


	private File standardOutFile(File processWorkingDir) {
		return new File(createFolderIfNotExists(processWorkingDir+File.separator+ this.definition.getStandardStreamFolder()),"stout.out");
	}


	public File processWorkingDirectory() {
		File processWorkingDir = new File(DefinitionManager.DEFAULT_LP_WORKDIR+File.separator+uuid);
		if (!processWorkingDir.exists()) {
			boolean mkdirs = processWorkingDir.mkdirs();
			if (!mkdirs) throw new RuntimeException("cannot create directory '"+processWorkingDir.getAbsolutePath()+"'");
		}
		return processWorkingDir;
	}


	private File createFolderIfNotExists(String folder) {
		File fldr= new File(folder);
		if (!fldr.exists()) {
			boolean mkdirs = fldr.mkdirs();
			if (!mkdirs) throw new RuntimeException("cannot create directory '"+fldr.getAbsolutePath()+"'");
		}
		return fldr;
	}

	@Override
	public void stopMe() {
	    if (this.getProcessState() == States.PLANNED) {
            this.setProcessState(States.KILLED);
            this.manager.updateLongRunningProcessState(this);
	    } else {
	        if (this.pid == null) {
	            throw new IllegalStateException("cannot stop this process! No PID associated");
	        }
	        
	        this.setProcessState(States.KILLED);
	        this.manager.updateLongRunningProcessState(this);
	        
	        this.stopMeOsDependent();
	    }
	}

	protected abstract void stopMeOsDependent();
	
	public String getPid() {
		return pid;
	}
	

	public void setPid(String pid) {
		this.pid = pid;
	}


	@Override
	public String getDefinitionId() {
		return this.definition.getId();
	}


	public LRProcessDefinition getDefinition() {
		return definition;
	}


	public void setDefinition(LRProcessDefinition definition) {
		this.definition = definition;
	}




	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public String getUuid() {
		return uuid;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public void setProcessState(States state) {
		this.state =  state;
	}

	@Override
	public States getProcessState() {
		return this.state;
	}


	@Override
	public String getProcessName() {
		return this.name;
	}



	@Override
	public void setProcessName(String nm) {
		this.name = nm;
	}

//	public File processWorkingDirectory() {

	@Override
	public InputStream getErrorProcessOutputStream() throws FileNotFoundException {
		return new FileInputStream(errorOutFile(processWorkingDirectory()));
	}


	@Override
	public InputStream getStandardProcessOutputStream() throws FileNotFoundException {
		return new FileInputStream(standardOutFile(processWorkingDirectory()));
	}


	@Override
	public RandomAccessFile getErrorProcessRAFile() throws FileNotFoundException {
		File errStreamFile = errorOutFile(processWorkingDirectory());
		return new RandomAccessFile(errStreamFile, "r");
	}


	@Override
	public RandomAccessFile getStandardProcessRAFile() throws FileNotFoundException {
		File standardStreamFile = standardOutFile(processWorkingDirectory());
		return new RandomAccessFile(standardStreamFile, "r");
	}

	

	public long getPlannedTime() {
		return plannedTime;
	}


	public void setPlannedTime(long plannedTime) {
		this.plannedTime = plannedTime;
	}



    @Override
    public User getUser() {
        return this.user;
    }


    @Override
    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public String getToken() {
        return this.token;
    }


    @Override
    public void setToken(String token) {
        this.token = token;
    }


    public String getLoginname() {
        return loginname;
    }


    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }


    public String getFirstname() {
        return firstname;
    }


    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }


    public String getSurname() {
        return surname;
    }


    public void setSurname(String surname) {
        this.surname = surname;
    }


    @Override
    public String getLoggedUserKey() {
        return this.loggedUserKey;
    }


    @Override
    public void setLoggedUserKey(String loggedUserKey) {
        this.loggedUserKey = loggedUserKey;
    }


    public boolean isMasterProcess() {
        return this.masterProcess;
    }


    @Override
    public void setMasterProcess(boolean flag) {
        this.masterProcess = flag;
    }
    
}