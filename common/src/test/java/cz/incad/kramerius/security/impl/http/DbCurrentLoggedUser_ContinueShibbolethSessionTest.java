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

import static org.easymock.EasyMock.createMockBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Named;

import cz.incad.kramerius.security.IsActionAllowed;
import cz.incad.kramerius.security.Role;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.security.UserManager;
import cz.incad.kramerius.security.impl.RoleImpl;
import cz.incad.kramerius.security.impl.UserImpl;
import cz.incad.kramerius.security.utils.UserUtils;
import cz.incad.kramerius.shib.utils.ShibbolethUtilsTest;
import cz.incad.kramerius.users.LoggedUsersSingleton;

/**
 * Tests situation when session keeps logged user and shibboleth session hasn't been broken.
 * @author pavels
 *
 */
public class DbCurrentLoggedUser_ContinueShibbolethSessionTest {

    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(DbCurrentLoggedUser_ShibbLoggingTest.class.getName());
    
    @Test
    public void test() {
        // expecting user
        UserImpl user = new UserImpl(-1, "", "", "shibuser", 1);
        Role role = new RoleImpl(1, "common_users", -1);
        user.setGroups(new Role[] {role});
        
        Injector inj = Guice.createInjector(new TestModule(user));
        DbCurrentLoggedUser dbCurUser = inj.getInstance(DbCurrentLoggedUser.class);
        
        User gotUserFromMock = dbCurUser.get();
        junit.framework.Assert.assertEquals(gotUserFromMock, user);
    }
    
    
    static class TestModule extends AbstractModule {
        
        private User user;
        private HashMap<String, Object> sessionStoreMap = new HashMap<String, Object>();
        
        private TestModule(User user) {
            super();
            this.user = user;
            
            sessionStoreMap.put(UserUtils.LOGGED_USER_PARAM, user);
            sessionStoreMap.put(UserUtils.LOGGED_USER_KEY_PARAM, "0xAAA");
            sessionStoreMap.put("SHIB_USER_KEY", "true");
        }

        @Override
        protected void configure() {
            try {
                DbCurrentLoggedUser dbCurUser = createMockBuilder(DbCurrentLoggedUser.class)
                .withConstructor()
                // evaluate withnout shibrules
                .addMockedMethod("evaluateShibRules")
                // evaluate without save rights to session
                .addMockedMethod("saveRightsIntoSession")
                .createMock();
                
                
                dbCurUser.evaluateShibRules(user);
                dbCurUser.saveRightsIntoSession(user);
                
                EasyMock.replay(dbCurUser);

                bind(DbCurrentLoggedUser.class).toInstance(dbCurUser);
            } catch (FileNotFoundException e) {
                LOGGER.log(Level.SEVERE,e.getMessage(),e);
            } catch (RecognitionException e) {
                LOGGER.log(Level.SEVERE,e.getMessage(),e);
            } catch (TokenStreamException e) {
                LOGGER.log(Level.SEVERE,e.getMessage(),e);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE,e.getMessage(),e);
            }
            
        }
        
        @Provides
        @Named("kramerius4")
        public Connection getConnection() {
            Connection con = EasyMock.createMock(Connection.class);
            EasyMock.replay(con);
            return con;
        }

        @Provides
        public LoggedUsersSingleton getLoggedUsersSingleton() {
            LoggedUsersSingleton sing = EasyMock.createMock(LoggedUsersSingleton.class);
            EasyMock.expect(sing.registerLoggedUser(this.user)).andReturn("0xAAA");
            EasyMock.replay(sing);
            return sing;
        }

        @Provides
        public UserManager getUserManager() {
            UserManager um = EasyMock.createMock(UserManager.class);
            EasyMock.expect(um.findCommonUsersRole()).andReturn(new RoleImpl(1, "common_users", -1)).anyTimes();
            EasyMock.replay(um);
            return um;
        }

        @Provides
        public IsActionAllowed getIsActionAllowed() {
            IsActionAllowed all = EasyMock.createMock(IsActionAllowed.class);
            EasyMock.replay(all);
            return all;
        }
        
        @Provides
        public HttpServletRequest getRequest() {

            final Hashtable<String,String> table = ShibbolethUtilsTest.getLoggedShibTable();
            
            HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
            EasyMock.expect(request.getHeaderNames()).andAnswer(new IAnswer<Enumeration>() {

                @Override
                public Enumeration answer() throws Throwable {
                    return table.keys();
                }
            }).anyTimes();

            
            EasyMock.expect(request.getUserPrincipal()).andAnswer(new IAnswer<Principal>() {

                @Override
                public Principal answer() throws Throwable {
                    return null;
                }
            }).anyTimes();
         


            // ((httpServletRequest.getParameter(UserUtils.USER_NAME_PARAM) != null) && (httpServletRequest.getParameter(UserUtils.PSWD_PARAM) != null)) {
            EasyMock.expect(request.getParameter(UserUtils.USER_NAME_PARAM)).andAnswer(new IAnswer<String>() {

                @Override
                public String answer() throws Throwable {
                    return null;
                }
            }).anyTimes();
            
            EasyMock.expect(request.getParameter(UserUtils.PSWD_PARAM)).andAnswer(new IAnswer<String>() {

                @Override
                public String answer() throws Throwable {
                    return null;
                }
            }).anyTimes();

            
            Enumeration<String> keys = table.keys();
            while(keys.hasMoreElements()) {
                final String k = keys.nextElement();
                EasyMock.expect(request.getHeader(k)).andAnswer(new IAnswer<String>() {

                    @Override
                    public String answer() throws Throwable {
                        return table.get(k);
                    }
                }).anyTimes();
            }
            
            
            final HttpSession session = EasyMock.createMock(HttpSession.class);
            
            getSessionExpectation(request, session);
            getSessionParamsExpectations(session);
            setSessionParamsExpectations(session);
            removeSessionParamsExpectations(session);
            
            EasyMock.replay(request,session);
            
            return request;
        }

        public void removeSessionParamsExpectations(final HttpSession session) {

            session.removeAttribute(AbstractLoggedUserProvider.SECURITY_FOR_REPOSITORY_KEY);
            EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {

                @Override
                public Object answer() throws Throwable {
                    sessionStoreMap.remove(AbstractLoggedUserProvider.SECURITY_FOR_REPOSITORY_KEY);
                    return null;
                }
            });
            
            session.removeAttribute(UserUtils.LOGGED_USER_PARAM);
            EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {

                @Override
                public Object answer() throws Throwable {
                    sessionStoreMap.remove(UserUtils.LOGGED_USER_PARAM);
                    return null;
                }
            });

            
        }
        
        public void setSessionParamsExpectations(final HttpSession session) {
            final User user = this.user;
            session.setAttribute(UserUtils.LOGGED_USER_PARAM,this.user);
            EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {

                @Override
                public Object answer() throws Throwable {
                    sessionStoreMap.put(UserUtils.LOGGED_USER_PARAM, user);
                    return null;
                }
            });
            session.setAttribute(UserUtils.LOGGED_USER_KEY_PARAM,"0xAAA");
            EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {

                @Override
                public Object answer() throws Throwable {
                    sessionStoreMap.put(UserUtils.LOGGED_USER_KEY_PARAM, "0xAAA");
                    return null;
                }
            });
            
            session.setAttribute("SHIB_USER_KEY","true");
            EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {

                @Override
                public Object answer() throws Throwable {
                    sessionStoreMap.put("SHIB_USER_KEY", "true");
                    return null;
                }
            });
        }
        
        public void getSessionParamsExpectations(final HttpSession session) {
            EasyMock.expect(session.getAttribute(DbCurrentLoggedUser.SHIB_USER_KEY)).andAnswer(new IAnswer<String>() {

                @Override
                public String answer() throws Throwable {
                    return (String) sessionStoreMap.get(DbCurrentLoggedUser.SHIB_USER_KEY);
                }
                
            }).anyTimes();
            
            EasyMock.expect(session.getAttribute("loggedUser")).andAnswer(new IAnswer<User>() {
                @Override
                public User answer() throws Throwable {
                    return (User) sessionStoreMap.get("loggedUser");
                }
            }).anyTimes();

            EasyMock.expect(session.getAttribute("securityForRepository")).andAnswer(new IAnswer<String>() {
                @Override
                public String answer() throws Throwable {
                    return (String) sessionStoreMap.get("securityForRepository");
                }
            }).anyTimes();
        }

        public void getSessionExpectation(HttpServletRequest request, final HttpSession session) {
            EasyMock.expect(request.getSession()).andAnswer(new IAnswer<HttpSession>() {

                @Override
                public HttpSession answer() throws Throwable {
                    return session;
                }
            }).anyTimes();

            EasyMock.expect(request.getSession(true)).andAnswer(new IAnswer<HttpSession>() {

                @Override
                public HttpSession answer() throws Throwable {
                    return session;
                }
            }).anyTimes();
        }
        
    }    
}
