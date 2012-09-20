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
package cz.incad.kramerius.database.cond;

import cz.incad.kramerius.database.utils.VersionVal;

/**
 * @author pavels
 *
 */
public class LessEq implements Condition {

    @Override
    public String getOperatorString() {
        return "<=";
    }

    @Override
    public boolean execute(String leftOperand, String rightOperand) {
        int leftVal = VersionVal.interpretVersionValue(leftOperand);
        int rightVal = VersionVal.interpretVersionValue(rightOperand);
        return leftVal <= rightVal;
    }

    
}
