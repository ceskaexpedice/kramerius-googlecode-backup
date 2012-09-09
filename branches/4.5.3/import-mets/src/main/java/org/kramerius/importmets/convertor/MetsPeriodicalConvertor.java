package org.kramerius.importmets.convertor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kramerius.dc.ElementType;
import org.kramerius.dc.OaiDcType;
import org.kramerius.importmets.utils.XMLTools;
import org.kramerius.importmets.valueobj.ConvertorConfig;
import org.kramerius.importmets.valueobj.DublinCore;
import org.kramerius.importmets.valueobj.Foxml;
import org.kramerius.importmets.valueobj.ImageMetaData;
import org.kramerius.importmets.valueobj.ImageRepresentation;
import org.kramerius.importmets.valueobj.RelsExt;
import org.kramerius.importmets.valueobj.ServiceException;
import org.kramerius.mets.AreaType;
import org.kramerius.mets.DivType;
import org.kramerius.mets.DivType.Fptr;
import org.kramerius.mets.FileType.FLocat;
import org.kramerius.mets.FileType;
import org.kramerius.mets.MdSecType;
import org.kramerius.mets.Mets;
import org.kramerius.mets.MetsType.FileSec;
import org.kramerius.mets.MetsType.FileSec.FileGrp;
import org.kramerius.mets.MetsType.StructLink;
import org.kramerius.mets.StructLinkType.SmLink;
import org.kramerius.mets.StructMapType;
import org.kramerius.mods.DateBaseDefinition;
import org.kramerius.mods.DetailDefinition;
import org.kramerius.mods.IdentifierDefinition;
import org.kramerius.mods.ModsDefinition;
import org.kramerius.mods.ObjectFactory;
import org.kramerius.mods.PartDefinition;
import org.kramerius.mods.TitleInfoDefinition;
import org.kramerius.mods.XsString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

import cz.incad.kramerius.utils.conf.KConfiguration;

/**
 * Konvertor periodika do sady foxml digitalnich objektu
 *
 * @author xholcik
 */
public class MetsPeriodicalConvertor extends BaseConvertor {


    private static final Logger log = Logger.getLogger(MetsPeriodicalConvertor.class);

    public MetsPeriodicalConvertor(ConvertorConfig config, Unmarshaller unmarshaller) throws ServiceException {
        super(config, unmarshaller);
    }





    public void convert(Mets mets, StringBuffer convertedURI)
            throws ServiceException {
        try {
            policyID = config.isDefaultVisibility() ? POLICY_PUBLIC: POLICY_PRIVATE;
            loadModsAndDcMap(mets);
            loadFileMap(mets);
            processStructMap(mets);
            for (Foxml page : objects.values()) {
                exportFoxml(page);
            }
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Načte všechny MODS a DC záznamy a uloží do map podle jejich ID
     *
     * @param mets
     */
    private void loadModsAndDcMap(Mets mets) throws JAXBException {
        int modsCounter = 0;
        int dcCounter = 0;
        for (MdSecType md : mets.getDmdSec()) {
            String id = md.getID();
            Element me = ((Element) md.getMdWrap().getXmlData().getAny().get(0));
            String type = md.getMdWrap().getMDTYPE();
            if ("MODS".equalsIgnoreCase(type)) {
                ModsDefinition mods = (ModsDefinition) ((JAXBElement<?>) unmarshallerMODS
                        .unmarshal(me)).getValue();
                if (modsMap.put(id, mods) != null) {
                    log.warn("Duplicate MODS record: " + id);
                } else {
                    modsCounter++;
                }
            } else if ("DC".equalsIgnoreCase(type)) {
                OaiDcType dc = (OaiDcType) ((JAXBElement<?>) unmarshallerDC
                        .unmarshal(me)).getValue();
                if (dcMap.put(id, dc) != null) {
                    log.warn("Duplicate DC record: " + id);
                } else {
                    dcCounter++;
                }
            } else {
                log.warn("Unsupported metadata type: " + type + " for " + id);
            }
        }
        log.info("Loaded " + modsCounter + " MODS records and " + dcCounter
                + " DC records.");
        if (dcCounter != modsCounter) {
            log.warn("Different MODS (" + modsCounter + ") and DC ("
                    + dcCounter + ") records count.");
        }
    }

    private void loadFileMap(Mets mets) throws JAXBException {
        int filecounter = 0;
        FileSec fsec = mets.getFileSec();
        for (FileGrp fGrp : fsec.getFileGrp()) {
            for (FileType file : fGrp.getFile()) {
                String id = file.getID();
                FLocat fl = firstItem(file.getFLocat());
                String name = fl.getHref();
                /*if (KConfiguration.getInstance().getConfiguration().getBoolean("convert.userCopy", true)){
                    name = name.replace("masterCopy/MC", "userCopy/UC");
                }*/
                fileMap.put(id, name);
                filecounter++;
            }
        }
        log.info("Loaded files: "+filecounter);
    }

    private void processStructMap(Mets mets) throws ServiceException {
        for (StructMapType sm : mets.getStructMap()) {
            if ("PHYSICAL".equals(sm.getTYPE())) {
                processPages(sm);
            } else if ("LOGICAL".equals(sm.getTYPE())) {
                processDiv(null, sm.getDiv());
            } else {
                log.warn("Unsupported StructMap type: " + sm.getTYPE()
                        + " for " + sm.getID());
            }
        }
        processStructLink(mets.getStructLink());

    }


    private void processPages(StructMapType sm) {
        DivType issueDiv = sm.getDiv();
        for (DivType pageDiv : issueDiv.getDiv()) {
            String type = pageDiv.getTYPE();
            BigInteger order = pageDiv.getORDER();
            String pageTitle = pageDiv.getORDERLABEL();

            Foxml page = new Foxml();
            page.setPid(pid(generateUUID()));
            page.setTitle(pageTitle);
            // create MODS for page
            ModsDefinition pageMods = modsObjectFactory.createModsDefinition();
            PartDefinition pagePart = modsObjectFactory.createPartDefinition();
            pagePart.setType(type);
            // add part for page Number
            DetailDefinition titleDetail = modsObjectFactory.createDetailDefinition();
            titleDetail.setType("pageNumber");
            XsString titleString = modsObjectFactory.createXsString();
            titleString.setValue(pageTitle);
            JAXBElement<XsString> titleElement = modsObjectFactory.createNumber(titleString);
            titleDetail.getNumberOrCaptionOrTitle().add(titleElement);
            pagePart.getDetailOrExtentOrDate().add(titleDetail);
            // add part for page Index
            DetailDefinition orderDetail = modsObjectFactory.createDetailDefinition();
            orderDetail.setType("pageIndex");
            XsString orderString = modsObjectFactory.createXsString();
            orderString.setValue(order.toString());
            JAXBElement<XsString> orderElement = modsObjectFactory.createNumber(orderString);
            orderDetail.getNumberOrCaptionOrTitle().add(orderElement);
            pagePart.getDetailOrExtentOrDate().add(orderDetail);
            // add mods to page foxml
            pageMods.getModsGroup().add(pagePart);
            page.setMods(pageMods);
            // create DC for page
            OaiDcType pageDc = createDC(page.getPid(),page.getTitle());
            setDCModelAndPolicy(pageDc, MODEL_PAGE, policyID);
            page.setDc(pageDc);

            page.setRe(new RelsExt(page.getPid(), MODEL_PAGE));
            page.getRe().addRelation(RelsExt.POLICY, policyID, true);

            for (Fptr fptr : pageDiv.getFptr()) {
                    FileType fileId = (FileType) fptr.getFILEID();
                    String fileName = fileMap.get(fileId.getID());
                    if (KConfiguration.getInstance().getConfiguration().getBoolean("convert.userCopy", true)){
                        if (fileId.getID().startsWith(MC_PREFIX)){
                            continue;
                        }
                    }else{
                        if (fileId.getID().startsWith(UC_PREFIX)){
                            continue;
                        }
                    }
                    page.addFiles(new ImageRepresentation(fileName, getFileType(fileId.getID())));
            }

            String pageId = pageDiv.getID();
            objects.put(pageId, page);
        }
    }

    private OaiDcType createDC(String pid, String title){
        OaiDcType dc = dcObjectFactory.createOaiDcType();
        dc.getTitleOrCreatorOrSubject().add(dcObjectFactory.createIdentifier(createDcElementType(pid)));
        dc.getTitleOrCreatorOrSubject().add(dcObjectFactory.createTitle(createDcElementType(title)));
        return dc;
    }

    private void setDCModelAndPolicy(OaiDcType dc, String model, String policy){
        dc.getTitleOrCreatorOrSubject().add(dcObjectFactory.createType(createDcElementType(model)));
        dc.getTitleOrCreatorOrSubject().add(dcObjectFactory.createRights(createDcElementType(policy)));
    }


    private Foxml processDiv(Foxml parent, DivType div) {
        String divType = div.getTYPE();
        if ("PICTURE".equalsIgnoreCase(divType)) return null;//divs for PICTURE are not supported in K4
        if ("MONOGRAPH".equalsIgnoreCase(divType)){//special hack to ignore extra div for monograph
            List<DivType> volumeDivs = div.getDiv();
            if (volumeDivs == null) return null;
            if (volumeDivs.size()==1){//process volume as top level
                processDiv(null, volumeDivs.get(0));
                return null;
            }
            if (volumeDivs.size()>1){//if monograph div contains more subdivs, first is supposed to be the volume, the rest are supplements that will be nested in the volume.
                Foxml volume = processDiv(null, volumeDivs.get(0));
                for (int i =1;i<volumeDivs.size();i++){
                    processDiv(volume,volumeDivs.get(i));
                }
            }
            return null;
        }

        MdSecType modsIdObj = (MdSecType) firstItem(div.getDMDID());
        if (modsIdObj == null) return null;//we consider only div with associated metadata (DMDID)


        String model = mapModel(divType);


        String modsId = modsIdObj.getID();
        String dcId = modsId.replaceFirst("MODS", "DC");

        ModsDefinition mods = modsMap.get(modsId);
        if (mods == null) {
            throw new ServiceException("Cannot find mods: " + modsId);
        }


        String uuid = getUUIDfromMods(mods);
        if (uuid == null) {
            uuid = generateUUID();
        }
        String pid = pid(uuid);
        String title = getTitlefromMods(mods);
        RelsExt re = new RelsExt(pid, model);

        re.addRelation(RelsExt.POLICY, policyID, true);

        OaiDcType dc = dcMap.get(dcId);
        if (dc == null){
            log.warn("DublinCore part missing for MODS "+modsId);
            dc = createDC(pid, title);
        }
        setDCModelAndPolicy(dc, model, policyID);

        Foxml foxml = new Foxml();
        foxml.setPid(pid);
        foxml.setTitle(title);
        foxml.setDc(dc);
        foxml.setMods(mods);
        foxml.setRe(re);
        if (parent!= null){
            String parentRelation = mapParentRelation(model);
            parent.getRe().addRelation(parentRelation, pid, false);
        }
        String divID = div.getID();
        objects.put(divID,foxml);

        for (DivType partDiv : div.getDiv()) {
            processDiv(foxml, partDiv);
        }
        return foxml;
    }

    private String mapModel(String divType){
        if ("PERIODICAL_TITLE".equalsIgnoreCase(divType)){
            return MODEL_PERIODICAL;
        }else if ("PERIODICAL_VOLUME".equalsIgnoreCase(divType)){
            return MODEL_PERIODICAL_VOLUME;
        }else if ("ISSUE".equalsIgnoreCase(divType)){
            return MODEL_PERIODICAL_ITEM;
        }else if ("ARTICLE".equalsIgnoreCase(divType)){
            return MODEL_ARTICLE;
        }else if ("SUPPLEMENT".equalsIgnoreCase(divType)){
            return MODEL_SUPPLEMENT;
        }else if ("PICTURE".equalsIgnoreCase(divType)){
            return MODEL_PICTURE;
        }else if ("VOLUME".equalsIgnoreCase(divType)){
            return MODEL_MONOGRAPH;
        }else if ("CHAPTER".equalsIgnoreCase(divType)){
            return MODEL_INTERNAL_PART;
        }
        throw new ServiceException("Unsupported div type in logical structure: "+divType);
    }

    private String mapParentRelation(String model){
        if (MODEL_PERIODICAL_VOLUME.equalsIgnoreCase(model)){
            return RelsExt.HAS_VOLUME;
        }else if (MODEL_PERIODICAL_ITEM.equalsIgnoreCase(model)){
            return RelsExt.HAS_ITEM;
        }else if (MODEL_ARTICLE.equalsIgnoreCase(model)){
            return RelsExt.HAS_INT_COMP_PART;
        }else if (MODEL_SUPPLEMENT.equalsIgnoreCase(model)){
            return RelsExt.HAS_INT_COMP_PART;
        }else if (MODEL_PICTURE.equalsIgnoreCase(model)){
            return RelsExt.HAS_INT_COMP_PART;
        }else if (MODEL_INTERNAL_PART.equalsIgnoreCase(model)){
            return RelsExt.HAS_INT_COMP_PART;
        }
        throw new ServiceException("Unsupported model mapping in logical structure: "+model);
    }


    protected void processStructLink(StructLink structLink){
        for ( Object o: structLink.getSmLinkOrSmLinkGrp()){
            if (o instanceof SmLink){
                SmLink smLink = (SmLink)o;
                String from = smLink.getFrom();
                String to = smLink.getTo();
                if (from == null || to == null) continue;
                Foxml target = objects.get(to);
                if (target == null){
                    log.warn("Invalid structLink from: "+from+" to: "+to);
                    continue;
                }
                Foxml part = objects.get(from);
                if (part == null){
                    log.warn("Invalid structLink from: "+from+" to: "+to);
                    continue;
                }
                if (from.startsWith("ISSUE")||from.startsWith("VOLUME")||from.startsWith("SUPPLEMENT")){
                    part.getRe().addRelation(RelsExt.HAS_PAGE, target.getPid(), false);
                }else{
                    part.getRe().addRelation(RelsExt.IS_ON_PAGE, target.getPid(), false);
                }
            }
        }
    }



    /*
    private String getContract(List<PeriodicalPage> pages) {
        if (pages != null) {
            for (PeriodicalPage page : pages) {
                List<PageRepresentation> reps = page.getPageRepresentation();
                if (reps != null) {
                    for (PageRepresentation rep : reps) {
                        if (rep.getPageImage() != null) {
                            String filename = removeSigla(rep.getPageImage()
                                    .getHref());
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
    }*/

}