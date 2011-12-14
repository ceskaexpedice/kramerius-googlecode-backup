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
package cz.incad.kramerius.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import cz.incad.kramerius.AbstractGuiceTestCase;
import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.security.impl.ClassRightParam;
import cz.incad.kramerius.security.impl.MovingWallRightParam;
import cz.incad.kramerius.security.impl.RightImpl;
import cz.incad.kramerius.security.impl.RightParamEvaluatingContextFactoryImpl;
import cz.incad.kramerius.utils.XMLUtils;

public class MovingWallExample extends AbstractGuiceTestCase {
    
    @Test
    public void testMovingWall() throws IOException, ParserConfigurationException, SAXException, RightParamEvaluateContextException {
        Injector injector = injector();
        FedoraAccess fedoraAccess = injector.getInstance(Key.get(FedoraAccess.class, Names.named("securedFedoraAccess")));
        RightsManager rman = injector.getInstance(RightsManager.class);

        Document dcPageDoc = XMLUtils.parseDocument(new ByteArrayInputStream(getDrobnustkyDC().getBytes()), true);
        String uuid = "aaa-bbb-ccc";
        String action = "thumbViewer";
        User user = createPavelStastny();

        ClassRightParam param = new ClassRightParam(MovingWallRightParam.class);
        RightImpl rightImpl = new RightImpl(param, uuid, action, user);

        EasyMock.expect(rman.findRight(uuid, action, user)).andReturn(rightImpl);
        EasyMock.replay(rman);

        EasyMock.expect(fedoraAccess.getDC(uuid)).andReturn(dcPageDoc);
        EasyMock.replay(fedoraAccess);
        
        
        RightsManager expectedRMan = injector.getInstance(RightsManager.class);
        Right foundRight = expectedRMan.findRight(uuid, action, user);
        
        RightParamEvaluatingContext ctx = injector.getInstance(RightParamEvaluatingContextFactory.class).create(uuid, user);
        
        TestCase.assertNotNull(ctx.getFedoraAccess());
        TestCase.assertNotNull(ctx.getUUID());
        TestCase.assertNotNull(ctx.getUser());
        TestCase.assertNotNull(foundRight);

        // evaluate rightParam
        boolean evaluated = foundRight.evaluate(ctx);

        TestCase.assertTrue(evaluated);
    
    }

    @Override
    protected Injector injector() {
        return Guice.createInjector(new SecurityGuiceModule());
    }

    
    public static User createPredplatitel() {
        return new User() {
            @Override
            public int getId() {
                return 222;
            }

            @Override
            public String getFirstName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getSurname() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getLoginname() {
                // TODO Auto-generated method stub
                return null;
            }
            
            
        };
    }
    
    public static User createCommonUser() {
        return new User() {

            @Override
            public int getId() {
                return 111;
            }

            @Override
            public String getFirstName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getSurname() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getLoginname() {
                // TODO Auto-generated method stub
                return null;
            }
            

        };
    }
    
    public static User createPavelStastny() {
        return new User() {
            
            @Override
            public int getId() {
                return 333;
            }

            @Override
            public String getFirstName() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getSurname() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public String getLoginname() {
                // TODO Auto-generated method stub
                return null;
            }
            
            
        };
    }
    
    public static String getDrobunstkyPageDC() {
        return "<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">"+ 
        "<dc:title>[11]</dc:title> "+
        "<dc:type>model:page</dc:type>"+ 
        "<dc:identifier>uuid:4319b460-b03b-11dd-83ca-000d606f5dc6</dc:identifier> "+
        "<dc:rights>policy:private</dc:rights> "+
        "</oai_dc:dc>";
    }
    
    public static String getDrobnustkyDC() {
        return "<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" " +
        		"xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">"+ 
  "<dc:title>Drobnustky</dc:title>"+ 
  "<dc:creator>Doucha Frantisek</dc:creator>"+ 
  "<dc:subject>ddc:neuvedeno</dc:subject>"+ 
  "<dc:subject>udc:821-93</dc:subject>"+ 
  "<dc:subject>ddc:neuvedeno</dc:subject>"+ 
  "<dc:subject>udc:821.162.3-1</dc:subject>"+ 
  "<dc:publisher>Mikolas Lehmann</dc:publisher>"+ 
  "<dc:date>1862</dc:date>"+ 
  "<dc:type>model:monograph</dc:type>"+ 
  "<dc:identifier>uuid:0eaa6730-9068-11dd-97de-000d606f5dc6</dc:identifier>"+ 
  "<dc:identifier>contract:4011400001.djvu</dc:identifier>"+ 
  "<dc:language>cze</dc:language>"+ 
  "<dc:rights>policy:private</dc:rights>"+ 
  "</oai_dc:dc>";
    }
}