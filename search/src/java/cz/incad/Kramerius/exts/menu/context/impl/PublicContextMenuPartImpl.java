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
package cz.incad.Kramerius.exts.menu.context.impl;

import cz.incad.Kramerius.exts.menu.context.ContextMenuPart;
import cz.incad.Kramerius.exts.menu.impl.AbstractMenuPart;

public class PublicContextMenuPartImpl extends AbstractMenuPart implements ContextMenuPart {

    public static final String FORMAL_NAME="PUBLIC";
    
    @Override
    public String getFormalName() {
        return FORMAL_NAME;
    }

    @Override
    public boolean isRenderable() {
        // TODO Auto-generated method stub
        return false;
    }
}
