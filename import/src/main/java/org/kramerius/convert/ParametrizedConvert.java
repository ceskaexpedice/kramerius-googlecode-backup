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
package org.kramerius.convert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.kramerius.Import;
import org.xml.sax.SAXException;

import com.qbizm.kramerius.imptool.poc.Main;
import com.qbizm.kramerius.imptool.poc.valueobj.ServiceException;

import cz.incad.kramerius.processes.annotations.DefaultParameterValue;
import cz.incad.kramerius.processes.annotations.ParameterName;
import cz.incad.kramerius.processes.annotations.Process;
import cz.incad.kramerius.utils.conf.KConfiguration;

public class ParametrizedConvert {

    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ParametrizedConvert.class.getName());
    
    
    
    @Process
    public static void process(
            @ParameterName("convertDirectory") File convertDirectory, 
            @ParameterName("convertTargetDirectory") File convertTargetDirectory, 
            @ParameterName("ingestSkip") Boolean ingestSkip,
            @ParameterName("indexerStart")Boolean startIndexer, 
            @ParameterName("defaultRights")Boolean defaultRights) throws FileNotFoundException, InterruptedException, JAXBException, SAXException, ServiceException {
        
        LOGGER.info("convert directory "+convertDirectory.getAbsolutePath());
        LOGGER.info("target directory "+convertTargetDirectory.getAbsolutePath());
        LOGGER.info("ingest skip "+ingestSkip);
        LOGGER.info("indexer start "+startIndexer);
        LOGGER.info("default rights "+defaultRights);
    }
}
