package cz.incad.kramerius.pdf;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import cz.incad.kramerius.ProcessSubtreeException;
import cz.incad.kramerius.pdf.impl.OutputStreams;
import cz.incad.kramerius.pdf.pdfpages.AbstractRenderedDocument;


/**
 * Service for generating PDF
 * @author pavels
 */
public interface GeneratePDFService {
	
	/**
	 * Generate static export
	 * @param parentUUID  Starting point of static export
	 * @param outputs  OutputStreams
	 * @param brk Break
	 * @param djvuUrl URL for full images
	 * @param i18Url URL for translations
	 * @throws IOException
	 * @throws ProcessSubtreeException 
	 */
	public void fullPDFExport(String parentUUID, OutputStreams outputs, Break brk, String djvuUrl, String i18Url) throws IOException, ProcessSubtreeException;

	/**
	 * Generate dynamic export
	 * @param path Path from Master UUID to selected UUID,
	 * @param uuidFrom Starting page of export
	 * @param uuidTo Ending page of export
	 * @param titlePage Where is title page
	 * @param os OutputStreams
	 * @throws IOException 
	 * @throws ProcessSubtreeException 
	 */
	public void dynamicPDFExport(List<String> path,String uuidFrom, String uuidTo, String titlePage, OutputStream os, String djvuUrl, String i18nUrl) throws IOException, ProcessSubtreeException;
	
	/**
	 * Generate custom pdf 
	 * @param doc Rendered document 
	 * @param parentUUID starting point of export
	 * @param os OutputStreams
	 * @throws IOException
	 */
	public void generateCustomPDF(AbstractRenderedDocument doc, String parentUUID, OutputStream os, String djvuUrl, String i18nUrl) throws IOException;

	public AbstractRenderedDocument generateCustomPDF(AbstractRenderedDocument doc, String parentUUID, OutputStream os, Break brk, String djvuUrl, String i18nUrl) throws IOException;

	/**
	 * Folder for templates
	 * @return
	 */
	public File templatesFolder();

	/**
	 * Folder for fonts
	 * @return
	 */
	public File fontsFolder();
}

