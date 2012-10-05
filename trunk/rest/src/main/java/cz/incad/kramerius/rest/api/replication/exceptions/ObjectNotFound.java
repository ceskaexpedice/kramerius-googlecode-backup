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
package cz.incad.kramerius.rest.api.replication.exceptions;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cz.incad.kramerius.rest.api.processes.exceptions.ExceptionMessageBuilder;

/**
 * Object is not found in K4
 * @author pavels
 */
public class ObjectNotFound extends WebApplicationException {

    public ObjectNotFound(String message) {
        super(Response.status(HttpServletResponse.SC_NOT_FOUND).entity(ExceptionMessageBuilder.createErrorMessage(404,message)).type(MediaType.APPLICATION_JSON).build());
    }
}
