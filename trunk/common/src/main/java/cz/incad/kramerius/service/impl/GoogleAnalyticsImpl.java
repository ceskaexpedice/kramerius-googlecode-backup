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
package cz.incad.kramerius.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import cz.incad.kramerius.service.GoogleAnalytics;
import cz.incad.kramerius.utils.IOUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;

public class GoogleAnalyticsImpl implements GoogleAnalytics {

    @Override
    public boolean isCodeDefined() {
        File file = KConfiguration.getInstance().getGoogleCodeFile();
        return file.exists() && file.canRead();
    }

    @Override
    public String getCodeDefine() throws  IOException {
        File file = KConfiguration.getInstance().getGoogleCodeFile();
        return IOUtils.readAsString(new FileInputStream(file), Charset.forName("UTF-8"),true);
    }
}
