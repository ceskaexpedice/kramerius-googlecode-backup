package cz.incad.kramerius.processes.database;

import cz.incad.kramerius.processes.LRProcess;
import cz.incad.kramerius.processes.ProcessManagerException;
import cz.incad.kramerius.processes.States;
import cz.incad.kramerius.security.Role;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.utils.DatabaseUtils;
import cz.incad.kramerius.utils.database.JDBCQueryTemplate;
import cz.incad.kramerius.utils.database.JDBCUpdateTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class which contains methods for manipulation of processes
 * @author pavels
 *
 */
public class ProcessDatabaseUtils {

    public static final Logger LOGGER = Logger.getLogger(ProcessDatabaseUtils.class.getName());


    public static void insertProcessMapping2RolesNotClosingOP(Connection con, String token, Role role) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement(
            "insert into processes_2_role (token,role_id) values (?,?)");
        prepareStatement.setString(1, token);
        prepareStatement.setInt(2, role.getId());
        
        int r = prepareStatement.executeUpdate();
        LOGGER.log(Level.FINEST, "CREATE TABLE: inserted rows {0}", r);
    }
    

    public static void insertProcessMapping2SessionId(Connection con, String token, int processId, int sessionKeyId) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement(
            "insert into PROCESS_2_TOKEN (PROCESS_2_TOKEN_ID, " +
            "PROCESS_ID, " +
            "TOKEN," +
            "SESSION_KEYS_ID) " +
            " " +
            "values (nextval('PROCESS_2_TOKEN_ID_SEQUENCE')," +
            "?," +
            "?," +
            "?)");
        prepareStatement.setInt(1, processId);
        prepareStatement.setString(2, token);
        prepareStatement.setInt(3, sessionKeyId);
        
        
        int r = prepareStatement.executeUpdate();
        LOGGER.log(Level.FINEST, "CREATE TABLE: inserted rows {0}", r);
    }

    

    public static void createProcessTable(Connection con) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement(
                "CREATE TABLE PROCESSES("
                + "DEFID VARCHAR(255), UUID VARCHAR(255) PRIMARY KEY, PID int,"
                + " STARTED timestamp, PLANNED timestamp, STATUS int,"
                + " NAME VARCHAR(1024), PARAMS VARCHAR(4096))");
        try {
            int r = prepareStatement.executeUpdate();
            LOGGER.log(Level.FINEST, "CREATE TABLE: updated rows {0}", r);
        } finally {
            DatabaseUtils.tryClose(prepareStatement);
        }
    }

    public static void addColumn(Connection con, String tableName, String columnName, String def) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement(
                "alter table " + tableName + " add column " + columnName + " " + def);
        try {
            int r = prepareStatement.executeUpdate();
            LOGGER.log(Level.FINEST, "alter table {0}", r);
        } finally {
            DatabaseUtils.tryClose(prepareStatement);
        }

    }

    
    public static void registerProcess(Connection con, LRProcess lp, User user, String loggedUserKey) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement(
                "insert into processes(" +
                "   DEFID, " + //1
                "   UUID, " + //2
                "   PLANNED, " + //3
                "   STATUS, " + //4
                "   PARAMS, " + //5
                "   TOKEN, " +  //6
                "   PROCESS_ID, " + //
                "   LOGINNAME," + //7
                "   FIRSTNAME," + //8
                "   SURNAME," +  // 9
                "   USER_KEY) " + //10
                "       values " +
                "   (" +
                "       ?," + //1 - DEFID
                "       ?," + //2 - UUID
                "       ?," + //3 - PLANNED
                "       ?," + //4 - STATUS
                "       ?," + //5 - PARAMS
                "       ?," + //6 - TOKEN
                "       nextval('PROCESS_ID_SEQUENCE')," + // 
                "       ?," + //7 LOGINNAME
                "       ?," + //8 FIRSTNAME
                "       ?," + //9 SURNAME
                "       ?" + //10 USERKEY
                "   )");
        try {
            prepareStatement.setString(1, lp.getDefinitionId());
            prepareStatement.setString(2, lp.getUUID());
            prepareStatement.setTimestamp(3, new Timestamp(lp.getPlannedTime()));
            prepareStatement.setInt(4, lp.getProcessState().getVal());

            StringBuilder buffer = new StringBuilder();
            List<String> parameters = lp.getParameters();
            if (!parameters.isEmpty()) {
                for (int i = 0, ll = parameters.size(); i < ll; i++) {
                    buffer.append(parameters.get(i));
                    buffer.append((i == ll - 1) ? "" : ",");
                }
                prepareStatement.setString(5, buffer.toString());
            } else {
                prepareStatement.setString(5, null);
            }
            
            //prepareStatement.setInt(6, user.getId());
            
            prepareStatement.setString(6, lp.getToken());
            prepareStatement.setString(7, user.getLoginname());
            prepareStatement.setString(8, user.getFirstName());
            prepareStatement.setString(9, user.getSurname());
            prepareStatement.setString(10, loggedUserKey);
            
            prepareStatement.executeUpdate();
        } finally {
            DatabaseUtils.tryClose(prepareStatement);
        }
    }

    public static void updateProcessStarted(Connection con, String uuid, Timestamp timestamp) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement(
                "update processes set STARTED = ? where UUID = ?");
        try {
            prepareStatement.setTimestamp(1, timestamp);
            prepareStatement.setString(2, uuid);
            prepareStatement.executeUpdate();
        } finally {
            DatabaseUtils.tryClose(prepareStatement);
        }
    }


    public static void updateProcessState(Connection con, String uuid, States state) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement(
                "update processes set STATUS = ? where UUID = ?");
        try {
            prepareStatement.setInt(1, state.getVal());
            prepareStatement.setString(2, uuid);
            prepareStatement.executeUpdate();
        } finally {
            DatabaseUtils.tryClose(prepareStatement);
        }
    }

    public static void updateProcessName(Connection con, String uuid, String name) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement(
                "update processes set NAME = ? where UUID = ?");
        try {
            prepareStatement.setString(1, name);
            prepareStatement.setString(2, uuid);
            prepareStatement.executeUpdate();
        } finally {
            DatabaseUtils.tryClose(prepareStatement);
        }
    }

    /*
    public static void updateProcessPID(Connection con, String pid, String uuid) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement(
                "update processes set PID = ? where UUID = ?");
        
        try {
            prepareStatement.setInt(1, Integer.parseInt(pid));
            prepareStatement.setString(2, uuid);
            prepareStatement.executeUpdate();
        } finally {
            DatabaseUtils.tryClose(prepareStatement);
        }
    }*/

    
    
    public static void deleteProcess(Connection con, String uuid) throws SQLException {
        PreparedStatement prepareStatement = con.prepareStatement(
                "delete from processes where UUID = ?");
        try {
            prepareStatement.setString(1, uuid);
            prepareStatement.executeUpdate();
        } finally {
            DatabaseUtils.tryClose(prepareStatement);
        }
    }


    public static List<String> getAssociatedTokens(String sessionKey, Connection connection) {
        List<String> list = new JDBCQueryTemplate<String>(connection, false) {
            public boolean handleRow(ResultSet rs, List<String> returnsList) throws SQLException {
                returnsList.add(rs.getString("TOKEN"));
                return true;
            }
        }.executeQuery("select m.*,sk.* from PROCESS_2_TOKEN m" +
                " join session_keys sk on (sk.session_keys_id = m.session_keys_id)" +
                " where SESSION_KEY = ?", sessionKey);
        return list;
    }


    public static List<String> getAssociatedSessionKeys(String token, Connection connection) {
        List<String> list = new JDBCQueryTemplate<String>(connection, false) {
            public boolean handleRow(ResultSet rs, List<String> returnsList) throws SQLException {
                returnsList.add(rs.getString("SESSION_KEY"));
                return true;
            }
        }.executeQuery("select m.*,sk.* from PROCESS_2_TOKEN m" +
        		" join session_keys sk on (sk.session_keys_id = m.session_keys_id)" +
        		" where token = ?", token);
        return list;
    }


    public static int getProcessId(LRProcess lrProcess, Connection con) {
        List<Integer> list = new JDBCQueryTemplate<Integer>(con, false) {
            public boolean handleRow(ResultSet rs, List<Integer> returnsList) throws SQLException {
                returnsList.add(rs.getInt("process_id"));
                return true;
            }
        }.executeQuery("select * from processes where token = ?", lrProcess.getToken());
        return !list.isEmpty() ? list.get(0) : -1;
    }


    public static void updateTokenMapping(LRProcess lrProcess, Connection con, int sessionKeyId) throws SQLException {
        int id = getProcessId(lrProcess, con);
        if (id > -1) {
            new JDBCUpdateTemplate(con, true).executeUpdate("insert into PROCESS_2_TOKEN (PROCESS_2_TOKEN_ID, PROCESS_ID, TOKEN,SESSION_KEYS_ID) " + "values (nextval('PROCESS_2_TOKEN_ID_SEQUENCE')," + "?,?,?)", id, lrProcess.getToken(), sessionKeyId);
        } else {
            throw new ProcessManagerException("cannot find process id associated with token "+lrProcess.getToken());
        }
    }


    
    public static void deleteTokenMappings(LRProcess lrProcess, Connection con) throws SQLException {
        int id = getProcessId(lrProcess, con);
        if (id > -1) {
            new JDBCUpdateTemplate(con, true).executeUpdate("delete from PROCESS_2_TOKEN where process_id = ?", id);
        } else {
            throw new ProcessManagerException("cannot find process id associated with token "+lrProcess.getToken());
        }
    }
}
