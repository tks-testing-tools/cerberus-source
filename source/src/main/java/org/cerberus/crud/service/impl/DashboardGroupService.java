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
package org.cerberus.crud.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.dao.IDashboardGroupDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroup;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.factory.IFactoryDashboardGroup;
import org.cerberus.crud.service.IDashboardEntryService;
import org.cerberus.crud.service.IDashboardGroupService;
import org.cerberus.dto.DashboardGroupDTO;
import org.cerberus.dto.DashboardIndicatorDTO;
import org.cerberus.dto.DashboardTypeIndicatorDTO;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.enums.DashboardIndicatorEnum;
import org.cerberus.enums.DashboardTypeIndicatorEnum;
import static org.cerberus.enums.DashboardTypeIndicatorEnum.values;
import org.cerberus.enums.MessageEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author utilisateur
 */
@Service
public class DashboardGroupService implements IDashboardGroupService {

    private static final Logger LOG = LogManager.getLogger(DashboardGroupService.class);

    @Autowired
    private IDashboardGroupDAO dashboardGroupEntriesDAO;

    @Autowired
    private IDashboardEntryService dashboardEntryService;

    @Autowired
    private IFactoryDashboardGroup factoryDashboardGroupEntries;

    @Override
    public Map<String, Object> readDashboard(User user) {

        LOG.debug("Read dashboard for user : ", user.getLogin());

        Map<String, Object> response = new HashMap();
        List<DashboardGroupDTO> dashboardGroupListDTO = new ArrayList();
        List<DashboardGroup> dashboardGroupList = new ArrayList();

        //Read all group for Dashboard
        dashboardGroupList = this.readByUser(user);

        //Get all dashboard entry for groups
        for (DashboardGroup grp : dashboardGroupList) {
            grp.setDashboardEntries(this.dashboardEntryService.readByGroupEntriesWithData(grp));
            dashboardGroupListDTO.add(this.dashboardGroupEntriesToDTO(grp));
        }
        response.put("DashboardGroup", dashboardGroupListDTO);

        //Control size of groups for send MESSAGE_EVENT empty or success
        if (dashboardGroupListDTO.isEmpty()) {
            response.put("MessageEvent_Group", MessageEventEnum.DASHBOARD_READ_GROUP_EMPTY);
        } else if (dashboardGroupListDTO.size() > 0) {
            response.put("MessageEvent_Group", MessageEventEnum.DASHBOARD_READ_GROUP_SUCCESS);
        }

        List<DashboardTypeIndicatorDTO> availableIndicator = new ArrayList();

        availableIndicator = DashboardTypeIndicatorEnum.getDashboardPossibility();
        response.put("Dashboard_Indicator_Availability", availableIndicator);
        
        if (availableIndicator.isEmpty()) {
            response.put("MessageEvent_availability", MessageEventEnum.DASHBOARD_READ_AVAILABILITY_FAILED);
        } else {
            response.put("MessageEvent_availability", MessageEventEnum.DASHBOARD_READ_AVAILABILITY_SUCCESS);
        }

        return response;
    }

    @Override
    public List<DashboardTypeIndicatorDTO> readDashboardPossibility() {
        return DashboardTypeIndicatorEnum.getDashboardPossibility();
    }

    @Override
    public DashboardGroupDTO dashboardGroupEntriesToDTO(DashboardGroup dashboardGroupEntries) {
        Integer id = dashboardGroupEntries.getId();
        List<DashboardEntry> dashboardEntries = dashboardGroupEntries.getDashboardEntries();
        String sort = dashboardGroupEntries.getSort();
        String type = dashboardGroupEntries.getType();
        String associateElement = dashboardGroupEntries.getAssociateElement();
        return new DashboardGroupDTO(id, dashboardEntries, sort, associateElement, type);
    }

    public DashboardGroup dashboardGroupEntriesFromDTO(DashboardGroupDTO dashboardGroupEntriesDTO, User user) {
        List<DashboardEntry> dashboardEntries = dashboardGroupEntriesDTO.getDashboardEntries();
        String sort = dashboardGroupEntriesDTO.getSort();
        String associateElement = dashboardGroupEntriesDTO.getAssociateElement();
        String type = dashboardGroupEntriesDTO.getType();
        return this.factoryDashboardGroupEntries.create(null, user, dashboardEntries, sort, associateElement, type);
    }

    @Override
    public Integer create(DashboardGroup dashboardGroup) {
        return dashboardGroupEntriesDAO.create(dashboardGroup);
    }

    @Override
    public Map<String, Object> cleanByUser(User user) {
        Map<String, Object> response = new HashMap();
        String cleanResponse = dashboardGroupEntriesDAO.cleanByUser(user);
        response.put("RESPONSE", cleanResponse);
        if (cleanResponse.equals("CLEAN SUCESSFULLY")) {
            response.put("MessageEvent", MessageEventEnum.DASHBOARD_DELETE_SUCCESS);
        } else {
            response.put("MessageEvent", MessageEventEnum.DASHBOARD_DELETE_GROUP_FAILED);
        }
        return response;
    }

    @Override
    public List<DashboardGroup> readByUser(User user) {
        return this.dashboardGroupEntriesDAO.readByUser(user);
    }
}
