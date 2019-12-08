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

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroupEntries;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.factory.IFactoryDashboardEntry;
import org.cerberus.crud.factory.IFactoryDashboardGroupEntries;
import org.cerberus.crud.service.IUserService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Dashboard webservice, target is read and update value of user dashboard
 *
 * @author cDelage
 */
@Path("/dashboard")
public class DashboardWebService {

    private static final Logger LOG = LogManager.getLogger(DashboardWebService.class);

    private IFactoryDashboardEntry factoryDashboardEntry;
    private IFactoryDashboardGroupEntries factoryDashboardGroupEntries;
    private IUserService userService;
    /*
     * return all dashboard entries classed sorted by dashboard group entries in map
     */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/poc")
    public Response poc(@Context ServletContext servletContext, @Context HttpServletRequest request) {
        LOG.info("READ DASHBOARD");
        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.factoryDashboardEntry = appContext.getBean(IFactoryDashboardEntry.class);
        this.factoryDashboardGroupEntries = appContext.getBean(IFactoryDashboardGroupEntries.class);
        this.userService = appContext.getBean(IUserService.class);
        Map<String, Object> maResponse = new HashMap();
        try {
            User user = userService.findUserByKey(request.getRemoteUser());
            DashboardEntry dashboardEntry = this.factoryDashboardEntry.create("TOTO", null, "TOTOPARAM", "TOTOPARAM2");
            DashboardGroupEntries dashboardGroupEntries = this.factoryDashboardGroupEntries.create("TITI", user, null, 1);
            maResponse.put("dashboard entry", dashboardEntry);
            maResponse.put("dashboard group entries", dashboardGroupEntries);
        } catch (Exception e) {
            LOG.error("Exception catch during recup user process : {}", e);
        }

        return Response.ok(maResponse).status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/read")
    public Response readDashboard(@Context ServletContext servletContext, @Context HttpServletRequest request) {
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
