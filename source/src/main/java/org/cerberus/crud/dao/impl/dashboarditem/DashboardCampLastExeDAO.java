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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
public class DashboardCampLastExeDAO implements IDashboardEntryDataDAO {

    @Autowired
    private DatabaseSpring databaseSpring;

    private static final Logger LOG = LogManager.getLogger(DashboardCampLastExeDAO.class);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    private SimpleDateFormat durationFormat = new SimpleDateFormat("HH:mm:ss");

    @Override
    public Map<String, Object> readDataForDashboardEntry(DashboardEntry dashboardEntry) {
        Map<String, Object> response = new HashMap();
        try {
            String queryTag = "SELECT `Tag`, `DateEndQueue`, `UsrCreated` FROM tag WHERE Campaign = ? ORDER BY id DESC LIMIT 1;";
            String queryFirstExecutionForTag = "SELECT `Start`FROM testcaseexecution WHERE tag = ? LIMIT 1;";

            Connection connection = this.databaseSpring.connect();

            PreparedStatement preStat = connection.prepareStatement(queryTag);
            preStat.setString(1, dashboardEntry.getAssociateElement());
            ResultSet rs = preStat.executeQuery();

            if (rs.first()) {
                Timestamp end = rs.getTimestamp("DateEndQueue");
                String tag = rs.getString("Tag");
                
                response.put("Tag", tag);
                response.put("End", dateFormat.format(new Date(end.getTime())).toString());
                response.put("Launch by", rs.getString("UsrCreated"));
                
                preStat = connection.prepareStatement(queryFirstExecutionForTag);
                preStat.setString(1, tag);
                ResultSet rsExe = preStat.executeQuery();
                if (rsExe.first()) {
                    Timestamp start = rsExe.getTimestamp("Start");
                    response.put("Start", this.dateFormat.format(new Date(start.getTime())));
                    if (end != null && start != null) {
                            Long duration = end.getTime() - start.getTime();
                            //Format the calcul duration and substract 1 hour cause Java add automaticly 1 hour in duration...
                            response.put("Duration", this.durationFormat.format(new Date(duration - TimeUnit.HOURS.toMillis(1))));
                    }
                }

            } else {
                LOG.info("No tag found for campaign " + dashboardEntry.getAssociateElement());
                response.put("Tag_Error", "No tag found for Campaign " + dashboardEntry.getAssociateElement());
            }
            connection.close();
        } catch (SQLException e) {
            LOG.error("ERROR during read last execution detail : ", e);
        }
        return response;
    }
}
