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

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.dao.impl.dashboarditem.DashboardCampaignEvolutionDAO;
import org.cerberus.crud.dao.impl.dashboarditem.DashboardCampaignLastReportByStatusDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.service.IDashboardEntryDataService;
import org.cerberus.enums.MessageEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author CorentinDelage
 */
@Service
public class DashboardEntryDataService implements IDashboardEntryDataService {

    @Autowired
    private DashboardCampaignEvolutionDAO dashboardCampaignEvolutionDAO;

    @Autowired
    private DashboardCampaignLastReportByStatusDAO dashboardCampainLastReportByStatusDAO;

    private static final Logger LOG = LogManager.getLogger(DashboardEntryService.class);

    public Map<String, Object> read(DashboardEntry dashboardEntry) {
        LOG.debug("Read dashboard data for entry : ", dashboardEntry.getCodeIndicator());
        Map<String, Object> dashboardEntryData = new HashMap();
        boolean dataFailed = false;
        try {
            switch (dashboardEntry.getCodeIndicator()) {
                case "CAMPAIGN_EVOLUTION":
                    dashboardEntryData = dashboardCampaignEvolutionDAO.readDataForDashboardEntry(dashboardEntry);
                    break;
                case "CAMPAIGN_LAST_REPORT_DETAIL":
                    dashboardEntryData = dashboardCampainLastReportByStatusDAO.readDataForDashboardEntry(dashboardEntry);
                    break;
                default:
                    dashboardEntryData.put("Unknown report item type", "Error");
                    dashboardEntryData.put("MessageEvent", MessageEventEnum.DASHBOARD_READ_DATA_FAILED);
                    dataFailed = true;
            }
        } catch (Exception e) {
            LOG.error("Catch exception during read data for dashboard : ", e);
        }
        if (!dataFailed) {
            if (dashboardEntryData.size() == 0) {
                dashboardEntryData.put("MessageEvent", MessageEventEnum.DASHBOARD_READ_DATA_EMPTY);
            } else {
                dashboardEntryData.put("MessageEvent", MessageEventEnum.DASHBOARD_READ_DATA_SUCCESS);
            }
        }

        return dashboardEntryData;
    }

}
