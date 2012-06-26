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
package cz.incad.Kramerius.exts.menu.context.impl.adm.items;

import java.io.IOException;

import cz.incad.Kramerius.exts.menu.context.impl.AbstractContextMenuItem;
import cz.incad.Kramerius.exts.menu.context.impl.adm.AdminContextMenuItem;
import cz.incad.Kramerius.views.item.menu.ContextMenuItem;
import cz.incad.kramerius.security.SecuredActions;

public class VirtualCollectionAdd extends AbstractContextMenuItem implements AdminContextMenuItem {

    //
//    adminItems.add(new ContextMenuItem("administrator.menu.virtualcollection.add", "_data_x_role", "vcAddToVirtualCollection",
//            "", true));

    @Override
    public boolean isMultipleSelectSupported() {
        return true;
    }

    @Override
    public boolean isRenderable() {
        return true;
    }

    @Override
    public String getRenderedItem() throws IOException {
        return super.renderContextMenuItem("javascript:vcAddToVirtualCollection();", "administrator.menu.virtualcollection.add");
    }

    
}
