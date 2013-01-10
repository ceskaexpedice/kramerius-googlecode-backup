package cz.incad.Kramerius.views;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.google.inject.Provider;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import cz.incad.Kramerius.Initializable;
import cz.incad.kramerius.processes.BatchStates;
import cz.incad.kramerius.processes.DefinitionManager;
import cz.incad.kramerius.processes.LRPRocessFilter;
import cz.incad.kramerius.processes.LRProcess;
import cz.incad.kramerius.processes.LRProcessDefinition;
import cz.incad.kramerius.processes.LRProcessManager;
import cz.incad.kramerius.processes.LRProcessOffset;
import cz.incad.kramerius.processes.LRProcessOrdering;
import cz.incad.kramerius.processes.States;
import cz.incad.kramerius.processes.TypeOfOrdering;
import cz.incad.kramerius.processes.LRPRocessFilter.Tripple;
import cz.incad.kramerius.processes.template.OutputTemplateFactory;
import cz.incad.kramerius.service.ResourceBundleService;
import cz.incad.kramerius.utils.params.ParamsLexer;
import cz.incad.kramerius.utils.params.ParamsParser;

public class ProcessesViewObject implements Initializable {

    public static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ProcessesViewObject.class.getName());

    @Inject
    protected LRProcessManager processManager;
    
    @Inject
    protected DefinitionManager definitionManager;

    @Inject
    protected ResourceBundleService bundleService;

    @Inject
    protected Provider<Locale> localesProvider;
    
    @Inject
    protected Provider<HttpServletRequest> requestProvider;

    @Inject
    protected OutputTemplateFactory outputTemplateFactory;
    
    private LRProcessOrdering ordering;
    private LRProcessOffset offset;
    private TypeOfOrdering typeOfOrdering;

    private String lrUrl;

    private LRPRocessFilter filter;

    private String filterParam;

    private int numberOfRunningProcesses = -1;

    public ProcessesViewObject() throws RecognitionException {
        super();
    }
    
    public void init() {
        try {
            String ordering = this.requestProvider.get().getParameter("ordering");
            if ((ordering == null) || (ordering.trim().equals(""))) {
                ordering = LRProcessOrdering.PLANNED.name();
            }
            this.ordering = LRProcessOrdering.valueOf(ordering);

            String offset = this.requestProvider.get().getParameter("offset");
            if ((offset == null) || (offset.trim().equals(""))) {
                offset = "0";
            }

            String size = this.requestProvider.get().getParameter("size");
            if ((size == null) || (size.trim().equals(""))) {
                size = "5";
            }
            this.offset = new LRProcessOffset(offset, size);

            
            String type = this.requestProvider.get().getParameter("type");
            if ((type == null) || (type.trim().equals(""))) {
                type = "DESC";
            }
            this.typeOfOrdering = TypeOfOrdering.valueOf(type);
            
            this.filterParam = this.requestProvider.get().getParameter("filter");

            this.filter = this.createProcessFilter();
        } catch (RecognitionException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    public LRPRocessFilter getFilter() {
        return filter;
    }
    
    public List<ProcessViewObject> getProcesses() {
        List<LRProcess> lrProcesses = this.processManager.getLongRunningProcessesAsGrouped(this.ordering, this.typeOfOrdering, this.offset, this.filter);
        List<ProcessViewObject> objects = new ArrayList<ProcessViewObject>();
        for (LRProcess lrProcess : lrProcesses) {
            LRProcessDefinition def = this.definitionManager.getLongRunningProcessDefinition(lrProcess.getDefinitionId());
            ProcessViewObject pw = new ProcessViewObject(lrProcess, def, this.ordering, this.offset, this.typeOfOrdering, this.bundleService, this.localesProvider.get(), this.outputTemplateFactory);
            if (lrProcess.isMasterProcess()) {
                List<LRProcess> childSubprecesses = this.processManager.getLongRunningProcessesByGroupToken(lrProcess.getGroupToken());
                for (LRProcess child : childSubprecesses) {
                    if (!child.getUUID().equals(lrProcess.getUUID())) {
                        LRProcessDefinition childDef = this.definitionManager.getLongRunningProcessDefinition(child.getDefinitionId());
                        ProcessViewObject childPW = new ProcessViewObject(child, childDef, this.ordering, this.offset, this.typeOfOrdering, this.bundleService, this.localesProvider.get(), this.outputTemplateFactory);
                        pw.addChildProcess(childPW);
                    }
                }
            }
            objects.add(pw);
        }
        return objects;
    }

    private LRPRocessFilter createProcessFilter() throws RecognitionException {
        if (this.filterParam == null)
            return null;
        try {
            ParamsParser paramsParser = new ParamsParser(new ParamsLexer(new StringReader(this.filterParam)));
            List params = paramsParser.params();
            List<Tripple> tripples = new ArrayList<LRPRocessFilter.Tripple>();
            for (Object object : params) {
                List trippleList = (List) object;
                Tripple tripple = createTripple(trippleList);
                if (tripple.getVal() != null) {
                    tripples.add(tripple);
                }
            }
            LRPRocessFilter filter = LRPRocessFilter.createFilter(tripples);
            // TODO: do it better
            if (filter!= null) {

                Tripple statusTripple = filter.findTripple("status");
                if (statusTripple != null) {
                    if (((Integer) statusTripple.getVal()) == -1) {
                        filter.removeTripple(statusTripple);
                    }
                }

                Tripple bstatusTripple = filter.findTripple("batch_status");
                if (bstatusTripple != null) {
                    if (((Integer) bstatusTripple.getVal()) == -1) {
                        filter.removeTripple(bstatusTripple);
                    }
                }

            }

            return filter;
        } catch (TokenStreamException te) {
            te.printStackTrace();
            return null;
        }
    }

    private Tripple createTripple(List trpList) {
        if (trpList.size() == 3) {
            String name = (String) trpList.get(0);
            String op = (String) trpList.get(1);
            String val = (String) trpList.get(2);
            Tripple trp = new Tripple(name, val, op);
            return trp;
        } else
            return null;
    }

    public boolean getHasNext() {
        int count = getNumberOfRunningProcess();
        int oset = Integer.parseInt(this.offset.getOffset());
        int size = Integer.parseInt(this.offset.getSize());
        return (oset + size) < count;
    }

    public int getNumberOfRunningProcess() {
        if (this.numberOfRunningProcesses == -1) {
            this.numberOfRunningProcesses = this.processManager.getNumberOfLongRunningProcesses(this.filter);
        }
        return this.numberOfRunningProcesses;
    }

    public int getOffsetValue() {
        return Integer.parseInt(this.offset.getOffset());
    }

    public int getPageSize() {
        return Integer.parseInt(this.offset.getSize());
    }
    
    public int getPageNumber() {
        return getOffsetValue() / getPageSize();
    }

    public int getPrevPageValue() {
        return Math.max(0, getOffsetValue() - getPageSize());
    }

    public int getNextPageValue() {
        int count = getNumberOfRunningProcess();
        return Math.min(count - getPageSize(), getOffsetValue() + getPageSize());
    }
    
    public int getSkipPrevPageValue() {
        int r = getOffsetValue() - (getPageSize() * 10);
        return Math.max(0, r);
    }
    
    public int getSkipNextPageValue() {
        int count = getNumberOfRunningProcess();
        int r = getOffsetValue() + (getPageSize() * 10);
        return Math.min(count - getPageSize(), r);
    }
    
    
    public String getOrdering() {
        return this.ordering.toString();
    }

    public String getTypeOfOrdering() {
        return this.typeOfOrdering.getTypeOfOrdering();
    }

    public String getMoreNextAHREF() {
        try {
            String nextString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.next");
            int count = getNumberOfRunningProcess();
            int offset = Integer.parseInt(this.offset.getOffset());
            int size = Integer.parseInt(this.offset.getSize())*5;
            if ((offset + size) < count) {
                return "<a href=\"javascript:_wait();processes.modifyProcessDialogData('" + this.ordering + "','" + this.offset.getNextOffset() + "','" + size + "','" + this.typeOfOrdering.getTypeOfOrdering() + "');\"> " + nextString + " <img  border=\"0\" src=\"img/next_arr.png\"/> </a>";
            } else {
                return "<span>" + nextString + "</span> <img border=\"0\" src=\"img/next_arr.png\" alt=\"next\" />";
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return "<img border=\"0\" src=\"img/next_arr.png\" alt=\"next\" />";
        }
        
    }
    
    public List<String> getDirectPages() {
        List<String> hrefs = new ArrayList<String>();
        int count = getNumberOfRunningProcess();
        int size = Integer.parseInt(this.offset.getSize());
        int pages  = count / size;
        for (int i = 0; i < pages; i++) {
            String href = "<a href=\"javascript:_wait();processes.modifyProcessDialogData('" + this.ordering + "','" + (i*size) + "','" + this.offset.getSize() + "','" + this.typeOfOrdering.getTypeOfOrdering() + "');\"> " + i + "</a>";
            hrefs.add(href);
        }

        int rest = count % size;
        if (rest != 0) {
            String href = "<a href=\"javascript:_wait();processes.modifyProcessDialogData('" + this.ordering + "','" + pages + "','" + this.offset.getSize() + "','" + this.typeOfOrdering.getTypeOfOrdering() + "');\"> " + pages + "</a>";
            hrefs.add(href);
        }
        return hrefs;
    }
    
    public String getNextAHREF() {
        try {
            String nextString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.next");
            int count = getNumberOfRunningProcess();
            int offset = Integer.parseInt(this.offset.getOffset());
            int size = Integer.parseInt(this.offset.getSize());
            if ((offset + size) < count) {
                return "<a href=\"javascript:_wait();processes.modifyProcessDialogData('" + this.ordering + "','" + this.offset.getNextOffset() + "','" + this.offset.getSize() + "','" + this.typeOfOrdering.getTypeOfOrdering() + "');\"> " + nextString + " <img  border=\"0\" src=\"img/next_arr.png\"/> </a>";
            } else {
                return "<span>" + nextString + "</span> <img border=\"0\" src=\"img/next_arr.png\" alt=\"next\" />";
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return "<img border=\"0\" src=\"img/next_arr.png\" alt=\"next\" />";
        }
    }

    public String getPrevAHREF() {
        try {
            String prevString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.prev");
            int offset = Integer.parseInt(this.offset.getOffset());
            if (offset > 0) {
                return "<a href=\"javascript:_wait();processes.modifyProcessDialogData('" + this.ordering + "','" + this.offset.getPrevOffset() + "','" + this.offset.getSize() + "','" + this.typeOfOrdering.getTypeOfOrdering() + "');\"> <img border=\"0\" src=\"img/prev_arr.png\"/> " + prevString + " </a>";
            } else {
                return "<img border=\"0\" src=\"img/prev_arr.png\" alt=\"prev\" /> <span>" + prevString + "</span>";
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return "<img border=\"0\" src=\"img/prev_arr.png\" alt=\"prev\" /> ";
        }
    }

    
    private TypeOfOrdering switchOrdering() {
        return this.typeOfOrdering.equals(TypeOfOrdering.ASC) ? TypeOfOrdering.DESC : TypeOfOrdering.ASC;
    }

    public String getOrderingIcon() {
        return this.typeOfOrdering.equals(TypeOfOrdering.ASC)  ? "<span class='ui-icon ui-icon-triangle-1-s'></span>" : "<span class='ui-icon ui-icon-triangle-1-n'></span>";
    }
    
    public boolean isNameOrdered() {
        return this.ordering.equals(LRProcessOrdering.NAME);
    }
    
    public String getNameOrdering() {
        try {
            String nameString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.name");
            LRProcessOrdering nOrdering = LRProcessOrdering.NAME;
            boolean changeTypeOfOrdering = this.ordering.equals(nOrdering);
            return newOrderingURL(nOrdering, nameString, changeTypeOfOrdering ? switchOrdering() : TypeOfOrdering.ASC);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return e.getMessage();
        }
    }

    public boolean isStartedDateOrdered() {
        return this.ordering.equals(LRProcessOrdering.STARTED);
    }

    public String getDateOrdering() {
        try {
            String startedString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.started");
            LRProcessOrdering nOrdering = LRProcessOrdering.STARTED;
            boolean changeTypeOfOrdering = this.ordering.equals(nOrdering);
            return newOrderingURL(nOrdering, startedString, changeTypeOfOrdering ? switchOrdering() : TypeOfOrdering.ASC);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return e.getMessage();
        }
    }

    public boolean isPlannedDateOrdered() {
        return this.ordering.equals(LRProcessOrdering.PLANNED);
    }

    public String getPlannedDateOrdering() {
        try {
            String startedString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.planned");
            LRProcessOrdering nOrdering = LRProcessOrdering.PLANNED;
            boolean changeTypeOfOrdering = this.ordering.equals(nOrdering);
            return newOrderingURL(nOrdering, startedString, changeTypeOfOrdering ? switchOrdering() : TypeOfOrdering.ASC);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return e.getMessage();
        }
    }

    public boolean isFinishedDateOrdered() {
        return this.ordering.equals(LRProcessOrdering.FINISHED);
    }

    public String getFinishedDateOrdering() {
        try {
            String startedString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.finished");
            LRProcessOrdering nOrdering = LRProcessOrdering.FINISHED;
            boolean changeTypeOfOrdering = this.ordering.equals(nOrdering);
            return newOrderingURL(nOrdering, startedString, changeTypeOfOrdering ? switchOrdering() : TypeOfOrdering.ASC);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return e.getMessage();
        }
    }

    public boolean isUserOrdered() {
        return this.ordering.equals(LRProcessOrdering.LOGINNAME);
    }

    

    public String getUserOrdering() {
        try {
            String pidString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.user");
            LRProcessOrdering nOrdering = LRProcessOrdering.LOGINNAME;
            boolean changeTypeOfOrdering = this.ordering.equals(nOrdering);
            return newOrderingURL(nOrdering, pidString, changeTypeOfOrdering ? switchOrdering() : TypeOfOrdering.ASC);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return e.getMessage();
        }
    }

    
    public String getPidOrdering() {
        try {
            String pidString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.pid");
            LRProcessOrdering nOrdering = LRProcessOrdering.ID;
            boolean changeTypeOfOrdering = this.ordering.equals(nOrdering);
            return newOrderingURL(nOrdering, pidString, changeTypeOfOrdering ? switchOrdering() : TypeOfOrdering.ASC);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return e.getMessage();
        }
    }

    public boolean isStateOrdered() {
        return this.ordering.equals(LRProcessOrdering.STATE);
    }

    public String getStateOrdering() {
        try {
            String stateString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.state");
            LRProcessOrdering nOrdering = LRProcessOrdering.STATE;
            boolean changeTypeOfOrdering = this.ordering.equals(nOrdering);
            return newOrderingURL(nOrdering, stateString, changeTypeOfOrdering ? switchOrdering() : TypeOfOrdering.ASC);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return e.getMessage();
        }
    }

    public boolean isBatchStateOrdered() {
        return this.ordering.equals(LRProcessOrdering.BATCHSTATE);
    }

    public String getBatchStateOrdering() {
        try {
            String stateString = bundleService.getResourceBundle("labels", this.localesProvider.get()).getString("administrator.processes.batch");
            LRProcessOrdering nOrdering = LRProcessOrdering.BATCHSTATE;
            boolean changeTypeOfOrdering = this.ordering.equals(nOrdering);
            return newOrderingURL(nOrdering, stateString, changeTypeOfOrdering ? switchOrdering() : TypeOfOrdering.ASC);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return e.getMessage();
        }
    }
    
    private String newOrderingURL(LRProcessOrdering nOrdering, String name, TypeOfOrdering ntypeOfOrdering) {
        String href = "<a href=\"javascript:_wait();processes.modifyProcessDialogData('" + nOrdering + "','" + this.offset.getOffset() + "','" + this.offset.getSize() + "','" + ntypeOfOrdering.getTypeOfOrdering() + "');\"";
        if (this.ordering.equals(nOrdering)) {
            // href += orderingImg(nOrdering);
            if (typeOfOrdering.equals(TypeOfOrdering.DESC)) {
                href += " class=\"order_down\"";
            } else {
                href += " class=\"order_up\"";
            }
        }
        href += ">" + name + "</a>";
        return href;
    }

    public String getPlannedAfter() {
        if (this.filter != null) {
            List<Tripple> tripples = this.filter.getTripples();
            for (Tripple tripple : tripples) {
                if (tripple.getName().equals("planned") && tripple.getOp().equals(LRPRocessFilter.Op.GT)) {
                    return LRPRocessFilter.getFormattedValue(tripple);
                }
            }
        } 
        return "";
    }

    public String getPlannedBefore() {
        if (this.filter != null) {
            List<Tripple> tripples = this.filter.getTripples();
            for (Tripple tripple : tripples) {
                if (tripple.getName().equals("planned") && tripple.getOp().equals(LRPRocessFilter.Op.LT)) {
                    return LRPRocessFilter.getFormattedValue(tripple);
                }
            }
        } 
        return "";
    }


    public String getStartedAfter() {
        if (this.filter != null) {
            List<Tripple> tripples = this.filter.getTripples();
            for (Tripple tripple : tripples) {
                if (tripple.getName().equals("started") && tripple.getOp().equals(LRPRocessFilter.Op.GT)) {
                    return LRPRocessFilter.getFormattedValue(tripple);
                }
            }
        } 
        return "";
    }

    public String getStartedBefore() {
        if (this.filter != null) {
            List<Tripple> tripples = this.filter.getTripples();
            for (Tripple tripple : tripples) {
                if (tripple.getName().equals("started") && tripple.getOp().equals(LRPRocessFilter.Op.LT)) {
                    return LRPRocessFilter.getFormattedValue(tripple);
                }
            }
        } 
        return "";
    }


    public String getFinishedAfter() {
        if (this.filter != null) {
            List<Tripple> tripples = this.filter.getTripples();
            for (Tripple tripple : tripples) {
                if (tripple.getName().equals("finished") && tripple.getOp().equals(LRPRocessFilter.Op.GT)) {
                    return LRPRocessFilter.getFormattedValue(tripple);
                }
            }
        } 
        return "";
    }

    public String getFinishedBefore() {
        if (this.filter != null) {
            List<Tripple> tripples = this.filter.getTripples();
            for (Tripple tripple : tripples) {
                if (tripple.getName().equals("finished") && tripple.getOp().equals(LRPRocessFilter.Op.LT)) {
                    return LRPRocessFilter.getFormattedValue(tripple);
                }
            }
        } 
        return "";
    }

    
    public String getNameLike() {
        if (this.filter != null) {
            List<Tripple> tripples = this.filter.getTripples();
            for (Tripple tripple : tripples) {
                if (tripple.getName().equals("name") && tripple.getOp().equals(LRPRocessFilter.Op.LIKE)) {
                    return LRPRocessFilter.getFormattedValue(tripple);
                }
            }
        }    
        return "";
    }


    public List<BatchProcessStateWrapper> getBatchStatesForFilter() {
        List<BatchProcessStateWrapper> wrap = BatchProcessStateWrapper.wrap(true, BatchStates.values());
        if (this.filter != null) {
            Tripple tripple = this.filter.findTripple("batch_status");
            if (tripple != null) {
                Integer intg = (Integer)tripple.getVal();
                if (intg.intValue() >= 0) {
                    for (BatchProcessStateWrapper wrapper : wrap) {
                        if (wrapper.getVal() == intg.intValue()) {
                            wrapper.setSelected(true);
                        }
                    }
                }
            }
        }
        return wrap;
    }

    
    public List<ProcessStateWrapper> getStatesForFilter() {
        List<ProcessStateWrapper> wrap = ProcessStateWrapper.wrap(true, States.values());
        if (this.filter != null) {
            Tripple tripple = this.filter.findTripple("status");
            if (tripple != null) {
                Integer intg = (Integer)tripple.getVal();
                if (intg.intValue() >= 0) {
                    for (ProcessStateWrapper wrapper : wrap) {
                        if (wrapper.getVal() == intg.intValue()) {
                            wrapper.setSelected(true);
                        }
                    }
                }
            }
        }
        return wrap;
    }

    private String orderingImg(LRProcessOrdering nOrdering) {
        if (nOrdering.equals(this.ordering)) {
            if (typeOfOrdering.equals(TypeOfOrdering.DESC)) {
                return "<img src=\"img/order_down.png\"></img>";
            } else {
                return "<img src=\"img/order_up.png\"></img>";
            }
        } else
            return "";
    }
}