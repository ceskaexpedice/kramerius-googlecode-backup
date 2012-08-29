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
package org.kramerius.replications;

import java.io.File;
import java.io.IOException;

public abstract class AbstractPhase implements Phase{

    public static String ITERATE_FILE = "iterate";

    public File createIterateFile() throws IOException, PhaseException {
        File iterate = new File(ITERATE_FILE);
        if (!iterate.exists()) iterate.createNewFile();
        if (!iterate.exists()) throw new PhaseException("cannot create file '"+iterate+"'");
        return iterate;
    }

    public File getIterateFile() throws PhaseException {
        File iterate = new File(ITERATE_FILE);
        if ((!iterate.exists()) && (!iterate.canRead())) throw new PhaseException("cannot create file '"+iterate+"'");
        return iterate;
    }

    
}
