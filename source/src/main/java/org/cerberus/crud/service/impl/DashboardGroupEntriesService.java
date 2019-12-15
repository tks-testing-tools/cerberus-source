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
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.dao.IDashboardGroupEntriesDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroupEntries;
import org.cerberus.crud.entity.User;
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

    @Override
    public List<DashboardGroupEntries> readByUser(User user) {
        return this.dashboardGroupEntriesDAO.readByUser(user);
    }

    @Override
    public List<DashboardGroupEntriesDTO> readDashboard(User user) {
        List<DashboardGroupEntriesDTO> response = new ArrayList();
        List<DashboardGroupEntries> dashboardGroupEntriesList = new ArrayList();
        dashboardGroupEntriesList = this.readByUser(user);
        for (DashboardGroupEntries grp : dashboardGroupEntriesList) {
            // ADD ASSOCIATE ELEMENT
            grp.setDashboardEntries(this.dashboardEntryService.readByGroupEntriesWithData(grp));
            response.add(this.dashboardGroupEntriesToDTO(grp));
        }
        return response;
    }

    @Override
    public DashboardGroupEntriesDTO dashboardGroupEntriesToDTO(DashboardGroupEntries dashboardGroupEntries) {
        Integer id = dashboardGroupEntries.getId();
        String codeGroupEntries = dashboardGroupEntries.getCodeGroupEntries();
        List<DashboardEntry> dashboardEntries = dashboardGroupEntries.getDashboardEntries();
        String sort = dashboardGroupEntries.getSort();
        return new DashboardGroupEntriesDTO(id, codeGroupEntries, dashboardEntries, sort, null);
    }

    @Override
    public Integer create(String pCodeGroupeEntries, int pSort, int pDashboardUserId, int pReportItemType) {
        return dashboardGroupEntriesDAO.create(pCodeGroupeEntries, pSort, pDashboardUserId, pReportItemType);
    }

    @Override
    public String cleanByUser(User user) {
        return dashboardGroupEntriesDAO.cleanByUser(user);
    }
}
