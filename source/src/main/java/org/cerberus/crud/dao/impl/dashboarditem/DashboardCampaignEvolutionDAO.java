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
package org.cerberus.crud.dao.impl.dashboarditem;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.dao.IDashboardEntryDataDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.springframework.stereotype.Repository;

/**
 *
 * @author CorentinDelage
 */
@Repository
public class DashboardCampaignEvolutionDAO implements IDashboardEntryDataDAO {

    private static final Logger LOG = LogManager.getLogger(DashboardCampaignEvolutionDAO.class);

    /**
     * 
     * @param dashboardEntry
     * @return 
     */
    public Map<String, Object> readDataForDashboardEntry(DashboardEntry dashboardEntry) {
        LOG.debug("READ DATA FOR DASHBOARD ENTRY");
        Map<String, Object> response = new HashMap();
        try {
            //Requete
            response.put("Start", "Testvalues");
        } catch (Exception e) {
            LOG.error("Error to read campaign evolution, catch exception : ", e);
            response.put("EXCEPTION", e);
        }
        return response;
    }

    public Map<String, Object> loadFromResultSet(ResultSet rs) {
        Map<String, Object> response = new HashMap();
        return response;
    }

}
