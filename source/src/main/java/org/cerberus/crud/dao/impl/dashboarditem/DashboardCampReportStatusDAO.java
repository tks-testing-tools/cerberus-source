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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.dao.IDashboardEntryDataDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.database.DatabaseSpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author cDelage
 */
@Repository
public class DashboardCampReportStatusDAO implements IDashboardEntryDataDAO {
    
    private static final Logger LOG = LogManager.getLogger(DashboardCampReportStatusDAO.class);
    
    @Autowired
    DatabaseSpring databaseSpring;
    
    @Override
    public Map<String, Object> readDataForDashboardEntry(DashboardEntry dashboardEntry) {
        Map<String, Object> response = new HashMap();
        String query = "SELECT `nbOK`, `nbKO`, `nbFA`, `nbNA`, `nbNE`, `nbWE`, `nbPE`, `nbQU`, `nbCA`, `CIScore` FROM `tag` WHERE Campaign = ? ORDER BY `DateEndQueue` DESC LIMIT ?;";
        try {
            Connection connection = databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query);
            
            //Parse limit parameter
            Integer limitVal = 1;
            try {
                limitVal = Integer.valueOf(dashboardEntry.getParam1Val());
            } catch (NumberFormatException exception) {
                LOG.error("Error during format limit of campaign report status : ", exception);
            }
            
            int i = 1;
            preStat.setString(i++, dashboardEntry.getAssociateElement());
            preStat.setInt(i++, limitVal);
            ResultSet rs = preStat.executeQuery();
            long index = 0;
            while (rs.next()) {
                index = response.size()/limitVal;
                response.put("nbOK_" + index, rs.getInt("nbOK"));
                response.put("nbKO_" + index, rs.getInt("nbKO"));
                response.put("nbFA_" + index, rs.getInt("nbFA"));
                response.put("nbNA_" + index, rs.getInt("nbNA"));
                response.put("nbNE_" + index, rs.getInt("nbNE"));
                response.put("nbWE_" + index, rs.getInt("nbWE"));
                response.put("nbPE_" + index, rs.getInt("nbPE"));
                response.put("nbQU_" + index, rs.getInt("nbQU"));
                response.put("nbCA_" + index, rs.getInt("nbCA"));
                response.put("CIScore_" + index, rs.getInt("CIScore"));
            }
        } catch (SQLException exception) {
            LOG.error("Error during load status from tag execution : ", exception);
        }
        return response;
    }
}
