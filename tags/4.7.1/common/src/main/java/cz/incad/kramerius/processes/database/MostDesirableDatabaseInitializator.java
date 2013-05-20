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
package cz.incad.kramerius.processes.database;

import static cz.incad.kramerius.processes.database.MostDesirableDatabaseUtils.createTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import cz.incad.kramerius.database.VersionService;
import cz.incad.kramerius.utils.DatabaseUtils;


public class MostDesirableDatabaseInitializator {

    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(MostDesirableDatabaseInitializator.class.getName());
    
    public static void initDatabase(Connection conn, VersionService versionService) {
        try {
            if (versionService.getVersion() == null) {
                if (!DatabaseUtils.tableExists(conn,"DESIRABLE")) {
                    createTable(conn);
                }
            } else { /* already created */ }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
        }
    }
}
