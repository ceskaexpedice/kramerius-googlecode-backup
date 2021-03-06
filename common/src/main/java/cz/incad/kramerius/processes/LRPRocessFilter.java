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
package cz.incad.kramerius.processes;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Represents objects for filtering LRProcess list
 * @author pavels
 */
public class LRPRocessFilter {
    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(LRPRocessFilter.class.getName());
    
    static Map<String, ConverterAndFormatter> CONVERTERS = new HashMap<String, LRPRocessFilter.ConverterAndFormatter>(); 
    static {
        CONVERTERS.put("status", new IntegerConverter());
        CONVERTERS.put("batch_status", new IntegerConverter());
        CONVERTERS.put("planned", new DateConvereter());
        CONVERTERS.put("started", new DateConvereter());
        CONVERTERS.put("finished", new DateConvereter());
        CONVERTERS.put("default", new StringConverter());
    
    }
    
    private List<Tripple> tripples = new ArrayList<LRPRocessFilter.Tripple>();
    private List<Object> objectsToPreparedStm = new ArrayList<Object>();
    
    
    
    private LRPRocessFilter() {
    }
    
    /**
     * Add new filter tripple
     * @param tripple filter tripple
     */
    public void addTripple(Tripple tripple) {
        this.tripples.add(tripple);
    }
    
    /**
     * Remove the old filter tripple
     * @param tripple filter triple
     */
    public void removeTripple(Tripple tripple) {
        this.tripples.remove(tripple);
    }
    
    
    /**
     * Find tripple by given name
     * @param name Tripple name
     * @return Found tripple
     */
    public Tripple findTripple(String name) {
        for (Tripple trp : this.tripples) {
            if (trp.name.equals(name)) return trp;
        }
        return null;
    }
    
    /**
     * REturns all tripples
     * @return
     */
    public List<Tripple> getTripples() {
        return this.tripples;
    }
    
    
    /**
     * Creates new filter from given tripples array
     * @param triples Triples
     * @return new filter object
     */
    public static LRPRocessFilter createFilter(Tripple...triples) {
        LRPRocessFilter filter = new LRPRocessFilter();
        for (Tripple tr : triples) {
            filter.addTripple(tr);
        }
        return filter;
    }
    
    /**
     * Creates new filter from given tripples list
     * @param triples Triples
     * @return new filter object
     */
    public static LRPRocessFilter createFilter(List<Tripple> triples) {
        LRPRocessFilter filter = new LRPRocessFilter();
        for (Tripple tr : triples) {
            filter.addTripple(tr);
        }
        return filter;
    }
    
    /**
     * Returns objects to JDBC prepared statement
     * @return returns list
     */
    public List<Object> getObjectsToPreparedStm() {
        return objectsToPreparedStm;
    }
    
    /**
     * Returns filter in sql representation
     * @return sql represenation
     */
    public String getSQLOffset() {
        this.objectsToPreparedStm.clear();
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0,ll=this.tripples.size(); i < ll; i++) {
            Tripple trp = this.tripples.get(i);
            this.objectsToPreparedStm.add(trp.val);
            builder.append(trp.name).append(" ").append(trp.getOp().getRenderOperator()).append(" ");
            if (i < ll-1) { builder.append(" and "); }
        }
        
        if (builder.length() > 0) {
            return " where "+builder.toString();
        } else return "";
    }
    
    /**
     * Value converter
     * @author pavels
     */
    public interface ConverterAndFormatter {
        
        /**
         * Convert string value to concrete object
         * @param strVal String value
         * @return Created object
         */
        public Object convert(String strVal);

        /**
         * Format object to string value
         * @param val Object val
         * @return formatted string value
         */
        public String format(Object val);
    }
    
    public static class StringConverter implements ConverterAndFormatter {

        @Override
        public Object convert(String strVal) {
            return strVal;
        }

        @Override
        public String format(Object val) {
            return val != null ? val.toString() : null;
        }
        
    }

    public static  class IntegerConverter implements ConverterAndFormatter {

        @Override
        public Object convert(String strVal) {
            return Integer.parseInt(strVal);
        }

        @Override
        public String format(Object val) {
            return ""+val;
        }
        
        
    }
    

    
    public static class DateConvereter implements ConverterAndFormatter {

        public static SimpleDateFormat FORMATTER = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        
        @Override
        public Object convert(String strVal) {
            try {
                LOGGER.fine("parsing date "+strVal+" ");
                Timestamp timestamp = new Timestamp(FORMATTER.parse(strVal).getTime());
                return timestamp;
            } catch (ParseException e) {
                LOGGER.log(Level.SEVERE,e.getMessage(),e);
            }
            return null;
        }

        @Override
        public String format(Object val) {
            Timestamp tmsp = (Timestamp) val;
            Date date = new Date(tmsp.getTime());
            return FORMATTER.format(date);
        }
    }
    
    public static String getFormattedValue(Tripple tripple) {
        boolean contains = CONVERTERS.containsKey(tripple.getName());
        if (contains) {
            return  CONVERTERS.get(tripple.getName()).format(tripple.getVal());
        } else {
            return CONVERTERS.get("default").format(tripple.getVal());
        }
    }
    
    /**
     * Represents tripple in filter
     * @author pavels
     */
    public static class Tripple {
        
        private String name;
        private Object val;
        private Op op;
        
        public Tripple(String name, Object val, String opString) {
            super();
            this.name = name;
            this.val = val;
            this.op = Op.valueOf(opString);
        }

        public Tripple(String name, String val, String opString) {
            super();
            this.name = name;
            
            boolean contains = CONVERTERS.containsKey(name);
            if (contains) {
                this.val = CONVERTERS.get(name).convert(val);
            } else {
                this.val = CONVERTERS.get("default").convert(val);
            }
            this.op = Op.valueOf(opString);
        }

        /**
         * Returns name
         * @return name
         */
        public String getName() {
            return name;
        }

        /**
         * Returns value
         * @return value
         */
        public Object getVal() {
            return val;
        }
        
        /**
         * Tripple operator
         * @return operator
         */
        public Op getOp() {
            return op;
        }
    }
    
    /**
     * Operators value
     * @author pavels
     */
    public enum Op {
        /** = operator*/
        EQ("="," ?"), 
        
        /** < operator*/
        LT("<", " ?"), 
        
        /** > operator*/
        GT(">"," ?"), 
        
        /** like operator*/
        LIKE("like"," ?");
        
        private Op(String r, String valueOffset) {
            this.r = r;
            this.valueOffset = valueOffset;
        }

        public String getRawString() {
            return r;
        }
        
        public String getRenderOperator() {
            return this.r + this.valueOffset;
        }
        
        private String valueOffset;
        private String r;
        
        public static Op findByString(String opString) {
            Op[] values = Op.values();
            for (Op op : values) {
                if (op.getRawString().equals(opString)) return op;
            }
            return null;
        }
    }
    
    
    
}

