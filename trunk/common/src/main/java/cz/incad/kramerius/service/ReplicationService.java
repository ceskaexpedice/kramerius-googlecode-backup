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

import java.io.IOException;
import java.util.List;

import cz.incad.kramerius.service.replication.ReplicationServiceFoxmlFilter;

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
    public List<String> prepareExport(String pid) throws ReplicateException,IOException;

    /**
     * Returns data of current pid
     * @param pid PID
     * @return raw foxml data 
     * @throws ReplicateException cannot return foxml
     * @throws IOException 
     */
    public byte[] getExportedFOXML(String pid) throws ReplicateException, IOException;
    
    

    /**
     * Return replication FOXML filter
     * @return FOXML filter
     */
    public ReplicationServiceFoxmlFilter getReplicationServiceFoxmlFilter();

    /**
     * Sets new FOXML filter instance
     * @param filter
     */
    public void setReplicationServiceFoxmlFilter(ReplicationServiceFoxmlFilter filter);
}
