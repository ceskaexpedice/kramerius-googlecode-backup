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
package cz.incad.Kramerius.security.strenderers;

import cz.incad.kramerius.security.AbstractUser;
import cz.incad.kramerius.security.EvaluatingResult;
import cz.incad.kramerius.security.Right;
import cz.incad.kramerius.security.RightCriterium;
import cz.incad.kramerius.security.RightCriteriumContext;
import cz.incad.kramerius.security.RightCriteriumException;
import cz.incad.kramerius.security.SpecialObjects;

public class RightWrapper implements Right{
    
    private Right right;

    public RightWrapper(Right right) {
        super();
        this.right = right;
    }

    public int getId() {
        return right.getId();
    }

    
    public String getPid() {
        if (("uuid:"+SpecialObjects.REPOSITORY.getUuid()).equals(right.getPid())) {
            return SpecialObjects.REPOSITORY.name();
        } else  return right.getPid();
    }
    
    public String getUuid() {
        String pid = right.getPid();
        return pid.substring("uuid:".length());
    }

    public String getAction() {
        return right.getAction();
    }

    public AbstractUser getUser() {
        return  new AbstractUserWrapper(right.getUser());
    }

    public void setUser(AbstractUser user) {
        throw new UnsupportedOperationException("this is unsupported");
    }

    public RightCriterium getCriterium() {
        return new CriteriumWrapper(right.getCriterium());
    }
    
    @Override
    public void setCriterium(RightCriterium rightCriterium) {
        throw new UnsupportedOperationException("this is unsupported");
    }

    public EvaluatingResult evaluate(RightCriteriumContext ctx) throws RightCriteriumException {
        throw new IllegalStateException();
    }
    
    public static RightWrapper[] wrapRights(Right...rights) {
        RightWrapper[] wrappers = new RightWrapper[rights.length];
        for (int i = 0; i < rights.length; i++) {
            wrappers[i]= new RightWrapper(rights[i]);
        }
        return wrappers;
    }

    @Override
    public void setAction(String action) {
        throw new UnsupportedOperationException("this is unsupported!");
    }
    
}
