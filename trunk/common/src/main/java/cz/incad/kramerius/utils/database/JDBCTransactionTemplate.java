/*
 * Copyright (C) 2010 Pavel Stastny
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.incad.kramerius.utils.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class JDBCTransactionTemplate {
    
    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(JDBCTransactionTemplate.class.getName());
    
    private Connection connection;
    private boolean closeConnectionFlag;
    
    public JDBCTransactionTemplate(Connection con, boolean closeConnection) {
        this.connection = con;
        this.closeConnectionFlag = closeConnection;
    }
    
    
    public Object updateWithTransaction(final JDBCCommand... commands) throws SQLException {
        try {
            this.connection.setAutoCommit(false);
            
            Object obj = null;
            for (int i = 0,ll=commands.length; i < ll; i++) {
                JDBCCommand command = commands[i];
                command.setPreviousResult(obj);
                obj = command.executeJDBCCommand();
            }
            this.connection.commit();
            return obj;
        } catch (SQLException ex) {
            this.connection.rollback();
            throw ex;
        } finally {
            this.connection.setAutoCommit(true);
            if (closeConnectionFlag ) {
                try {
                    LOGGER.info("Closing connection !");
                    this.connection.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

    }
    
    
    public static void main(String[] args) {
        JDBCUpdateTemplate updateRightCriterium = new JDBCUpdateTemplate(null);
        JDBCUpdateTemplate updateRightCriterumParam = new JDBCUpdateTemplate(null);
        JDBCUpdateTemplate updateRight = new JDBCUpdateTemplate(null);
        
        
        
//        JDBCTransactionTemplate template = new JDBCTransactionTemplate(null, templates, closeConnection)
//        template.updateWithTransaction();
        
    }
}
