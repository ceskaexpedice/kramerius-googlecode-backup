package com.qbizm.kramerius.imptool.poc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.qbizm.kramerius.imp.jaxb.DigitalObject;
import com.qbizm.kramerius.imp.jaxb.Monograph;
import com.qbizm.kramerius.imp.jaxb.periodical.Periodical;
import com.qbizm.kramerius.imptool.poc.convertor.MonographConvertor;
import com.qbizm.kramerius.imptool.poc.convertor.PeriodicalConvertor;
import com.qbizm.kramerius.imptool.poc.valueobj.ConvertorConfig;
import com.qbizm.kramerius.imptool.poc.valueobj.ServiceException;

import cz.incad.kramerius.utils.IOUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;


/**
 * Nastroj pro konverzi XML z Krameria do formatu Fedora Object XML
 * 
 * @author xholcik
 */
public class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    private static Marshaller marshaller = null;
    private static Unmarshaller unmarshaller = null;

    public static void main(String[] args) {

        if (args.length > 4) {
            System.out.println("KrameriusXML to FOXML conversion tool.\n");
            System.out.println("Usage: conversion-tool useDB defaultVisibility <input-file> [<output-folder>]");
            System.exit(1);
        }
        
        boolean useDB = Boolean.parseBoolean(args[0]);
        boolean defaultVisibility = Boolean.parseBoolean(args[1]);

        String importRoot = null;
        if (args.length == 2){
            importRoot = KConfiguration.getInstance().getConfiguration().getString("migration.directory");
        } else{
            importRoot = args[2];
        }
        String exportRoot = null;
        if (args.length == 4) {
            exportRoot = args[3];
        } else {
            exportRoot = importRoot + "-converted";
        }

        convert(importRoot, exportRoot, useDB, defaultVisibility);

    }

    
    
    public static String convert(String importRoot, String exportRoot, boolean useDB, boolean defaultVisibility) {
        System.setProperty("java.awt.headless", "true");
        StringBuffer convertedURI = new StringBuffer();
        if (useDB){
            initDB();
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Monograph.class, DigitalObject.class, Periodical.class);
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            try{
            	marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapperInternalImpl());
            } catch (PropertyException ex){
            	marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl());
            }
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd");

            unmarshaller = jaxbContext.createUnmarshaller();
            

        } catch (Exception e) {
            log.error("Cannot init JAXB", e);
            throw new RuntimeException(e);
        }

        File importFile = new File(importRoot);
        if (!importFile.exists()) {
            System.err.println("Import root folder doesn't exist: " + importFile.getAbsolutePath());
            System.exit(1);
        }

        visitAllDirsAndFiles(importFile, importRoot, exportRoot,  useDB, defaultVisibility,  convertedURI);
        if (conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                
            }
        }
        return convertedURI.toString();
    }
    
    static Connection conn = null;
    
    // "jdbc:postgresql://localhost:5432/kramerius", "kramerius", "f8TasR"
    private static void initDB() {
        try {
        	Configuration configuration = KConfiguration.getInstance().getConfiguration();
            Class.forName(configuration.getString("k3.db.driver"));
            String url = configuration.getString("k3.db.url");
            String user = configuration.getString("k3.db.user");
            String pwd = configuration.getString("k3.db.password");
            conn = DriverManager.getConnection(url, user, pwd);
            conn.setAutoCommit(true);
            log.info("Database initialized.");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }
    
    

    private static void visitAllDirsAndFiles(File importFile, String importRoot, String exportRoot, boolean useDB, boolean defaultVisibility, StringBuffer convertedURI) {

        if (importFile.isDirectory()) {
            String subFolderName = importFile.getAbsolutePath().substring(importRoot.length());

            String exportFolder = exportRoot + subFolderName;

            File exportFolderFile = IOUtils.checkDirectory(exportFolder);
            IOUtils.cleanDirectory(exportFolderFile);
            File[] children = importFile.listFiles();
            for (int i = 0; i < children.length; i++) {
                visitAllDirsAndFiles(children[i], importRoot, exportRoot,  useDB, defaultVisibility, convertedURI);
            }
        } else {
            if (importFile.getName().endsWith(".xml")) {

                String importFolder = importFile.getParent();
                if (importFolder == null) {
                    importFolder = ".";
                }
                String subFolderName = importFolder.substring(importRoot.length());

                String exportFolder = exportRoot + subFolderName;

                ConvertorConfig config = new ConvertorConfig();
                config.setMarshaller(marshaller);
                config.setExportFolder(exportFolder);
                config.setImportFolder(importFolder);
                if (useDB){
                    config.setDbConnection(conn);
                }
                config.setDefaultVisibility(defaultVisibility);
                int l=5; 
                try{
                    l=KConfiguration.getInstance().getConfiguration().getInt("contractNo.length");
                }catch(NumberFormatException ex){
                    log.error("Cannot parse property contractNo.length", ex);
                }
                config.setContractLength(l);
                try {
                    convertOneDirectory(unmarshaller, importFile, config, convertedURI);
                } catch (InterruptedException e) {
                    log.error("Cannot convert "+importFile, e);
                } catch (JAXBException e) {
                    log.error("Cannot convert "+importFile, e);
                } catch (FileNotFoundException e) {
                	log.error("Cannot convert "+importFile, e);
				} catch (SAXException e) {
					log.error("Cannot convert "+importFile, e);
				}

            }
        }
    }

    private static void convertOneDirectory(Unmarshaller unmarshaller, File importFile, ConvertorConfig config, StringBuffer convertedURI) throws InterruptedException, JAXBException, FileNotFoundException, SAXException {
        long timeStart = System.currentTimeMillis();

        long before = getFreeMem();
        
        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setEntityResolver(new EntityResolver(){
			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
					return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
			}
		});
        SAXSource saxSource = new SAXSource( reader, new InputSource( new FileInputStream(importFile) ) );
		Object source = unmarshaller.unmarshal(saxSource);

		long after = getFreeMem();
        if (log.isInfoEnabled()) {
            log.info("Memory eaten: " + ((after - before) / 1024) + "KB");
        }

        int objectCounter = 0;
        try {
            if (source instanceof Monograph) {
                MonographConvertor mc = new MonographConvertor(config);
                Monograph monograph = (Monograph) source;
                mc.convert(monograph, convertedURI);
                objectCounter = mc.getObjectCounter();
            } else if (source instanceof Periodical) {
                PeriodicalConvertor pc = new PeriodicalConvertor(config);
                Periodical periodical = (Periodical) source;
                pc.convert(periodical, convertedURI);
                objectCounter = pc.getObjectCounter();
            } else {
                throw new UnsupportedOperationException("Unsupported object class: " + source.getClass());
            }
        } catch (ServiceException e) {
            log.error(importFile.getName() + ": conversion failed", e);
        }
        long timeFinish = System.currentTimeMillis();
        if (log.isInfoEnabled()) {
            log.info("Elapsed time: " + ((timeFinish - timeStart) / 1000.0) + " seconds. "+objectCounter + " digital objects (files) written.");
        }
    }

 
    /**
   * 
   */
    
    static class NamespacePrefixMapperInternalImpl extends com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper {

        public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
            if ("info:fedora/fedora-system:def/foxml#".equals(namespaceUri)) {
                return "foxml";
            }
            if ("http://www.loc.gov/mods/v3".equals(namespaceUri)) {
                return "mods";
            }
            if ("http://purl.org/dc/elements/1.1/".equals(namespaceUri)){
                return "dc";
            }
            if ("http://www.openarchives.org/OAI/2.0/oai_dc/".equals(namespaceUri)){
                return "oai_dc";
            }
            if ("info:fedora/fedora-system:def/model#".equals(namespaceUri)){
                return "fedora-model";
            }
            if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceUri)){
                return "rdf";
            }
            if ("http://www.nsdl.org/ontologies/relationships#".equals(namespaceUri)){
                return "kramerius";
            }
            return suggestion;
        }

    }
    
    static class NamespacePrefixMapperImpl extends com.sun.xml.bind.marshaller.NamespacePrefixMapper {

        public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
            if ("info:fedora/fedora-system:def/foxml#".equals(namespaceUri)) {
                return "foxml";
            }
            if ("http://www.loc.gov/mods/v3".equals(namespaceUri)) {
                return "mods";
            }
            if ("http://purl.org/dc/elements/1.1/".equals(namespaceUri)){
                return "dc";
            }
            if ("http://www.openarchives.org/OAI/2.0/oai_dc/".equals(namespaceUri)){
                return "oai_dc";
            }
            if ("info:fedora/fedora-system:def/model#".equals(namespaceUri)){
                return "fedora-model";
            }
            if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceUri)){
                return "rdf";
            }
            if ("http://www.nsdl.org/ontologies/relationships#".equals(namespaceUri)){
                return "kramerius";
            }
            return suggestion;
        }

    }

    private static long getFreeMem() throws InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        Thread.sleep(100);
        return runtime.totalMemory() - runtime.freeMemory();
    }

}
