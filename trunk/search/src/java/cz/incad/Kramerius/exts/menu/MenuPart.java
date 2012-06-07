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
package cz.incad.Kramerius.exts.menu;

/**
 * Represetns one menu part
 * @author pavels
 */
public interface MenuPart{
    
    /**
     * Returns true if this part should be rendered
     * @return
     */
    public boolean isRenderable();
    
    /**
     * Returns part's formal name
     * @return
     */
    public String getFormalName();

    public void registerItem(MenuItem item);
    
    public void deregisterItem(MenuItem item);

    /**
     * Returns all part's items
     * @return
     */
    public MenuItem[] getItems();
    
}
