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
package org.kramerius.importmets.parametrized;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.kramerius.importmets.MetsConvertor;
import org.kramerius.importmets.valueobj.ServiceException;
import org.xml.sax.SAXException;

import cz.incad.kramerius.processes.annotations.ParameterName;
import cz.incad.kramerius.processes.annotations.Process;
import cz.incad.kramerius.processes.impl.ProcessStarter;

/**
 * Parametrized mets NKD import
 * @author pavels
 */
public class ParametrizedMetsNKDImport {

    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ParametrizedMetsNKDImport.class.getName());
    
    @Process
    public static void process(
                @ParameterName("convertDirectory")File convertDirectory, 
                @ParameterName("convertTargetDirectory")File targetDirectory, 
                @ParameterName("ingestSkip")Boolean ingestSkip,
                @ParameterName("startIndexer")Boolean startIndexer,
                @ParameterName("defaultRights")Boolean defaultRights) {
        
        System.setProperty("convert.defaultRights", defaultRights.toString());
        System.setProperty("ingest.startIndexer", startIndexer.toString());
        System.setProperty("ingest.skip", ingestSkip.toString());

        
        try {
            //TODO: I18N
            ProcessStarter.updateName("Parametrizovany import NDK METS z '"+convertDirectory.getAbsolutePath()+"'");
            MetsConvertor.main(new String[] {defaultRights.toString(), convertDirectory.getAbsolutePath(), targetDirectory.getAbsolutePath()});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
}
