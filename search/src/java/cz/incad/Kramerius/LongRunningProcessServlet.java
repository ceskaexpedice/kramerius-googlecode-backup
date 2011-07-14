package cz.incad.Kramerius;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.google.inject.Inject;
import com.google.inject.Provider;

import cz.incad.Kramerius.backend.guice.GuiceServlet;
import cz.incad.Kramerius.processes.ParamsLexer;
import cz.incad.Kramerius.processes.ParamsParser;
import cz.incad.Kramerius.security.KrameriusRoles;
import cz.incad.kramerius.intconfig.InternalConfiguration;
import cz.incad.kramerius.processes.DefinitionManager;
import cz.incad.kramerius.processes.GCScheduler;
import cz.incad.kramerius.processes.LRProcess;
import cz.incad.kramerius.processes.LRProcessDefinition;
import cz.incad.kramerius.processes.LRProcessManager;
import cz.incad.kramerius.processes.LRProcessOffset;
import cz.incad.kramerius.processes.LRProcessOrdering;
import cz.incad.kramerius.processes.ProcessScheduler;
import cz.incad.kramerius.processes.States;
import cz.incad.kramerius.processes.TypeOfOrdering;
import cz.incad.kramerius.security.IsActionAllowed;
import cz.incad.kramerius.security.SecuredActions;
import cz.incad.kramerius.security.SecurityException;
import cz.incad.kramerius.security.SpecialObjects;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.security.UserManager;
import cz.incad.kramerius.security.utils.UserUtils;
import cz.incad.kramerius.utils.ApplicationURL;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.utils.database.JDBCQueryTemplate;

/**
 * This is support for long running processes
 * 
 * @author pavels
 */
public class LongRunningProcessServlet extends GuiceServlet {

    private static final long serialVersionUID = 1L;

    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(LongRunningProcessServlet.class.getName());

    @Inject
    transient DefinitionManager definitionManager;

    @Inject
    transient LRProcessManager lrProcessManager;

    @Inject
    transient KConfiguration configuration;

    @Inject
    transient ProcessScheduler processScheduler;

    @Inject
    transient GCScheduler gcScheduler;


    @Inject
    transient IsActionAllowed actionAllowed;

    @Inject
    transient Provider<User> userProvider;
    @Inject
    transient UserManager usersManager;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            // classpath from war
            String appLibPath = getWebAppClasspath();
            // security core from tomcat/lib
            String jarFile = getSecurityCoreJarFile();
            
            KConfiguration conf = KConfiguration.getInstance();
            if ((conf.getApplicationURL() == null) || (conf.getApplicationURL().equals(""))) {
                throw new RuntimeException("lr servlet need configuration parameter 'applicationUrl'");
            }

            this.processScheduler.init(appLibPath, jarFile);
            this.gcScheduler.init();
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
        }
    }

    public String getSecurityCoreJarFile() throws URISyntaxException {
        URL url= JDBCQueryTemplate.class.getResource(JDBCQueryTemplate.class.getSimpleName()+".class");
        String jarFile = url.getFile();
        if (jarFile.contains("!")) {
            StringTokenizer tokenizer = new StringTokenizer(jarFile,"!");
            if (tokenizer.hasMoreTokens()) {
                String nextToken = tokenizer.nextToken();
                File nfile = new File(new URI(nextToken));
                return nfile.getAbsolutePath();
                
            } else return null;
        } else return null;
    }

    public String getWebAppClasspath() {
        String appLibPath = getServletContext().getRealPath("WEB-INF/lib");
        return appLibPath;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        try {
            String action = req.getParameter("action");
            if (action == null)
                action = Actions.list.name();
            Actions selectedAction = Actions.valueOf(action);
            selectedAction.doAction(getServletContext(), req, resp, this.definitionManager, this.lrProcessManager, this.usersManager, this.userProvider, this.actionAllowed);
        } catch (SecurityException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
    

    public static LRProcess planNewProcess(HttpServletRequest request, ServletContext context, String def, DefinitionManager definitionManager, String[] params, User user) {
        definitionManager.load();
        LRProcessDefinition definition = definitionManager.getLongRunningProcessDefinition(def);
        if (definition == null) {
            throw new RuntimeException("cannot find process definition '" + def + "'");
        }
        String token = request.getParameter("token");
        LRProcess newProcess = definition.createNewProcess(token);
        newProcess.setUser(user);
        newProcess.setParameters(Arrays.asList(params));
        newProcess.planMe();
        return newProcess;
    }

    public static LRProcess stopOldProcess(String defaultLibDir, String uuidOfProcess, DefinitionManager defManager, LRProcessManager lrProcessManager) {
        defManager.load();
        lrProcessManager.getLongRunningProcess(uuidOfProcess).stopMe();
        return lrProcessManager.getLongRunningProcess(uuidOfProcess);
    }

    public static SecuredActions getAction(String def) {
        SecuredActions[] securedActions = SecuredActions.values();
        for (SecuredActions act : securedActions) {
            if (act.getFormalName().equals(def)) return act;
        }
        return null;
    }

    static enum Actions {

        /**
         * Plan new process
         */
        start {
            public void doAction(ServletContext context, HttpServletRequest req, HttpServletResponse resp, DefinitionManager defManager, LRProcessManager lrProcessManager, UserManager userManager, Provider<User> userProvider, IsActionAllowed rightsResolver) {
                try {
                    String def = req.getParameter("def");
                    String out = req.getParameter("out");
                    String[] params = getParams(req);
                    //TODO: Zjisteni predavane autentizace 
                    SecuredActions actionFromDef = SecuredActions.findByFormalName(def);
                    String token = req.getParameter("token");
                    User user = null;
                    if (token != null) {
                        List<LRProcess> processes = lrProcessManager.getLongRunningProcessesByToken(token);
                        if (!processes.isEmpty()) {
                            user = processes.get(0).getUser();
                            UserUtils.associateGroups(user, userManager);
                            UserUtils.associateCommonGroup(user, userManager);
                            
                        }
                    } else {
                        user = userProvider.get();
                    }
                    boolean permited = user!= null? (rightsResolver.isActionAllowed(user,SecuredActions.MANAGE_LR_PROCESS.getFormalName(), SpecialObjects.REPOSITORY.getUuid(), new String[]{}) || 
                                        (actionFromDef != null && rightsResolver.isActionAllowed(user, actionFromDef.getFormalName(), SpecialObjects.REPOSITORY.getUuid(), new String[] {}))) : false ;
                    if (permited) {
                        LRProcess nprocess = planNewProcess(req, context, def, defManager, params, user);
                        
                        if ((out != null) && (out.equals("text"))) {
                            resp.setContentType("text/plain");
                            resp.getOutputStream().print("[" + nprocess.getDefinitionId() + "]" + nprocess.getProcessState().name());
                        } else {
                            StringBuffer buffer = new StringBuffer();
                            buffer.append("<html><body>");
                            buffer.append("<ul>");
                            buffer.append("<li>").append(nprocess.getDefinitionId());
                            buffer.append("<li>").append(nprocess.getUUID());
                            buffer.append("<li>").append(nprocess.getPid());
                            buffer.append("<li>").append(new Date(nprocess.getStartTime()));
                            buffer.append("<li>").append(nprocess.getProcessState());
                            buffer.append("</ul>");
                            buffer.append("</body></html>");
                            resp.setContentType("text/html");
                            resp.getOutputStream().println(buffer.toString());
                        }
                    } else {
                        throw new SecurityException("access denided");
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                } catch (RecognitionException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                } catch (TokenStreamException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }

        },

        /**
         * Stop running process
         */
        stop {
            @Override
            public void doAction(ServletContext context, HttpServletRequest req, HttpServletResponse resp, DefinitionManager defManager, LRProcessManager lrProcessManager, UserManager userManager, Provider<User> userProvider, IsActionAllowed actionIsAllowed) {
                if (actionIsAllowed.isActionAllowed(SecuredActions.MANAGE_LR_PROCESS.getFormalName(), SpecialObjects.REPOSITORY.getUuid(), new String[] {})) {
                    try {
                        String uuid = req.getParameter("uuid");
                        String realPath = context.getRealPath("WEB-INF/lib");
                        LRProcess oProcess = stopOldProcess(realPath, uuid, defManager, lrProcessManager);
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("<html><body>");
                        buffer.append("<ul>");
                        buffer.append("<li>").append(oProcess.getDefinitionId());
                        buffer.append("<li>").append(oProcess.getUUID());
                        buffer.append("<li>").append(oProcess.getPid());
                        buffer.append("<li>").append(new Date(oProcess.getStartTime()));
                        buffer.append("<li>").append(oProcess.getProcessState());
                        buffer.append("</ul>");
                        buffer.append("</body></html>");
                        resp.setContentType("text/html");
                        resp.getOutputStream().println(buffer.toString());
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            }

        },

        /**
         * list all processes
         */
        list {
            @Override
            public void doAction(ServletContext context, HttpServletRequest req, HttpServletResponse resp, DefinitionManager defManager, LRProcessManager lrProcessManager, UserManager userManager, Provider<User> userProvider, IsActionAllowed actionAllowed) {
                if (actionAllowed.isActionAllowed(SecuredActions.MANAGE_LR_PROCESS.getFormalName(), SpecialObjects.REPOSITORY.getUuid(), new String[] {})) {
                    try {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("<html><body>");
                        buffer.append("<h1>Running processes</h1>");
                        buffer.append("<ul>");
                        LRProcessOrdering ordering = LRProcessOrdering.NAME;
                        LRProcessOffset offset = new LRProcessOffset("0", "20");
                        List<LRProcess> longRunningProcesses = lrProcessManager.getLongRunningProcesses(ordering, TypeOfOrdering.ASC, offset);
                        for (LRProcess lrProcess : longRunningProcesses) {
                            buffer.append("<li>").append("PID:").append(lrProcess.getPid());
                            if (lrProcess.canBeStopped()) {
                                buffer.append("  ... <a href='" + lrServlet(req) + "?action=stop&uuid=" + lrProcess.getUUID() + "'>stop</a>");
                            }
                            buffer.append("<li>").append("uuid :").append(lrProcess.getUUID());
                            buffer.append("<li>").append("name :").append(lrProcess.getProcessName());
                            buffer.append("<li>").append("started :" + new Date(lrProcess.getStartTime()));
                            buffer.append("<li>").append("processState :").append(lrProcess.getProcessState());
                            LRProcessDefinition lrDef = defManager.getLongRunningProcessDefinition(lrProcess.getDefinitionId());
                            if (lrDef == null) {
                                throw new RuntimeException("cannot find definition '" + lrProcess.getDefinitionId() + "'");
                            }
                            buffer.append("<li>").append("errOut  :").append(lrDef.getErrStreamFolder() + File.separator + lrProcess.getUUID() + ".err");
                            buffer.append("<li>").append("standardOut  :").append(lrDef.getStandardStreamFolder() + File.separator + lrProcess.getUUID() + ".out");
                            buffer.append("<hr>");
                        }
                        buffer.append("</ul>");
                        buffer.append("</body></html>");

                        resp.setContentType("text/html");
                        resp.setCharacterEncoding("UTF-8");

                        resp.getWriter().println(buffer.toString());
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                }
            }
        },

        updatePID {
            @Override
            public void doAction(ServletContext context, HttpServletRequest req, HttpServletResponse resp, DefinitionManager defManager, LRProcessManager lrProcessManager, UserManager userManager, Provider<User> userProvider, IsActionAllowed actionAllowed) {
                Lock lock = lrProcessManager.getSynchronizingLock();
                lock.lock();
                try {
                    String uuid = req.getParameter("uuid");
                    String pid = req.getParameter("pid");
                    LRProcess longRunningProcess = lrProcessManager.getLongRunningProcess(uuid);
                    longRunningProcess.setPid(pid);
                    lrProcessManager.updateLongRunningProcessPID(longRunningProcess);
                } finally {
                    lock.unlock();
                }
            }
        },

        updateStatus {

            @Override
            public void doAction(ServletContext context, HttpServletRequest req, HttpServletResponse resp, DefinitionManager defManager, LRProcessManager processManager, UserManager userManager, Provider<User> userProvider, IsActionAllowed actionAllowed) {
                String uuid = req.getParameter("uuid");
                String state = req.getParameter("state");
                Lock lock = processManager.getSynchronizingLock();
                lock.lock();
                try  {
                    LRProcess longRunningProcess = processManager.getLongRunningProcess(uuid);
                    if (state != null) {

                        States st = States.valueOf(state);

                        if (st.equals(States.KILLED) && longRunningProcess.getProcessState().equals(States.RUNNING)) {
                            longRunningProcess.setProcessState(st);
                            processManager.updateLongRunningProcessState(longRunningProcess);
                        } else if (!st.equals(States.KILLED)) {
                            longRunningProcess.setProcessState(st);
                            processManager.updateLongRunningProcessState(longRunningProcess);
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        },

        updateName {

            @Override
            public void doAction(ServletContext context, HttpServletRequest req, HttpServletResponse resp, DefinitionManager defManager, LRProcessManager processManager, UserManager userManager, Provider<User> userProvider, IsActionAllowed actionAllowed) {
                Lock lock = processManager.getSynchronizingLock();
                lock.lock();
                try {
                    String uuid = req.getParameter("uuid");
                    String name = req.getParameter("name");
                    if (name != null) {
                        name = URLDecoder.decode(name, "UTF-8");
                        LRProcess longRunningProcess = processManager.getLongRunningProcess(uuid);
                        longRunningProcess.setProcessName(name);
                        processManager.updateLongRunningProcessName(longRunningProcess);
                    }
                } catch (UnsupportedEncodingException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                } finally {
                    lock.unlock();
                }
            }
        },

        delete {
            @Override
            public void doAction(ServletContext context, HttpServletRequest req, HttpServletResponse resp, DefinitionManager defManager, LRProcessManager processManager, UserManager userManager, Provider<User> userProvider, IsActionAllowed actionAllowed) {
                Lock lock = processManager.getSynchronizingLock();
                lock.lock();
                try {
                    String uuid = req.getParameter("uuid");
                    LRProcess longRunningProcess = processManager.getLongRunningProcess(uuid);
                    if (longRunningProcess != null) {
                        processManager.deleteLongRunningProcess(longRunningProcess);
                    }
                } finally {
                    lock.unlock();
                }
            }
        };

//        static boolean isInProcessAdminRole(IsUserInRoleDecision userInRoleDecision) {
//            return userInRoleDecision.isUserInRole(KrameriusRoles.LRPROCESS_ADMIN.getRoleName());
//        }

        abstract void doAction(ServletContext context, HttpServletRequest req, HttpServletResponse resp, DefinitionManager defManager, LRProcessManager processManager, UserManager userManager, Provider<User> userProvider, IsActionAllowed actionAllowed);
    }

    public static String lrServlet(HttpServletRequest request) {
        return ApplicationURL.urlOfPath(request, InternalConfiguration.get().getProperties().getProperty("servlets.mapping.lrcontrol"));
    }

    public static String[] getParams(HttpServletRequest req) throws RecognitionException, TokenStreamException {
        String parametersString = req.getParameter("params");
        if ((parametersString !=null) && (!parametersString.trim().equals("")))  {
            return parametersString.split(",");
        } else {
            parametersString = req.getParameter("nparams");
            if ((parametersString !=null) && (!parametersString.trim().equals("")))  {
                ParamsParser parser = new ParamsParser(new ParamsLexer(new StringReader(parametersString)));
                List paramsList = parser.params();
                return (String[]) paramsList.toArray(new String[paramsList.size()]);
            }
            return new String[0];
        }
    }
}
