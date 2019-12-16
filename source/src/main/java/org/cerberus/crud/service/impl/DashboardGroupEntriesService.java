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
import org.cerberus.crud.dao.IDashboardGroupEntriesDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroupEntries;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.factory.IFactoryDashboardGroupEntries;
import org.cerberus.crud.service.IDashboardEntryService;
import org.cerberus.crud.service.IDashboardGroupEntriesService;
import org.cerberus.dto.DashboardGroupEntriesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author utilisateur
 */
@Service
public class DashboardGroupEntriesService implements IDashboardGroupEntriesService {

    private static final Logger LOG = LogManager.getLogger(DashboardGroupEntriesService.class);

    @Autowired
    private IDashboardGroupEntriesDAO dashboardGroupEntriesDAO;

    @Autowired
    private IDashboardEntryService dashboardEntryService;

    @Autowired
    private IFactoryDashboardGroupEntries factoryDashboardGroupEntries;

    @Override
    public List<DashboardGroupEntries> readByUser(User user) {
        return this.dashboardGroupEntriesDAO.readByUser(user);
    }

    @Override
    public Map<String, Object> readDashboard(User user) {
        Map<String, Object> response = new HashMap();
        List<DashboardGroupEntriesDTO> dashboardGroupListDTO = new ArrayList();
        List<DashboardGroupEntries> dashboardGroupList = new ArrayList();
        dashboardGroupList = this.readByUser(user);
        for (DashboardGroupEntries grp : dashboardGroupList) {
            grp.setDashboardEntries(this.dashboardEntryService.readByGroupEntriesWithData(grp));
            dashboardGroupListDTO.add(this.dashboardGroupEntriesToDTO(grp));
        }
        response.put("DashboardGroupEntriesList", dashboardGroupListDTO);
        return response;
    }

    @Override
    public DashboardGroupEntriesDTO dashboardGroupEntriesToDTO(DashboardGroupEntries dashboardGroupEntries) {
        Integer id = dashboardGroupEntries.getId();
        String codeGroupEntries = dashboardGroupEntries.getCodeGroupEntries();
        List<DashboardEntry> dashboardEntries = dashboardGroupEntries.getDashboardEntries();
        String sort = dashboardGroupEntries.getSort();
        String type = dashboardGroupEntries.getType();
        String associateElement = dashboardGroupEntries.getAssociateElement();
        return new DashboardGroupEntriesDTO(id, codeGroupEntries, dashboardEntries, sort, associateElement, type);
    }

    public DashboardGroupEntries dashboardGroupEntriesFromDTO(DashboardGroupEntriesDTO dashboardGroupEntriesDTO, User user) {
        String codeGroupEntries = dashboardGroupEntriesDTO.getCodeGroupEntries();
        List<DashboardEntry> dashboardEntries = dashboardGroupEntriesDTO.getDashboardEntries();
        String sort = dashboardGroupEntriesDTO.getSort();
        String associateElement = dashboardGroupEntriesDTO.getAssociateElement();
        String type = dashboardGroupEntriesDTO.getType();
        return this.factoryDashboardGroupEntries.create(null, codeGroupEntries, user, dashboardEntries, sort, associateElement, type);
    }

    @Override
    public Integer create(String codeGroupEntries, int sort, int dashboardUserId, int reportItemType) {
        return dashboardGroupEntriesDAO.create(codeGroupEntries, sort, dashboardUserId, reportItemType);
    }

    @Override
    public String cleanByUser(User user) {
        return dashboardGroupEntriesDAO.cleanByUser(user);
    }

}
