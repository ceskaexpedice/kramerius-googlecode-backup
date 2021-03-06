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
package cz.incad.kramerius.statistics;

import java.util.List;
import java.util.Map;

/**
 * Represents one report
 * @author pavels
 */
public interface StatisticReport {
    
    public static final String COUNT_KEY = "count";
    public static final String PID_KEY = "pid";
    public static final String TITLE_KEY = "title";
    public static final String MODEL_KEY = "model";
    public static final String ACTION_KEY = "action";
    
    /**
     * Returns reporting page
     * @param reportOffset Offset and size
     * @return
     */
    public List<Map<String,Object>> getReportPage(ReportedAction reportedAction, StatisticReportOffset reportOffset, Object filteredValue) throws StatisticsReportException;
    
    /**
     * Returns optional filtering values
     * @return
     */
    public List<String> getOptionalValues();
    
    /**
     * Return report identifier
     * @return
     */
    public String getReportId();
    
    
    /**
     * Process access log for concrete report
     * @param sup
     */
    public void processAccessLog(ReportedAction action, StatisticsReportSupport sup, Object filteredValue, Object...args) throws StatisticsReportException;
}
