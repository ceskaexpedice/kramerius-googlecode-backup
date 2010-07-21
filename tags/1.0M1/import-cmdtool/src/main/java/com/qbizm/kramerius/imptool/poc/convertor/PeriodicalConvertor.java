package com.qbizm.kramerius.imptool.poc.convertor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.qbizm.kramerius.imp.jaxb.DigitalObject;
import com.qbizm.kramerius.imp.jaxb.periodical.Contributor;
import com.qbizm.kramerius.imp.jaxb.periodical.ContributorName;
import com.qbizm.kramerius.imp.jaxb.periodical.CoreBibliographicDescriptionPeriodical;
import com.qbizm.kramerius.imp.jaxb.periodical.Creator;
import com.qbizm.kramerius.imp.jaxb.periodical.CreatorName;
import com.qbizm.kramerius.imp.jaxb.periodical.ItemRepresentation;
import com.qbizm.kramerius.imp.jaxb.periodical.Language;
import com.qbizm.kramerius.imp.jaxb.periodical.MainTitle;
import com.qbizm.kramerius.imp.jaxb.periodical.PageIndex;
import com.qbizm.kramerius.imp.jaxb.periodical.PageRepresentation;
import com.qbizm.kramerius.imp.jaxb.periodical.Periodical;
import com.qbizm.kramerius.imp.jaxb.periodical.PeriodicalInternalComponentPart;
import com.qbizm.kramerius.imp.jaxb.periodical.PeriodicalItem;
import com.qbizm.kramerius.imp.jaxb.periodical.PeriodicalPage;
import com.qbizm.kramerius.imp.jaxb.periodical.PeriodicalVolume;
import com.qbizm.kramerius.imp.jaxb.periodical.Publisher;
import com.qbizm.kramerius.imp.jaxb.periodical.Subject;
import com.qbizm.kramerius.imp.jaxb.periodical.TechnicalDescription;
import com.qbizm.kramerius.imp.jaxb.periodical.UniqueIdentifier;
import com.qbizm.kramerius.imp.jaxb.periodical.UniqueIdentifierURNType;
import com.qbizm.kramerius.imptool.poc.valueobj.ConvertorConfig;
import com.qbizm.kramerius.imptool.poc.valueobj.DublinCore;
import com.qbizm.kramerius.imptool.poc.valueobj.ImageMetaData;
import com.qbizm.kramerius.imptool.poc.valueobj.ImageRepresentation;
import com.qbizm.kramerius.imptool.poc.valueobj.RelsExt;
import com.qbizm.kramerius.imptool.poc.valueobj.ServiceException;

/**
 * Konvertor periodika do sady foxml digitalnich objektu
 * 
 * @author xholcik
 */
public class PeriodicalConvertor extends BaseConvertor {

    /**
     * XSL transformacni sablony
     */
    private static final String XSL_MODS_PERIODICAL = "model_periodical_MODS.xsl";

    private static final String XSL_MODS_PERIODICAL_PART = "model_periodicalInternalComponentPart-MODS.xsl";

    private static final String XSL_MODS_PERIODICAL_ITEM = "model_periodicalItem_MODS.xsl";

    private static final String XSL_MODS_PERIODICAL_PAGE = "model_periodicalPage-MODS.xsl";

    private static final String XSL_MODS_PERIODICAL_VOLUME = "model_periodicalVolume_MODS.xsl";

    public PeriodicalConvertor(ConvertorConfig config) throws ServiceException {
        super(config);
    }

    /**
     * Pomocna metoda pro ziskani pid objektu
     * 
     * @param uid
     * @return
     */
    private String uuid(UniqueIdentifier uid) throws ServiceException {
        String pid;
        if (uid == null || uid.getUniqueIdentifierURNType() == null || !Pattern.matches(PID_PATTERN, PID_PREFIX + first(uid.getUniqueIdentifierURNType().getContent()))) {
            pid = generateUUID();
            if (uid.getUniqueIdentifierURNType() == null) {
                uid.setUniqueIdentifierURNType(new UniqueIdentifierURNType());
            }
            uid.getUniqueIdentifierURNType().getContent().add(pid);
        } else {
            pid = first(uid.getUniqueIdentifierURNType().getContent());
        }

        return pid;
    }

    /**
     * Obskurni metoda pro ziskani nazvu z obskurniho objektu
     * 
     * @param biblio
     * @return
     */
    private String getMainTitle(CoreBibliographicDescriptionPeriodical biblio) {
        if (biblio != null && biblio.getTitle() != null) {
            for (Object o : biblio.getTitle().getMainTitleAndSubTitleAndParallelTitle()) {
                if (o instanceof MainTitle) {
                    return first(((MainTitle) o).getContent());
                }
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * Prevede periodikum a vsechny navazane objekty do sady foxml souboru
     * 
     * @param peri
     * @throws ServiceException
     */
    public void  convert(Periodical peri, StringBuffer convertedURI) throws ServiceException {
        CoreBibliographicDescriptionPeriodical biblio = peri.getCoreBibliographicDescriptionPeriodical();
        if (biblio == null) {
            biblio = new CoreBibliographicDescriptionPeriodical();
        }
        String title = getMainTitle(biblio);
        if (peri.getUniqueIdentifier() == null) {
            peri.setUniqueIdentifier(new UniqueIdentifier());
        }
        String uuid = uuid(peri.getUniqueIdentifier());
        String pid = pid (uuid);

        RelsExt re = new RelsExt(pid, MODEL_PERIODICAL);
        boolean visibility = isPublic(uuid, config.isDefaultVisibility(), "p_periodical");

        String volumeuuid= null;
        for (PeriodicalVolume volume : peri.getPeriodicalVolume()) {
            volumeuuid = this.convertVolume(volume, visibility);
            re.addRelation(RelsExt.HAS_VOLUME, pid(uuid(volume.getUniqueIdentifier())),false);
        }
        String cleanTitle= StringUtils.replaceEach(title, new String[]{"\t", "\n"}, new String[]{" ", " "});
        if (volumeuuid== null){
            convertedURI.append(cleanTitle).append("\t").append("pid=").append(uuid).append("&pid_path=").append(uuid).append("&path=periodical\n");
        } else{
            convertedURI.append(cleanTitle).append("\t").append("pid=").append(volumeuuid).append("&pid_path=").append(uuid).append("/").append(volumeuuid)
            .append("&path=periodical/periodicalvolume\n");
        }

        addDonatorRelation(re, biblio.getCreator());
        
        DublinCore dc = createPeriodicalDublinCore(pid, title, biblio);
        
        convertHandle(uuid, dc, re);
        
        String ISSN = peri.getISSN()==null?null:first(peri.getISSN().getContent());
        dc.addQualifiedIdentifier(RelsExt.ISSN, ISSN);
        
        for (Subject subj : biblio.getSubject()) {
            if (subj.getDDC() != null) {
                for (String ddc : subj.getDDC().getContent()) {
                    dc.addSubject("ddc:" + ddc);
                }
            }
            if (subj.getUDC() != null) {
                for (String udc : subj.getUDC().getContent()) {
                    dc.addSubject("udc:" + udc);
                }
            }
        }
        
        dc.setDescription(biblio.getAnnotation() == null? null:concat(biblio.getAnnotation().getContent()));
        Publisher publ = firstItem(biblio.getPublisher());
        if (publ!= null){
            dc.setDate(publ.getDateOfPublication()==null?null:first(publ.getDateOfPublication().getContent()));
        }
        
        dc.setType(MODEL_PERIODICAL);
        
        Language lang = firstItem(biblio.getLanguage());
        if (lang != null){
            dc.setLanguage(first(lang.getContent()));
        }

        DigitalObject foxmlPeri = this.createDigitalObject(peri, pid, title, dc, re, XSL_MODS_PERIODICAL, null, visibility);

        this.marshalDigitalObject(foxmlPeri);
        
    }

    private void addDonatorRelation(RelsExt re, List<Creator> creators) {
        if (creators != null) {
            for (Creator creator : creators) {
                if (DONATOR_ID.equals(creator.getCreatorSurname()== null?"":first(creator.getCreatorSurname().getContent()))) {
                    re.addRelation(RelsExt.HAS_DONATOR, DONATOR_PID,false);
                }
            }
        }
    }

    /**
     * Prevede PeriodicalVolume do foxml
     * 
     * @param volume
     * @param prefix
     * @throws ServiceException
     */
    private String convertVolume(PeriodicalVolume volume, boolean parentVisibility) throws ServiceException {
        CoreBibliographicDescriptionPeriodical biblio = volume.getCoreBibliographicDescriptionPeriodical();
        if (biblio == null) {
            biblio = new CoreBibliographicDescriptionPeriodical();
        }
        String title = "";
        if (volume.getPeriodicalVolumeIdentification() != null && volume.getPeriodicalVolumeIdentification().getPeriodicalVolumeNumber() != null) {
            title = first(volume.getPeriodicalVolumeIdentification().getPeriodicalVolumeNumber().getContent());
        }
        if (volume.getUniqueIdentifier() == null) {
            volume.setUniqueIdentifier(new UniqueIdentifier());
        }
        String uuid = uuid(volume.getUniqueIdentifier());
        String pid = pid (uuid);

        RelsExt re = new RelsExt(pid, MODEL_PERIODICAL_VOLUME);
        
        boolean visibility = isPublic(uuid, parentVisibility, "p_periodicalvolume");

        for (PeriodicalItem item : volume.getPeriodicalItem()) {
            this.convertItem(item, visibility);
            re.addRelation(RelsExt.HAS_ITEM, pid(uuid(item.getUniqueIdentifier())),false);
        }

        Map<String, String> pageIdMap = new TreeMap<String, String>();
        for (PeriodicalPage page : volume.getPeriodicalPage()) {
            this.convertPage(page, visibility);

            String ppid = pid(uuid(page.getUniqueIdentifier()));
            re.addRelation(RelsExt.HAS_PAGE, ppid,false);
            if (page.getIndex() != null) {
                pageIdMap.put(page.getIndex(), ppid);
            } else {
                log.warn(WARN_PAGE_INDEX);
            }
        }

        for (PeriodicalInternalComponentPart part : volume.getPeriodicalInternalComponentPart()) {
            this.convertInternalPart(part, pageIdMap, visibility);
            re.addRelation(RelsExt.HAS_INT_COMP_PART, pid(uuid(part.getUniqueIdentifier())),false);
        }
        
        DublinCore dc = createPeriodicalDublinCore(pid, title, biblio);
        String contract = getContract(volume.getPeriodicalPage());
        if (contract == null){
            PeriodicalItem item = firstItem(volume.getPeriodicalItem());
            if (item !=  null){
                contract = getContract(item.getPeriodicalPage());
            }
        }
        dc.addQualifiedIdentifier(RelsExt.CONTRACT, contract);
        re.addRelation(RelsExt.CONTRACT, contract, true);
        
        convertHandle(uuid, dc, re);
        
        dc.setDescription(biblio.getAnnotation() == null? null:concat(biblio.getAnnotation().getContent()));
        
        Publisher publ = firstItem(biblio.getPublisher());
        if (publ!= null){
            dc.setDate(publ.getDateOfPublication()==null?null:first(publ.getDateOfPublication().getContent()));
        }

        dc.setType(MODEL_PERIODICAL_VOLUME);
        
        DigitalObject foxmlVolume = this.createDigitalObject(volume, pid, title, dc, re, XSL_MODS_PERIODICAL_VOLUME, null, visibility);

        this.marshalDigitalObject(foxmlVolume);
        return uuid;
    }

    /**
     * Prevede PeriodicalInternalComponentPart do foxml
     * 
     * @param part
     * @param prefix
     * @throws ServiceException
     */
    private void convertInternalPart(PeriodicalInternalComponentPart part, Map<String, String> pageIdMap, boolean visibility) throws ServiceException {
        CoreBibliographicDescriptionPeriodical biblio = part.getCoreBibliographicDescriptionPeriodical();
        if (biblio == null) {
            biblio = new CoreBibliographicDescriptionPeriodical();
        }
        String title = getMainTitle(biblio);
        if (part.getUniqueIdentifier() == null) {
            part.setUniqueIdentifier(new UniqueIdentifier());
        }
        String uuid = uuid(part.getUniqueIdentifier());
        String pid = pid(uuid);

        RelsExt re = new RelsExt(pid, MODEL_INTERNAL_PART);

        List<PageIndex> pageIndex = part.getPages().getPageIndex();
        if (pageIndex != null && !part.getPages().getPageIndex().isEmpty()) {
            for (PageIndex pi : pageIndex) {
                Integer piFrom = Integer.valueOf(pi.getFrom());
                Integer piTo = Integer.valueOf(pi.getTo());
                this.processPageIndex(re, piFrom, piTo, pageIdMap);
            }
        }
        
        DublinCore dc = createPeriodicalDublinCore(pid, title, biblio);
        
        convertHandle(uuid, dc, re);
        dc.setType(MODEL_INTERNAL_PART);
        
        DigitalObject foxmlPart = this.createDigitalObject(part, pid, title, dc, re, XSL_MODS_PERIODICAL_PART, null, visibility);

        this.marshalDigitalObject(foxmlPart);
    }

    /**
     * Prevede PeriodicalItem do foxml
     * 
     * @param item
     * @param prefix
     * @throws ServiceException
     */
    private void convertItem(PeriodicalItem item, boolean parentVisibility) throws ServiceException {
        CoreBibliographicDescriptionPeriodical biblio = item.getCoreBibliographicDescriptionPeriodical();
        if (biblio == null) {
            biblio = new CoreBibliographicDescriptionPeriodical();
        }
        String title = getMainTitle(biblio);
        if (item.getUniqueIdentifier() == null) {
            item.setUniqueIdentifier(new UniqueIdentifier());
        }
        String uuid = uuid(item.getUniqueIdentifier());
        String pid = pid (uuid);
        
        List<ImageRepresentation> files = new ArrayList<ImageRepresentation>(2);
        if (item.getItemRepresentation() != null) {
            ItemRepresentation r = item.getItemRepresentation();
            if (item.getItemRepresentation().getItemImage() != null) {
                files.add(this.createImageRepresentation(r.getItemImage().getHref(), r.getTechnicalDescription(), item.getItemRepresentation().getUniqueIdentifier()));
            }
            if (item.getItemRepresentation().getItemText() != null) {
                files.add(this.createImageRepresentation(r.getItemText().getHref(), null, item.getItemRepresentation().getUniqueIdentifier()));
            }
        }

        RelsExt re = new RelsExt(pid, MODEL_PERIODICAL_ITEM);
        boolean visibility = isPublic(uuid, parentVisibility, "p_periodicalitem");

        Map<String, String> pageIdMap = new TreeMap<String, String>();
        for (PeriodicalPage page : item.getPeriodicalPage()) {
            this.convertPage(page,visibility);

            String ppid = pid(uuid(page.getUniqueIdentifier()));
            re.addRelation(RelsExt.HAS_PAGE, ppid,false);
            if (page.getIndex() != null) {
                pageIdMap.put(page.getIndex(), ppid);
            } else {
                log.warn(WARN_PAGE_INDEX);
            }
        }

        for (PeriodicalInternalComponentPart part : item.getPeriodicalInternalComponentPart()) {
            this.convertInternalPart(part, pageIdMap, visibility);
            re.addRelation(RelsExt.HAS_INT_COMP_PART, pid(uuid(part.getUniqueIdentifier())),false);
        }

        DublinCore dc = createPeriodicalDublinCore(pid, title, biblio);
        String contract = getContract(item.getPeriodicalPage());
        dc.addQualifiedIdentifier(RelsExt.CONTRACT, contract);
        re.addRelation(RelsExt.CONTRACT, contract, true);
        
        convertHandle(uuid, dc, re);
        
        dc.setType(MODEL_PERIODICAL_ITEM);
        
        dc.setDescription(biblio.getAnnotation() == null? null:concat(biblio.getAnnotation().getContent()));
        
        Publisher publ = firstItem(biblio.getPublisher());
        if (publ!= null){
            dc.setDate(publ.getDateOfPublication()==null?null:first(publ.getDateOfPublication().getContent()));
        }
        
        DigitalObject foxmlItem = this.createDigitalObject(item, pid, title, dc, re, XSL_MODS_PERIODICAL_ITEM, files.toArray(new ImageRepresentation[files.size()]), visibility);

        this.marshalDigitalObject(foxmlItem);
    }

    /**
     * Prevede stranku periodika do foxml
     * 
     * @param page
     * @throws ServiceException
     */
    private void convertPage(PeriodicalPage page, boolean visibility) throws ServiceException {
        String title = first(page.getPageNumber().get(0).getContent());
        // String title = page.getIndex();
        if (page.getUniqueIdentifier() == null) {
            page.setUniqueIdentifier(new UniqueIdentifier());
        }
        String uuid = uuid(page.getUniqueIdentifier());
        String pid = pid (uuid);
        
        RelsExt re = new RelsExt(pid, MODEL_PAGE);

        List<ImageRepresentation> files = new ArrayList<ImageRepresentation>(2);
        for (PageRepresentation r : page.getPageRepresentation()) {
            if (r.getPageImage() != null) {
                files.add(this.createImageRepresentation(r.getPageImage().getHref(), r.getTechnicalDescription(), r.getUniqueIdentifier()));
                re.addRelation(RelsExt.FILE, r.getPageImage().getHref(), true);
            }
            if (r.getPageText() != null) {
                files.add(this.createImageRepresentation(r.getPageText().getHref(), null, r.getUniqueIdentifier()));
            }
        }

        
        
        DublinCore dc = this.createPeriodicalDublinCore(pid, title, null);
        
        convertHandle(uuid, dc, re);
        
        dc.setType(MODEL_PAGE);

        DigitalObject foxmlPage = this.createDigitalObject(page, pid, title, dc, re, XSL_MODS_PERIODICAL_PAGE, files.toArray(new ImageRepresentation[files.size()]), visibility);

        this.marshalDigitalObject(foxmlPage);
    }

    /**
     * Vytvori a naplni DublinCore data pro DC datastream
     * 
     * @param biblio
     * @return
     */
    private DublinCore createPeriodicalDublinCore(String pid, String title, CoreBibliographicDescriptionPeriodical biblio) {
        if (biblio == null) {
            biblio = new CoreBibliographicDescriptionPeriodical();
        }
        DublinCore dc = new DublinCore();

        dc.setTitle(title);
        dc.addIdentifier(pid);

        dc.setCreator(new ArrayList<String>());
        if (biblio.getCreator() != null) {
            for (Creator c : biblio.getCreator()) {
                if (DONATOR_ID.equals(c.getCreatorSurname()== null?"":first(c.getCreatorSurname().getContent())) ) {
                    continue;
                }
                StringBuffer s = new StringBuffer();
                s.append(c.getCreatorSurname());
                for (CreatorName name : c.getCreatorName()) {
                    s.append(" " + first(name.getContent()));
                }
                dc.getCreator().add(s.toString());
            }
        }

        dc.setPublisher(new ArrayList<String>());
        if (biblio.getPublisher() != null) {
            for (Publisher p : biblio.getPublisher() ) {
                dc.getPublisher().add(first(p.getPublisherName().getContent()));
            }
        }

        dc.setContributor(new ArrayList<String>());
        if (biblio.getContributor()  != null) {
            for (Contributor c : biblio.getContributor()) {
                StringBuffer s = new StringBuffer();
                s.append(first(c.getContributorSurname().getContent()));
                for (ContributorName name : c.getContributorName()) {
                    s.append(" " + first(name.getContent()));
                }
                dc.getContributor().add(s.toString());
            }
        }
        return dc;
    }

    

    private ImageRepresentation createImageRepresentation(String filename, TechnicalDescription td, UniqueIdentifier ui) {
        ImageRepresentation ir = new ImageRepresentation();
        ir.setFilename(filename);

        ImageMetaData ad = new ImageMetaData();
        ir.setImageMetaData(ad);

        if (td != null) {
            ad.setScanningDevice(first(td.getScanningDevice().getContent()));
            ad.setScanningParameters(first(td.getScanningParameters().getContent()));
            ad.setOtherImagingInformation(first(td.getOtherImagingInformation().getContent()));
        }

        if (ui != null) {
            if (ui.getUniqueIdentifierURNType() != null) {
                ad.setUrn(first(ui.getUniqueIdentifierURNType().getContent()));
            }
            if (ui.getUniqueIdentifierSICIType() != null) {
                ad.setSici(first(ui.getUniqueIdentifierSICIType().getContent()));
            }
        }

        return ir;
    }
    
    private String getContract(List<PeriodicalPage> pages) {
        if (pages != null) {
            for (PeriodicalPage page : pages) {
                List<PageRepresentation> reps = page.getPageRepresentation();
                if (reps != null) {
                    for (PageRepresentation rep : reps) {
                        if (rep.getPageImage() != null) {
                            String filename = removeSigla(rep.getPageImage().getHref());
                            if (filename != null) {
                                int length = config.getContractLength();
                                if (length > filename.length()) {
                                    return filename;
                                } else {
                                    return filename.substring(0, length);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
