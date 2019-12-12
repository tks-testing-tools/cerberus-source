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
package org.cerberus.crud.dao.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.dao.IDashboardEntryDataDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.springframework.stereotype.Repository;

/**
 *
 * @author cDelage
 */
@Repository
public class DashboardEntryDataDAO implements IDashboardEntryDataDAO {

    private static final Logger LOG = LogManager.getLogger(DashboardEntryDAO.class);

    @Override
    public Map<String, Object> readDataForDashboardEntry(DashboardEntry dashboardEntry) {
        Map<String, Object> response = new HashMap();
        try {
            switch (dashboardEntry.getCodeReportItem()) {
                case "CAMPAIGN_EVOLUTION":
                    response.put("CAMPAIGN_EVOLUTION DATA", "data of campaign evolution");
                    break;
                default:
                    response.put("INVALID_REPORT_ITEM", "Report item undefined in Read data dashboard entry DAO");
            }
        } catch (Exception exception) {
            LOG.error("Catch exception during loading data : ", exception);
        }
        return response;

    }
}
