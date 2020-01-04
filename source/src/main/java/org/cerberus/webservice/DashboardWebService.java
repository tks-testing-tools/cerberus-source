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

import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.service.IDashboardConfigService;
import org.cerberus.crud.service.IUserService;
import org.cerberus.dto.DashboardGroupConfigDTO;
import org.cerberus.dto.DashboardIndicatorConfigDTO;
import org.cerberus.dto.DashboardTypeConfigDTO;
import org.cerberus.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

    private IUserService userService;
    private IDashboardConfigService dashboardConfigService;

    /**
     * Read dashboard content and config for user
     *
     * @param servletContext
     * @param request
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/read")
    public Response readDashboard(@Context ServletContext servletContext, @Context HttpServletRequest request) {
        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.dashboardConfigService = appContext.getBean(IDashboardConfigService.class);
        this.userService = appContext.getBean(IUserService.class);
        User currentUser = new User();
        
        if (request.getRemoteUser() != null) {
            try {
                currentUser = userService.findUserByKey(request.getRemoteUser());
            } catch (Exception exception) {
                LOG.error("Exception during read user process : ", exception);
            }
            return Response.ok(dashboardConfigService.readDashboard(currentUser,"CURRENT")).status(200)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Credentials", "true")
                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();
        }
        return Response.ok("User exception, please contact your administrator").status(400)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD").build();
    }

    /**
     * Update dashboard statement for an user.
     *
     * @param servletContext
     * @param request
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/save")
    public Response save(@Context ServletContext servletContext, @Context HttpServletRequest request, String jsonReq) {

        //Load Dashboard and user services from context
        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        this.dashboardConfigService = appContext.getBean(IDashboardConfigService.class);
        this.userService = appContext.getBean(IUserService.class);

        //Load user
        User currentUser = new User();
        if (request.getRemoteUser() != null) {
            try {
                currentUser = userService.findUserByKey(request.getRemoteUser());
            } catch (Exception exception) {
                LOG.error("Exception during read user process : ", exception);
            }

            //Load dashboard config
            try {
                if (!StringUtil.isNullOrEmpty(jsonReq)) {
                    JSONObject jsonConf = new JSONObject(jsonReq);
                    JSONObject conf = jsonConf.getJSONObject("DashboardConfig");
                    JSONArray confContent = conf.getJSONArray("Config");


                    List<DashboardTypeConfigDTO> confExtract = this.loadConfFromRequest(confContent);
                    String title = conf.getString("TitleConfig");
                    LOG.debug("Title : " + title);
                    //Response to user : event list from update dashboard process
                    return Response.ok(this.dashboardConfigService.saveConfig(confExtract, title, currentUser, currentUser)).status(200)
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Access-Control-Allow-Credentials", "true")
                            .build();
                }

            } catch (JSONException exception) {
                LOG.error("Catch exception during json reading ", exception);
                return Response.ok("CATCH JSON EXCEPTION : " + exception.getLocalizedMessage()).status(401)
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Credentials", "true")
                        .build();
            }
        }
        return Response.ok("Fail to update dashboard, please contact your administrator").status(400)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .build();
    }

    /**
     * Load config from front request sent. use in update Dashboard process.
     *
     * @param array
     * @return
     */
    public List<DashboardTypeConfigDTO> loadConfFromRequest(JSONArray array) {
        List<DashboardTypeConfigDTO> conf = new ArrayList();
        try {
            //For each type of group
            for (int i = 0; i < array.length(); i++) {
                LOG.debug("Try to convert type from request" + array.getJSONObject(i).toString());
                JSONObject typeConf = array.getJSONObject(i);

                //Construct my type
                String typeIndicator = typeConf.getString("typeIndicator");

                List<DashboardGroupConfigDTO> grpList = new ArrayList();
                JSONArray grp = typeConf.getJSONArray("groupList");
                //For all group of type
                for (int j = 0; j < grp.length(); j++) {
                    LOG.debug("Try to convert group from request " + grp.getJSONObject(j).toString());
                    JSONObject group = grp.getJSONObject(j);
                    boolean isActiveGroup = group.getBoolean("isActive");

                    if (isActiveGroup) {
                        //Construct my group
                        String title = group.getString("title");
                        Integer sort = group.getInt("sort");
                        String type = typeConf.getString("typeIndicator");

                        List<DashboardIndicatorConfigDTO> indList = new ArrayList();
                        JSONArray ind = group.getJSONArray("availabilityList");
                        //For all indicator of group
                        for (int k = 0; k < ind.length(); k++) {
                            LOG.debug("Try to convert indicator from request " + ind.getJSONObject(k).toString());
                            JSONObject indicator = ind.getJSONObject(k);

                            boolean isActiveIndicator = indicator.getBoolean("isActive");

                            if (isActiveIndicator) {
                                //Construct my indicator
                                String codeIndicator = indicator.getString("codeIndicator");
                                String param1 = indicator.getString("param1Value");
                                String param2 = indicator.getString("param2Value");
                                String param3 = indicator.getString("param3Value");
                                String param4 = indicator.getString("param4Value");
                                indList.add(new DashboardIndicatorConfigDTO(codeIndicator, param1, param2, param3, param4));
                            }
                        }

                        //Add my group to type
                        grpList.add(new DashboardGroupConfigDTO(title, sort, type, indList, true, false));
                    }

                }

                conf.add(new DashboardTypeConfigDTO(typeIndicator, grpList));
            }

        } catch (JSONException exception) {
            LOG.error("Exception during Dashboard config extract", exception);
            conf.clear();
        }

        return conf;
    }

}
