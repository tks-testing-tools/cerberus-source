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
import org.cerberus.crud.dao.IDashboardEntryDAO;
import org.cerberus.crud.dao.IDashboardEntryDataDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroupEntries;
import org.cerberus.crud.service.IDashboardEntryDataService;
import org.cerberus.crud.service.IDashboardEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author cDelage
 */
@Service
public class DashboardEntryService implements IDashboardEntryService {

    private static final Logger LOG = LogManager.getLogger(DashboardEntryService.class);

    @Autowired
    private IDashboardEntryDAO dashboardEntryDAO;
    
    @Autowired
    private IDashboardEntryDataService dashboardEntryDataService;

    @Override
    public List<DashboardEntry> readByGroupEntriesWithData(DashboardGroupEntries dashboardGroupEntries) {
        List<DashboardEntry> response = new ArrayList();
        response = this.readByGroupEntries(dashboardGroupEntries);
        for (DashboardEntry ent : response) {
            ent.setEntryData(this.dashboardEntryDataService.read(ent));
        }
        return response;
    }

    @Override
    public List<DashboardEntry> readByGroupEntries(DashboardGroupEntries dashboardGroupEntries) {
        return dashboardEntryDAO.readByGroupEntries(dashboardGroupEntries);
    }
    
    /**
     * Create a dashboard entry
     * @param pReportItemCode report item code string
     * @param pParamId1 first param of the dashboard
     * @param pParamId2 second param of the dashboard
     * @return the new id group entries
     */
    @Override
    public String create(int pIdGroupEntries,String pReportItemCode, String pParamId1, String pParamId2) {
        return dashboardEntryDAO.create(pIdGroupEntries, pReportItemCode, pParamId1, pParamId2);
    }
    
}
