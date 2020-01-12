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
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * @author CorentinDelage
 */
@Repository
public class DashboardCampEvolutionDAO implements IDashboardEntryDataDAO {

    @Autowired
    private DatabaseSpring databaseSpring;

    private static final Logger LOG = LogManager.getLogger(DashboardCampLastExeDAO.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

    private final int MAX_ROW_SELECTED = 10000;

    /**
     *
     * @param dashboardEntry
     * @return
     */
    public Map<String, Object> readDataForDashboardEntry(DashboardEntry dashboardEntry) {
        Map<String, Object> response = new HashMap();
        StringBuilder query = new StringBuilder();
        boolean param1 = false;
        boolean param2 = false;
        query.append("SELECT `DateEndQueue`,`nbExe`  FROM tag WHERE `Campaign` = ? ");

        //Construct request with param if necessary
        //Param 1 : Start date
        if (!dashboardEntry.getParam1Val().equals("DEFAULT")) {
            query.append("AND `DateEndQueue` >= ? ");
            param1 = true;
        }
        //Param 2 : End date
        if (!dashboardEntry.getParam2Val().equals("DEFAULT")) {
            query.append("AND `DateEndQueue` <= ? ");
            param2 = true;
        }

        query.append("ORDER BY `DateEndQueue` ASC LIMIT ");
        query.append(this.MAX_ROW_SELECTED);
        query.append(";");

        try {
            Connection connection = this.databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query.toString());
            int i = 1;
            preStat.setString(i++, dashboardEntry.getAssociateElement());
            if (param1) {
                preStat.setString(i++, dashboardEntry.getParam1Val());
            }
            if (param2) {
                preStat.setString(i++, dashboardEntry.getParam2Val());
            }
            ResultSet rs = preStat.executeQuery();
            while (rs.next()) {
                //Index is use to format KEY_Index VALUE_X_Index for charts (format for dashboardUtil work)
                Integer index = response.size() / 2;
                response.put("KEY_" + index, dateFormat.format(new Date(rs.getTimestamp("DateEndQueue").getTime())));
                response.put("VALUE_1_" + index, rs.getInt("nbExe"));
            }
            connection.close();
        } catch (SQLException e) {
            LOG.error("Error to read campaign evolution, catch exception : ", e);
        }
        return response;
    }
}
