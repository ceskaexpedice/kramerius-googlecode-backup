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
package cz.incad.kramerius.security.impl.http;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;

import cz.incad.kramerius.security.EvaluatingResult;
import cz.incad.kramerius.security.Role;
import cz.incad.kramerius.security.IsActionAllowed;
import cz.incad.kramerius.security.RightCriteriumContext;
import cz.incad.kramerius.security.RightCriteriumException;
import cz.incad.kramerius.security.RightCriteriumContextFactory;
import cz.incad.kramerius.security.RightsManager;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.security.UserManager;
import cz.incad.kramerius.security.impl.UserImpl;
import cz.incad.kramerius.security.jaas.K4UserPrincipal;

public class IsActionAllowedFromRequest implements IsActionAllowed {

    private Logger logger;
    private Provider<HttpServletRequest> provider;

    private RightsManager rightsManager;
    private RightCriteriumContextFactory ctxFactory;
    private Provider<User> currentLoggedUser;
    
    
    @Inject
    public IsActionAllowedFromRequest(Logger logger, Provider<HttpServletRequest> provider, RightsManager rightsManager, RightCriteriumContextFactory contextFactory, Provider<User> currentUserProvider) {
        super();
        this.logger = logger;
        this.provider = provider;
        this.rightsManager = rightsManager;
        this.ctxFactory = contextFactory;
        this.currentLoggedUser = currentUserProvider;
    }

    @Override
    public boolean isActionAllowed(String actionName, String uuid, String[] pathOfUuids) {
        try {
            User user = this.currentLoggedUser.get();
            return isAllowedInternalForFedoraDocuments(actionName, uuid, pathOfUuids, user);
        } catch (RightCriteriumException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return false;
    }
    
    public boolean isActionAllowed(User user, String actionName, String uuid, String[] pathOfUuids) {
        try {
            return isAllowedInternalForFedoraDocuments(actionName, uuid, pathOfUuids, user);
        } catch (RightCriteriumException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return false;
    }
    
    
    

    @Override
    public boolean[] isActionAllowedForAllPath(String actionName, String uuid, String[] pathOfUuids) {
        try {
            User user = this.currentLoggedUser.get();
            RightCriteriumContext ctx = this.ctxFactory.create(uuid, user, this.provider.get().getRemoteHost(), this.provider.get().getRemoteAddr());
            EvaluatingResult[] evalResults = this.rightsManager.resolveAllPath(ctx, uuid, pathOfUuids, actionName, user);
            boolean[] results = new boolean[evalResults.length];
            for (int i = 0; i < results.length; i++) {
                results[i] = resultOfResult(evalResults[i]);
            }
            return results;
        } catch (RightCriteriumException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return new boolean[pathOfUuids.length];
        }
    }

    public boolean isAllowedInternalForFedoraDocuments(String actionName, String uuid, String[] pathOfUuids, User user) throws RightCriteriumException {
        RightCriteriumContext ctx = this.ctxFactory.create(uuid, user, this.provider.get().getRemoteHost(), this.provider.get().getRemoteAddr());
        EvaluatingResult result = this.rightsManager.resolve(ctx, uuid, pathOfUuids, actionName, user);
        return result != null ? resultOfResult(result) : false;
    }

    private boolean resultOfResult(EvaluatingResult result) {
        return result == EvaluatingResult.TRUE ? true : false;
    }

    @Override
    public boolean isActionAllowed(User user, String actionName) {
        throw new UnsupportedOperationException("still unsupported");
    }
}
