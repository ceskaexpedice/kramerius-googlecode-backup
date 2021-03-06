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
package cz.incad.Kramerius.security.rightscommands.post;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;

import sun.security.krb5.internal.SeqNumber;

import cz.incad.Kramerius.security.RightsServlet;
import cz.incad.Kramerius.security.ServletCommand;
import cz.incad.Kramerius.security.rightscommands.ServletRightsCommand;
import cz.incad.Kramerius.security.strenderers.CriteriumParamsWrapper;
import cz.incad.kramerius.ObjectPidsPath;
import cz.incad.kramerius.security.Right;
import cz.incad.kramerius.security.RightCriteriumWrapperFactory;
import cz.incad.kramerius.security.RightsManager;
import cz.incad.kramerius.security.SecuredActions;
import cz.incad.kramerius.security.SecurityException;
import cz.incad.kramerius.security.impl.RightImpl;

public class Create extends ServletRightsCommand {
    
    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Create.class.getName());

    
    
    
    @Override
    public void doCommand() {
        try {
            HttpServletRequest req = this.requestProvider.get();
            //Right right = RightsServlet.createRightFromPost(req, rightsManager, userManager, criteriumWrapperFactory);
            Map values = new HashMap();
            Enumeration parameterNames = req.getParameterNames();
            
            while(parameterNames.hasMoreElements()) {
                String key = (String) parameterNames.nextElement();
                String value = req.getParameter(key);
                SimpleJSONObjects simpleJSONObjects = new SimpleJSONObjects();
                simpleJSONObjects.createMap(key, values, value);
            }
            
            List affectedObjects = (List) values.get("affectedObjects");
            for (int i = 0; i < affectedObjects.size(); i++) {
                String pid = affectedObjects.get(i).toString();
                insertRight((Map) values.get("data"), pid);
            }

        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(),e);
        }
    }




    private void insertRight(Map data, String pid) throws IOException, SQLException {
        RightImpl right = right(data, pid);

        ObjectPidsPath[] paths = this.solrAccess.getPath(pid);
        
        boolean hasRight = false;
        for (int i = 0; i < paths.length; i++) {
            if (this.actionAllowed.isActionAllowed(SecuredActions.ADMINISTRATE.getFormalName(), pid, null, paths[i])) {
                hasRight = true;
                break;
            } else {
                throw new SecurityException("operation is not permited");
            }
        } 
        // root object
        if (paths.length  == 0) {
            if (this.actionAllowed.isActionAllowed(SecuredActions.ADMINISTRATE.getFormalName(), pid, null, new ObjectPidsPath(pid))) {
                hasRight = true;
            } else {
                throw new SecurityException("operation is not permited");
            }
        }
        
        if (hasRight) {
            rightsManager.insertRight(right);
        }
    }

    
}
