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
 * @author utilisateur
 */
@Repository
public class DashboardAppStatusDAO implements IDashboardEntryDataDAO {

    private static final Logger LOG = LogManager.getLogger(DashboardAppStatusDAO.class);

    @Autowired
    DatabaseSpring databaseSpring;
    
    public Map<String, Object> readDataForDashboardEntry(DashboardEntry dashboardEntry) {
        Map<String,Object> response = new HashMap();
        String query = "SELECT Count(`TestCase`) as nbTC, `Status` FROM `testcase` WHERE `Application` = ? GROUP BY Status;";
        try {
            Connection conncetion = databaseSpring.connect();
            PreparedStatement preStat = conncetion.prepareStatement(query);
            int i = 1;
            preStat.setString(i++, dashboardEntry.getAssociateElement());
            ResultSet rs = preStat.executeQuery();
            while(rs.next()){
                String status = rs.getString("Status");
                Integer nbTestCase = rs.getInt("nbTC");
                response.put(status, nbTestCase);
            }
        } catch (SQLException exception) {
            LOG.error("Catch exception during search test cases by status : ", exception);
        }
        return response;
    }
}
