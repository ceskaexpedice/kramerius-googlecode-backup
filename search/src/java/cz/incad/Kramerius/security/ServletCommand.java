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
package cz.incad.Kramerius.security;

import static cz.incad.utils.IKeys.UUID_PARAMETER;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.SolrAccess;
import cz.incad.kramerius.security.Group;
import cz.incad.kramerius.security.IsActionAllowed;
import cz.incad.kramerius.security.RightCriteriumWrapperFactory;
import cz.incad.kramerius.security.RightsManager;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.security.UserManager;
import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.utils.pid.LexerException;
import cz.incad.kramerius.utils.pid.PIDParser;

public abstract class ServletCommand {

    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ServletCommand.class.getName());


    @Inject
    protected Provider<HttpServletRequest> requestProvider;
    @Inject
    protected Provider<HttpServletResponse> responseProvider;

    @Inject
    protected transient RightsManager rightsManager;

    @Inject
    protected transient Provider<User> userProvider;

    @Inject
    protected transient SolrAccess solrAccess;

    @Inject
    protected transient ResourceBundleService resourceBundleService;

    @Inject
    protected transient Provider<Locale> localesProvider;

    @Inject
    @Named("securedFedoraAccess")
    protected transient FedoraAccess fedoraAccess;

    @Inject
    protected transient UserManager userManager;

    @Inject
    protected transient IsActionAllowed actionAllowed;

    @Inject
    protected transient RightCriteriumWrapperFactory criteriumWrapperFactory;
    
    public abstract void doCommand();


    public String getSecuredAction() {
        String securedActionString = this.requestProvider.get().getParameter("securedaction");
        return securedActionString;
    }

    public String[] getModels(String uuid) throws IOException {
        return uuid != null ? this.solrAccess.getPathOfModels(uuid) : new String[0];
    }

    public String[] getPathOfUUIDs(String uuid) throws IOException {
        return uuid != null ? this.solrAccess.getPathOfUUIDs(uuid) : new String[0];
    }

    public ResourceBundle getResourceBundle() throws IOException {
        ResourceBundle resourceBundle = resourceBundleService.getResourceBundle("labels", localesProvider.get());
        return resourceBundle;
    }

    public String getUuid() {
        String uuid = this.requestProvider.get().getParameter(UUID_PARAMETER);
        try {
            PIDParser pidParser = new PIDParser("uuid:" + uuid);
            pidParser.objectPid();
            return uuid;
        } catch (LexerException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    public Map<String, String> bundleToMap() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        ResourceBundle bundle = this.resourceBundleService.getResourceBundle("labels", localesProvider.get());
        Set<String> keySet = bundle.keySet();
        for (String key : keySet) {
            map.put(key, bundle.getString(key));
        }
        return map;
    }


    public boolean hasCurrentUserHasSuperAdminRole(User user) {
        Group[] groups = user.getGroups();
        for (Group group : groups) {
            if (group.getPersonalAdminId() <= 0 ) {
                return true;
            }
        }
        return false;
    
    }


    public int[] getUserGroups(User user) {
        Group[] grps = user.getGroups();
        int[] grpIds = new int[grps.length];
        for (int i = 0; i < grpIds.length; i++) {
            grpIds[i] = grps[i].getId();
        }
        return grpIds;
    }

}
