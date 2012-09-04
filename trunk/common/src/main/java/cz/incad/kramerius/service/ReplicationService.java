/*
 * Copyright (C) 2012 Pavel Stastny
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
package cz.incad.kramerius.service;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Main replication service
 * @author pavels
 */
public interface ReplicationService {
    
    /**
     * Preparing list of exporting pids
     * @param pid Root PID
     * @return
     * @throws ReplicateException cannot prepare export
     */
    public List<String> prepareExport(String pid) throws ReplicateException;

    /**
     * Returns data of current pid
     * @param pid PID
     * @return
     * @throws ReplicateException cannot return foxml
     */
    public byte[] getExportedFOXML(String pid) throws ReplicateException;
    
    
    /**
     * Returns descriptions of given pid
     * @param pid PID of the object
     * @return
     * @throws ReplicateException cannot return descriptions
     */
    public String[] getDescriptions(String pid) throws ReplicateException;
}
