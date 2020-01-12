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
 * @author cDelage
 */
@Repository
public class DashboardCampExeFreqDAO implements IDashboardEntryDataDAO {

    @Autowired
    private DatabaseSpring databaseSpring;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
    private static final Logger LOG = LogManager.getLogger(DashboardCampLastExeDAO.class);

    @Override
    public Map<String, Object> readDataForDashboardEntry(DashboardEntry dashboardEntry) {
        Map<String, Object> response = new HashMap();
        boolean startPararm = false;
        boolean endParam = false;
        String finalTime = "WEEK";

        StringBuilder query = new StringBuilder("SELECT Count(id) as nbExe, DateEndQueue ");
        if (dashboardEntry.getParam1Val().equals("MONTH")) {
            query.append(",Month(`DateEndQueue`) as timeField from tag WHERE `Campaign` = ? ");
            finalTime = "MONTH";
        } else if (dashboardEntry.getParam1Val().equals("DAY")) {
            query.append(",Day(`DateEndQueue`) as timeField from tag WHERE `Campaign` = ? ");
            finalTime = "DAY";
        } else if (dashboardEntry.getParam1Val().equals("YEAR")) {
            query.append(",Year(`DateEndQueue`) as timeField from tag WHERE `Campaign` = ? ");
            finalTime = "YEAR";
        } else {
            query.append(",Week(`DateEndQueue`) as timeField from tag WHERE `Campaign` = ? ");
        }

        if (!dashboardEntry.getParam2Val().equals("DEFAULT")) {
            query.append("AND `DateEndQueue` > ? ");
            startPararm = true;
        }

        if (!dashboardEntry.getParam3Val().equals("DEFAULT")) {
            query.append("AND `DateEndQueue` < ? ");
            endParam = true;
        }

        if (finalTime.equals("DAY")) {
            query.append("GROUP BY timeField, week(`DateEndQueue`), month(`DateEndQueue`), year(`DateEndQueue`) ORDER BY id DESC LIMIT 15;");
        } else if (finalTime.equals("MONTH")) {
            query.append("GROUP BY timeField, year(`DateEndQueue`) ORDER BY id DESC LIMIT 15;");
        } else if (finalTime.equals("WEEK")) {
            query.append("GROUP BY timeField, year(`DateEndQueue`) ORDER BY id DESC LIMIT 15;");
        } else if (finalTime.equals("YEAR")) {
            query.append("GROUP BY timeField ORDER BY id DESC LIMIT 15;");
        }

        try {
            Connection connection = databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query.toString());
            int i = 1;
            preStat.setString(i++, dashboardEntry.getAssociateElement());

            if (startPararm) {
                preStat.setString(i++, dashboardEntry.getParam1Val());
            }

            if (endParam) {
                preStat.setString(i++, dashboardEntry.getParam2Val());
            }
            ResultSet rs = preStat.executeQuery();
            Integer index = 0;
            while (rs.next()) {
                index = response.size() / 3;
                response.put("KEY_" + index, dateFormat.format(new Date(rs.getTimestamp("DateEndQueue").getTime())));
                response.put("VALUE_2_" + index, rs.getInt("nbExe"));
                response.put("VALUE_1_" + index, finalTime + " : " + rs.getInt("timeField"));
            }
            connection.close();
        } catch (SQLException exception) {
            LOG.error("Catch exception during search campaign execution frequency : ", exception);
        }
        return response;
    }

}
