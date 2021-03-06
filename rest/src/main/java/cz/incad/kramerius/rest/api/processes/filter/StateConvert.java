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
package cz.incad.kramerius.rest.api.processes.filter;

import java.util.logging.Level;

import cz.incad.kramerius.processes.States;

public class StateConvert implements Convert {

    static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(StateConvert.class.getName());
    
    @Override
    public Object convert(String str) {
        try {
            States st = States.valueOf(str);
            return st.getVal();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
        }
        return str;
    }

    
}
