/* Cerberus Copyright (C) 2013 - 2017 cerberustesting
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

 This file is part of Cerberus.

 Cerberus is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Cerberus is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Cerberus.  If not, see <http://www.gnu.org/licenses/>.*/
package org.cerberus.webservice;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Dashboard webservice, target is read and update value of user dashboard
 *
 * @author cDelage
 */
@Path("/dashboard")
public class DashboardWebService {

    private static final Logger LOG = LogManager.getLogger(DashboardWebService.class);

    /*
    * return all dashboard entries classed sorted by dashboard group entries in map
    */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/read")
    public Response readDashboard(@Context ServletContext servletContext) {
        LOG.info("READ DASHBOARD");
        return Response.ok("read dashboard actually not implemented").status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();
    }

    /*
    * update dashboard entries for user. take dashboard group entries parameter and return update status.
    */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response update(@Context ServletContext servletContext) {
        LOG.info("UPDATE DASHBOARD");
        return Response.ok("update dashboard actually not implemented").status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();
    }

    /*
    * read existing report item sorted by type
    */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/readrepitem")
    public Response readReportItem(@Context ServletContext servletContext) {
        LOG.info("READ DASHBOARD");
        return Response.ok("read dashboard actually not implemented").status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();
    }
}
