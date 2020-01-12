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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.dao.ICampaignGroupDAO;
import org.cerberus.database.DatabaseSpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author cDelage
 */
@Repository
public class CampaignGroupDAO implements ICampaignGroupDAO {

    private static final Logger LOG = LogManager.getLogger(CampaignGroupDAO.class);

    @Autowired
    private DatabaseSpring databaseSpring;

    /**
     * Read all group existing for campaign
     *
     * @return
     */
    @Override
    public List<String> readGroupList() {
        List<String> response = new ArrayList();
        String query1 = "SELECT `Group1` FROM campaign GROUP BY `Group1`;";
        String query2 = "SELECT `Group2` FROM campaign GROUP BY `Group2`;";
        String query3 = "SELECT `Group3` FROM campaign GROUP BY `Group3`;";

        try {
            Connection connection = databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query1);

            //Search group 1
            ResultSet rs = preStat.executeQuery(query1);
            while (rs.next()) {
                response.add(loadFromResultSet(rs, "Group1"));
            }
            preStat = connection.prepareStatement(query2);
            rs = preStat.executeQuery(query2);

            //Search group 2
            while (rs.next()) {
                response.add(loadFromResultSet(rs, "Group2"));
            }
            preStat = connection.prepareStatement(query3);
            rs = preStat.executeQuery(query3);

            //Search group 3
            while (rs.next()) {
                response.add(loadFromResultSet(rs, "Group3"));
            }

            //Remove duplicates element
            response = response.stream().distinct().collect(Collectors.toList());

        } catch (SQLException exception) {
            LOG.error("Error during executing query to read campaign group list : ", exception);
        }
        return response;
    }

    /**
     * Load element from resultset.
     *
     * @param rs
     * @param targetObject element searched
     * @return
     */
    @Override
    public String loadFromResultSet(ResultSet rs, String targetObject) {
        try {
            return rs.getString(targetObject);
        } catch (SQLException exception) {
            LOG.error("Error during load from resultset : ", exception);
            return "ERROR";
        }
    }

    @Override
    public List<String> getAllCampaignByGroup(String group) {
        List<String> response = new ArrayList();
        String query = "SELECT `Campaign` FROM campaign WHERE `Group1`= ? OR `Group2`= ? OR `Group3`= ? GROUP BY `Campaign`;";
        try {
            Connection connection = databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query);
            int i = 1;
            preStat.setString(i++, group);
            preStat.setString(i++, group);
            preStat.setString(i++, group);
            ResultSet rs = preStat.executeQuery();
            while (rs.next()) {
                response.add(loadFromResultSet(rs, "Campaign"));
            }
            connection.close();
        } catch (SQLException exception) {
            LOG.error("Catch exception during get campaign by group : ", exception);
        }
        return response;
    }

    @Override
    public boolean isExistingGroup(String group) {
        boolean response = false;
        String query = "SELECT Campaign` FROM campaign WHERE `Group1`= ? OR `Group2`= ? OR `Group3`= ? GROUP BY `Campaign`;";

        try {
            Connection connection = databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query);
            int i = 1;
            preStat.setString(i++, group);
            preStat.setString(i++, group);
            preStat.setString(i++, group);

            ResultSet rs = preStat.executeQuery();

            if (rs.first()) {
                response = true;
            }
        } catch (SQLException exception) {
            LOG.error("Catch exception during search if group existing : ", exception);
        }
        
        return response;
    }
}
