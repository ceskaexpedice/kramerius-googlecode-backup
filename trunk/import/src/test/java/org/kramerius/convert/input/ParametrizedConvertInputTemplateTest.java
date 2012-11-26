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
package org.kramerius.convert.input;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;

import junit.framework.Assert;

import org.apache.commons.configuration.Configuration;
import org.easymock.EasyMock;
import org.junit.Test;
import org.kramerius.convert.input.ParametrizedConvertInputTemplate.OtherSettingsTemplate;

import com.google.inject.Provider;

import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.utils.conf.KConfiguration;

/**
 * @author pavels
 *
 */
public class ParametrizedConvertInputTemplateTest {

    public static final String BUNDLES ="" +
    		"convert.directory=convert.directory\n" +
    		"target.directory=target.directory\n" +
    		"convert.selection.dialog=convert.selection.dialog";
    
    public interface _TestLocaleProvider extends Provider<Locale> {}
    
    @Test
    public void testInputTemplateTest() throws IOException {
        Provider<Locale> localeProvider = EasyMock.createMock(_TestLocaleProvider.class);
        EasyMock.expect(localeProvider.get()).andReturn(Locale.getDefault()).anyTimes();
        
        ResourceBundleService resb = EasyMock.createMock(ResourceBundleService.class);
        PropertyResourceBundle resourceBundle = new PropertyResourceBundle(new StringReader(BUNDLES));
        EasyMock.expect(resb.getResourceBundle("labels", Locale.getDefault())).andReturn(resourceBundle).anyTimes();
        
        KConfiguration  conf = EasyMock.createMock(KConfiguration.class);
        EasyMock.expect(conf.getProperty("import.directory")).andReturn(System.getProperty("user.dir")).anyTimes();

        EasyMock.expect(conf.getProperty("convert.target.directory")).andReturn(System.getProperty("user.dir")).anyTimes();
        EasyMock.expect(conf.getProperty("convert.directory")).andReturn(System.getProperty("user.dir")).anyTimes();

        Configuration  subConfObject = EasyMock.createMock(Configuration.class);
        EasyMock.expect(conf.getConfiguration()).andReturn(subConfObject).anyTimes();

        EasyMock.expect(subConfObject.getBoolean("ingest.skip")).andReturn(true).anyTimes();
        EasyMock.expect(subConfObject.getBoolean("ingest.startIndexer")).andReturn(true).anyTimes();
        EasyMock.expect(subConfObject.getBoolean("convert.defaultRights")).andReturn(true).anyTimes();

        EasyMock.replay(localeProvider,resb, conf, subConfObject);
        
        ParametrizedConvertInputTemplate temp = new ParametrizedConvertInputTemplate();
        temp.configuration=conf;
        temp.localesProvider = localeProvider;
        temp.resourceBundleService = resb;
        
        StringWriter nstr = new StringWriter();
        temp.renderInput(null,nstr, new Properties());
        Assert.assertNotNull(nstr.toString());
    }

    
    @Test
    public void testTemplateChoose() {
        OtherSettingsTemplate template1 = ParametrizedConvertInputTemplate.OtherSettingsTemplate.disectTemplate(true, true);
        Assert.assertEquals(template1, OtherSettingsTemplate.importFedoraStartIndexer);

        OtherSettingsTemplate template2 = ParametrizedConvertInputTemplate.OtherSettingsTemplate.disectTemplate(false, true);
        Assert.assertEquals(template2, OtherSettingsTemplate.noFedoraNoIndexer);

        OtherSettingsTemplate template3 = ParametrizedConvertInputTemplate.OtherSettingsTemplate.disectTemplate(false, false);
        Assert.assertEquals(template3, OtherSettingsTemplate.noFedoraNoIndexer);

        OtherSettingsTemplate template4 = ParametrizedConvertInputTemplate.OtherSettingsTemplate.disectTemplate(true, false);
        Assert.assertEquals(template4, OtherSettingsTemplate.importFedoraNoIndexer);
    }
}
