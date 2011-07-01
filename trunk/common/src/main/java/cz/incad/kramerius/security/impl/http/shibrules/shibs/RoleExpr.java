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
package cz.incad.kramerius.security.impl.http.shibrules.shibs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cz.incad.kramerius.security.Group;
import cz.incad.kramerius.security.User;
import cz.incad.kramerius.security.impl.UserImpl;

public class RoleExpr implements Expr {
    
    private Value value;
    
    public RoleExpr(Value value) {
        super();
        this.value = value;
    }


    @Override
    public void evaluate(ShibContext ctx) {
        Group grole = ctx.getUserManager().findGroupByName(this.value.getValue(ctx.getHttpServletRequest()));
        User user = ctx.getUser();
        Group[] groups = user.getGroups() == null ? new Group[0]:user.getGroups();
        List<Group> grpList = new ArrayList<Group>(Arrays.asList(groups));
        grpList.add(grole);
        ((UserImpl)user).setGroups(grpList.toArray(new Group[grpList.size()]));
    }
}
