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
package cz.incad.kramerius.security.jaas;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import biz.sourcecode.base64Coder.Base64Coder;


import cz.incad.kramerius.security.Group;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.security.impl.UserImpl;
import cz.incad.kramerius.security.utils.PasswordDigest;
import cz.incad.kramerius.security.utils.SecurityDBUtils;
import cz.incad.kramerius.utils.database.JDBCQueryTemplate;

/**
 * JAAS login module for K4
 * @author pavels
 */
public class K4LoginModule implements LoginModule {

    private static final String PSWD_COL = "pswd";

    public static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(K4LoginModule.class.getName());
    
    private Subject subject;
    private CallbackHandler callbackhandler;
    private Map<String, ?> options;

    
    private boolean logged = false;

    private User foundUser;
    private String foundPswd;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackhandler = callbackHandler;
        this.options = options;
        

    }

    @Override
    public boolean login() throws LoginException {
        try {
            
            NameCallback nmCallback = new NameCallback("Name");
            PasswordCallback pswdCallback = new PasswordCallback("Password",false);
            this.callbackhandler.handle(new Callback[] {nmCallback, pswdCallback});

            String loginName = nmCallback.getName();
            char[] pswd = pswdCallback.getPassword();
            
            HashMap<String,Object> foundMap = findUser(SecurityDBUtils.getConnection(), loginName);
            foundUser = (User) (foundMap != null ? foundMap.get("user") : null);
            foundPswd = (String) (foundMap != null ? foundMap.get("pswd") : null);
            
            if (foundUser != null) {
                boolean result = checkPswd(foundUser.getLoginname(),foundPswd,pswd);
                if (result) {
                    List<Group> groupsList = new JDBCQueryTemplate<Group>(SecurityDBUtils.getConnection()){
                        @Override
                        public boolean handleRow(ResultSet rs, List<Group> retList) throws SQLException {
                            retList.add(SecurityDBUtils.createGroup(rs));
                            return true;
                        }
                        
                    }.executeQuery("select * from user_group_mapping where user_id=?", foundUser.getId());
                    
                    //TODO:Zmenit
                    ((UserImpl)foundUser).setGroups((Group[]) groupsList.toArray(new Group[groupsList.size()]));
                }
                this.logged = result;
            } else {
                this.logged = false;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (UnsupportedCallbackException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return this.logged;
    }

    public static HashMap<String, Object> findUser(Connection secConnection, String loginName) {
        List<HashMap<String, Object>> list = new JDBCQueryTemplate<HashMap<String, Object>>(secConnection){
            @Override
            public boolean handleRow(ResultSet rs, List<HashMap<String, Object>> retList) throws SQLException {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("user", SecurityDBUtils.createUser(rs));
                map.put("pswd",rs.getString(PSWD_COL));
                retList.add(map);
                return false;
            }
            
        }.executeQuery("select * from user_entity where loginname=?", loginName);
        return list.isEmpty() ? null : list.get(0);
    }

    public static boolean checkPswd(String string, String dbPswd, char[] pswd) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String digestedPassword = PasswordDigest.messageDigest(new String(pswd));
        return dbPswd.equals(digestedPassword);
    }

    @Override
    public boolean commit()  throws LoginException {
        if (!this.logged ) return false;

        K4UserPrincipal userPrincipal = new K4UserPrincipal(foundUser);
        K4RolePrincipal rolePrincipal = new K4RolePrincipal("formalRole");
        // vyhodit .. 
        K4RolePrincipal webRole = new K4RolePrincipal("krameriusAdmin");

        assignPrincipal(userPrincipal);
        assignPrincipal(rolePrincipal);
        assignPrincipal(webRole);

        return true;
    }

    public void assignPrincipal(Principal principal) {
        if (!subject.getPrincipals().contains(principal)) {
            subject.getPrincipals().add(principal);
        }
    }

    @Override
    public boolean abort() throws LoginException {
        //throw new IllegalStateException("illegal call");
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        this.subject.getPrincipals().clear();
        return true;
    }

}