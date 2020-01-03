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
import org.cerberus.crud.dao.impl.dashboarditem.DashboardCampaignLastExeDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.service.IDashboardEntryDataService;
import org.cerberus.dto.MessageEventSlimDTO;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.util.DashboardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service use to search data for dashboard indicator.
 * @author CorentinDelage
 */
@Service
public class DashboardEntryDataService implements IDashboardEntryDataService {

    @Autowired
    private DashboardCampaignEvolutionDAO dashboardCampaignEvolutionDAO;

    @Autowired
    private DashboardCampaignLastExeDAO dashboardCampainLastReportByStatusDAO;

    private static final Logger LOG = LogManager.getLogger(DashboardEntryService.class);

    public Map<String, Object> read(DashboardEntry dashboardEntry) {

        LOG.debug("Read dashboard data for entry : " + dashboardEntry.getCodeIndicator());
        Map<String, Object> dashboardEntryData = new HashMap();
        boolean dataFailed = false;

        try {
            switch (dashboardEntry.getCodeIndicator()) {
                case "CAMPAIGN_EVOLUTION":

                    Integer yLadder = 10;

                    if (!dashboardEntry.getParam3Val().equals("DEFAULT")) {
                        try {
                            yLadder = Integer.valueOf(dashboardEntry.getParam3Val());
                            LOG.debug("Y LADDER : "+ yLadder);
                        } catch (NumberFormatException exception) {
                            LOG.error("Invalid param ladder for campaign evolution indicator, it will be compute for 10 values by default. Exception : ", exception);
                        }
                    }
                    
                    dashboardEntryData = dashboardCampaignEvolutionDAO.readDataForDashboardEntry(dashboardEntry);
                    long nbEntry = DashboardUtil.computeNbOfEntry(dashboardEntryData, 2);
                    dashboardEntryData = DashboardUtil.reduceMapForChart(dashboardEntryData, yLadder, false, false, false);
                    dashboardEntryData = DashboardUtil.computeAdvancement(dashboardEntryData, 2);
                    dashboardEntryData.put("Y_LADDER", DashboardUtil.generateLadder(dashboardEntryData, 2, 1, 1.25));
                    dashboardEntryData.put("INITIAL_TOTAL_TAG",nbEntry);
                    dashboardEntryData.put("FINAL_TOTAL_TAG", DashboardUtil.computeNbOfEntry(dashboardEntryData, 2));
                    break;
                case "CAMPAIGN_LAST_EXE_DETAIL":
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
                dashboardEntryData.put("MessageEvent", new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_READ_DATA_EMPTY));
            } else {
                dashboardEntryData.put("MessageEvent", new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_READ_DATA_SUCCESS));
            }
        }

        return dashboardEntryData;
    }

}
