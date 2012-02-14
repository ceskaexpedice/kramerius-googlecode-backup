package cz.incad.kramerius.lp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;

import javax.swing.border.TitledBorder;

import org.w3c.dom.Document;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import cz.incad.kramerius.FedoraAccess;
import cz.incad.kramerius.ObjectPidsPath;
import cz.incad.kramerius.SolrAccess;
import cz.incad.kramerius.lp.guice.ArgumentLocalesProvider;
import cz.incad.kramerius.lp.guice.PDFModule;
import cz.incad.kramerius.lp.utils.DecriptionHTML;
import cz.incad.kramerius.lp.utils.FileUtils;
import cz.incad.kramerius.lp.utils.PackUtils;
import cz.incad.kramerius.pdf.GeneratePDFService;
import cz.incad.kramerius.pdf.utils.TitlesUtils;
import cz.incad.kramerius.processes.impl.ProcessStarter;
import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.utils.DCUtils;
import cz.incad.kramerius.utils.IOUtils;
import cz.incad.kramerius.utils.conf.KConfiguration;
import cz.incad.kramerius.utils.pid.LexerException;
import cz.incad.kramerius.utils.pid.PIDParser;

/**
 * Staticky export do pdf
 * @author pavels
 */
public class PDFExport {
	
	public static final java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(PDFExport.class.getName());
	
	public static void main(String[] args) throws Exception {
	    LOGGER.info("starting static export ...");
	    if (args.length >= 4) {
			LOGGER.info("Parameters "+args[0]+", "+args[1]+", "+args[2]+", "+args[3]);

			String outputFolderName = args[0];
			Medium medium = Medium.valueOf(args[1]);
			String pid = args[2];
			
			String djvuUrl = args[3];
			String i18nUrl = args[4];
			
			if (!djvuUrl.startsWith("http")) {
			    String applicationURL = KConfiguration.getInstance().getApplicationURL();
			    djvuUrl = applicationURL + (djvuUrl.startsWith("/") ? "" : "/") + djvuUrl;
			}
			
			if (!i18nUrl.startsWith("http")) {
                String applicationURL = KConfiguration.getInstance().getApplicationURL();
                i18nUrl = applicationURL + (i18nUrl.startsWith("/") ? "" : "/") + i18nUrl;
			}
			LOGGER.info("imgurl = "+djvuUrl);
			LOGGER.info("i18nurl = "+i18nUrl);

			
			if (args.length > 6) {
				LOGGER.info("Country "+args[5]);
				LOGGER.info("Lang "+args[6]);
				System.setProperty(ArgumentLocalesProvider.ISO3COUNTRY_KEY, args[5]);
				System.setProperty(ArgumentLocalesProvider.ISO3LANG_KEY, args[6]);
			}
			
			PIDParser pidParser = new PIDParser(pid);
			pidParser.objectPid();
			File uuidFolder = new File(getTmpDir(), pidParser.getObjectId());
			if (uuidFolder.exists()) { 
				FileUtils.deleteRecursive(uuidFolder);
				if (!uuidFolder.delete()) throw new RuntimeException("cannot delete folder '"+uuidFolder.getAbsolutePath()+"'");
			}
			
			Injector injector = Guice.createInjector(new PDFModule());
			String titleFromDC = null;
			if (System.getProperty("uuid") != null) {
				titleFromDC = updateProcessName(pid, injector, medium);
			} else {
				FedoraAccess fa = injector.getInstance(Key.get(FedoraAccess.class, Names.named("rawFedoraAccess"))); 
                Document dc = fa.getDC(pid);
				titleFromDC = DCUtils.titleFromDC(dc);
				
				
	             
			}


            FedoraAccess fa = injector.getInstance(Key.get(FedoraAccess.class, Names.named("rawFedoraAccess"))); 
            Locale locale = injector.getProvider(Locale.class).get();

			generatePDFs(pid, uuidFolder, injector,djvuUrl,i18nUrl);
			createFSStructure(uuidFolder, new File(outputFolderName), medium, titleFromDC);
		}
	}
	

	private static String updateProcessName(String uuid, Injector injector, Medium medium)
			throws IOException {
		FedoraAccess fa = injector.getInstance(Key.get(FedoraAccess.class, Names.named("rawFedoraAccess"))); 
		Document dc = fa.getDC(uuid);
		String titleFromDC = DCUtils.titleFromDC(dc);
		ProcessStarter.updateName("Generovani '"+titleFromDC+"' na "+medium);
		return titleFromDC;
	}

	private static void createFSStructure(File pdfsFolder, File outputFodler, Medium medium, String titleFromDC) throws IOException {
		int pocitadlo = 0;
		long bytes = 0;
		File currentFolder = createFolder(outputFodler, medium, ++pocitadlo);
		System.out.println(currentFolder.getAbsolutePath());
		File[] listFiles = pdfsFolder.listFiles();
		if (listFiles != null) {
			Arrays.sort(listFiles, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					Date modified1 = new Date(o1.lastModified());
					Date modified2 = new Date(o2.lastModified());
					return modified1.compareTo(modified2);
				}
			});
			for (File file : listFiles) {
				if ((bytes+file.length()) > medium.getSize()) {
					copyHTMLContent(currentFolder, titleFromDC, medium, ""+pocitadlo);
					currentFolder = createFolder(outputFodler, medium, ++pocitadlo);
					bytes = 0;
				}
				bytes += file.length();
				File newFile = new File(currentFolder, file.getName());
				FileUtils.copyFile(file, newFile);
				file.deleteOnExit();
//				boolean renamed = file.renameTo(newFile);
//				if (!renamed) throw new RuntimeException("cannot rename file '"+file.getAbsolutePath()+"' to '"+newFile+"'");
			}
			//copyHTMLContent(currentFolder, titleFromDC, medium, ""+pocitadlo);
		}
	}

	static void copyHTMLContent(File currentFolder, String dctitle, Medium medium, String number) {
		try {
			File[] listFiles = currentFolder.listFiles();
			if (listFiles == null) return;
			String[] fileNames = new String[listFiles.length];
			for (int i = 0; i < listFiles.length; i++) {
				fileNames[i] = listFiles[i].getName();
			}
			File htmlFolder = new File(currentFolder,"html");
			boolean dirsCreated = htmlFolder.mkdirs();
			if (!dirsCreated) throw new RuntimeException("cannot create dir '"+htmlFolder.getAbsolutePath()+"'");
			PackUtils.unpack(htmlFolder);
			String indexHTML = DecriptionHTML.descriptionHTML(dctitle, medium, fileNames, number);
			File indexHTMLFile = new File(htmlFolder, "index.html");
			FileOutputStream fos = null;
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(indexHTML.getBytes(Charset.forName("UTF-8")));
			try {
				fos = new FileOutputStream(indexHTMLFile);
				IOUtils.copyStreams(byteArrayInputStream, fos);
			} finally {
				if (fos != null) fos.close();
				if (byteArrayInputStream != null) byteArrayInputStream.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	

	private static File createFolder(File outputFodler, Medium medium, int pocitadlo) {
		File dir = new File(outputFodler, medium.name()+"_"+pocitadlo);
		if (!dir.exists()) {
			boolean mkdirs = dir.mkdirs();
			if (!mkdirs) throw new RuntimeException("cannot create dir '"+dir.getAbsolutePath()+"'");
		}
		return dir;
	}


	private static void generatePDFs(String pid, File uuidFolder, Injector injector, String djvuUrl, String i18nUrl) throws Exception {
		try {
			if (!uuidFolder.exists()) { 
				boolean mkdirs = uuidFolder.mkdirs();
				if (!mkdirs) throw new RuntimeException("cannot create dir '"+uuidFolder.getAbsolutePath()+"'");
			} else {
					File[] files = uuidFolder.listFiles(); 
					if (files != null) {
						for (File file : files) { 
							file.deleteOnExit(); 
						}
					}
			}
			FedoraAccess fa = injector.getInstance(Key.get(FedoraAccess.class, Names.named("rawFedoraAccess"))); 
			SolrAccess sa = injector.getInstance(SolrAccess.class);
			GeneratePDFService generatePDF = injector.getInstance(GeneratePDFService.class);
			LOGGER.info("fedoraAccess.getDC("+pid+")");
			Document dc = fa.getDC(pid);
			LOGGER.info("dcUtils.titleFromDC("+dc+")");
			String title = DCUtils.titleFromDC(dc);
			LOGGER.info("title is "+title);
			GenerateController controller = new GenerateController(uuidFolder, title);
			//generatePDF.fullPDFExport(pid, controller, controller, djvuUrl, i18nUrl);
			ObjectPidsPath[] path = sa.getPath(pid);
			if (path.length == 0) {
			    path = new ObjectPidsPath[]{new ObjectPidsPath(pid)};
			}
			generatePDF.fullPDFExport(path[0], controller, controller, djvuUrl, i18nUrl, null /*use default*/);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		}
	}

	private static File getTmpDir() {
		return new File(System.getProperty("java.io.tmpdir"));
	}
}