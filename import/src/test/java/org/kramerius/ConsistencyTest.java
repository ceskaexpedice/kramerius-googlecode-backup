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
/**
 * 
 */
package org.kramerius;

import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.replay;
import static org.kramerius.fedora.impl.ImportDataPrepare.narodniListyRelsExt;
import static org.kramerius.fedora.impl.ImportDataPrepare.notConsistentNarodniListyRelsExt;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Test;
import org.kramerius.Consistency.NotConsistentRelation;
import org.xml.sax.SAXException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.ObjectPidsPath;
import cz.incad.kramerius.ProcessSubtreeException;
import cz.incad.kramerius.SolrAccess;
import cz.incad.kramerius.fedora.impl.DataPrepare;
import cz.incad.kramerius.impl.FedoraAccessImpl;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.utils.pid.LexerException;

/**
 * @author pavels
 */
public class ConsistencyTest {

    @Test
    public void shouldPassProcess() throws IOException, ProcessSubtreeException, LexerException, ParserConfigurationException, SAXException {
        FedoraAccess fa = createMockBuilder(FedoraAccessImpl.class)
        .withConstructor(KConfiguration.getInstance())
        .addMockedMethod("getRelsExt")
        .createMock();

        narodniListyRelsExt(fa);

        SolrAccess solrAccess = EasyMock.createMock(SolrAccess.class);
        Set<String> keys = DataPrepare.PATHS_MAPPING.keySet();
        for (String key : keys) {
            EasyMock.expect(solrAccess.getPath(key)).andReturn(new ObjectPidsPath[] { DataPrepare.PATHS_MAPPING.get(key) }).anyTimes();
        }
        

        replay(fa, solrAccess);

        Consistency consistency = new Consistency();
        Injector injector = Guice.createInjector(new _Module(fa,  solrAccess));
        injector.injectMembers(consistency);
        
        List<NotConsistentRelation> notConsitent = consistency.checkConsitency("uuid:b2f18fb0-91f6-11dc-9f72-000d606f5dc6", false);

        Assert.assertTrue(notConsitent.isEmpty());
    }


    @Test
    public void shouldFailProcess() throws IOException, ProcessSubtreeException, LexerException, ParserConfigurationException, SAXException {
        FedoraAccess fa = createMockBuilder(FedoraAccessImpl.class)
        .withConstructor(KConfiguration.getInstance())
        .addMockedMethod("getRelsExt")
        .createMock();

        notConsistentNarodniListyRelsExt(fa);

        SolrAccess solrAccess = EasyMock.createMock(SolrAccess.class);
        Set<String> keys = DataPrepare.PATHS_MAPPING.keySet();
        for (String key : keys) {
            EasyMock.expect(solrAccess.getPath(key)).andReturn(new ObjectPidsPath[] { DataPrepare.PATHS_MAPPING.get(key) }).anyTimes();
        }
        

        replay(fa, solrAccess);

        Consistency consistency = new Consistency();
        Injector injector = Guice.createInjector(new _Module(fa,  solrAccess));
        injector.injectMembers(consistency);
        
        List<NotConsistentRelation> notConsitent = consistency.checkConsitency("uuid:b2f18fb0-91f6-11dc-9f72-000d606f5dc6", false);
        Assert.assertTrue(!notConsitent.isEmpty());
        Assert.assertTrue(notConsitent.size() == 16); // stranky prvniho cisla
    }

    static class _Module extends AbstractModule {

        private FedoraAccess fedoraAccess;
        private SolrAccess solrAccess;
        
        
        public _Module(FedoraAccess fedoraAccess, SolrAccess solrAccess) {
            super();
            this.fedoraAccess = fedoraAccess;
            this.solrAccess = solrAccess;
        }


        @Override
        protected void configure() {
            bind(KConfiguration.class).toInstance(KConfiguration.getInstance());
            bind(FedoraAccess.class).toInstance(fedoraAccess);
            bind(SolrAccess.class).toInstance(solrAccess);
        }
    }

}
