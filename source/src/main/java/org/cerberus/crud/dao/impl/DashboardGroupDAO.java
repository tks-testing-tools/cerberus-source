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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.dao.IDashboardGroupDAO;
import org.cerberus.crud.entity.DashboardGroup;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.factory.IFactoryDashboardGroup;
import org.cerberus.database.DatabaseSpring;
import org.cerberus.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author cDelage
 */
@Repository
public class DashboardGroupDAO implements IDashboardGroupDAO {

    private static final Logger LOG = LogManager.getLogger(DashboardGroupDAO.class);

    @Autowired
    private DatabaseSpring databaseSpring;

    @Autowired
    private IFactoryDashboardGroup factoryDashboardGroupEntries;

    @Override
    public List<DashboardGroup> readByUser(User user) {
        LOG.debug("DASHBOARD GROUP readByUser DAO");
        List<DashboardGroup> response = new ArrayList();
        StringBuilder query = new StringBuilder();
        query.append("SELECT `id_group`,`sort`,`usr_id`,`associate_element`, `type` "
                + "FROM `dashboardgroup`"
                + "WHERE `usr_id`= ? ;");

        try {
            Connection connection = this.databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query.toString());
            preStat.setInt(1, user.getUserID());
            ResultSet rs = preStat.executeQuery();
            while (rs.next()) {
                response.add(this.loadFromResultSet(rs, user));
            }
        } catch (SQLException exception) {
            LOG.error("Catch sql exception during Dashboard group entries read : ", exception);
        } catch (Exception exception) {
            LOG.error("Exception catch during read Dashboard group entries request execute : ", exception);
        }
        return response;
    }

    @Override
    public DashboardGroup loadFromResultSet(ResultSet rs, User user) throws SQLException {
        Integer id = rs.getInt("id_group");
        Integer sort = rs.getInt("sort");
        String type = rs.getString("type");
        String associateElement = rs.getString("associate_element");
        return factoryDashboardGroupEntries.create(id, user, null, sort.toString(), associateElement, type);
    }

    @Override
    public Integer create(DashboardGroup dashboardGroup) {
        Integer result = 0;
        final String query = "INSERT INTO `dashbordgroup`(`sort`, `usr_id`, `type`,`associate_element`) VALUES (?,?,?,?)";

        try {
            Connection connection = databaseSpring.connect();
            try {
                PreparedStatement preStat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                try {

                    preStat.setString(1, dashboardGroup.getSort());
                    preStat.setInt(2, dashboardGroup.getUser().getUserID());
                    preStat.setString(3, dashboardGroup.getType());
                    if (!StringUtil.isNullOrEmpty(dashboardGroup.getAssociateElement())) {
                        preStat.setString(4, dashboardGroup.getAssociateElement());
                    } else {
                        preStat.setString(4, "");
                    }
                    preStat.execute();
                    ResultSet resultSet = preStat.getGeneratedKeys();
                    if (resultSet.first()) {
                        result = resultSet.getInt(1);
                    }
                    resultSet.close();

                    LOG.debug("CREATE DASHBOARD GROUP ENTRIES ID : ", result);

                } catch (SQLException exception) {
                    LOG.error("Unable to execute query : " + exception.toString());
                } finally {
                    preStat.close();
                }
            } catch (SQLException exception) {
                LOG.error("Unable to execute query : " + exception.toString());
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (Exception exception) {
            LOG.error("Failed to connect to database, catched Exception : ", exception);
        }

        return result;
    }

    @Override
    public String cleanByUser(User user) {
        final String query = "DELETE FROM dashboardGroupEntries WHERE dashboardUserId = ?";
        String response = new String();
        try {
            Connection connection = databaseSpring.connect();
            try {
                PreparedStatement preStat = connection.prepareStatement(query);
                try {
                    preStat.setInt(1, user.getUserID());
                    preStat.execute();
                    response = "CLEAN SUCESSFULLY";
                } catch (SQLException exception) {
                    LOG.error("Unable to execute query : " + exception.toString());
                    response = "FAIL TO CLEAN DASHBOARD CAUSE " + exception.getLocalizedMessage();
                } finally {
                    preStat.close();
                }
            } catch (SQLException exception) {
                LOG.error("Unable to execute query : " + exception.toString());
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    LOG.warn(e.toString());
                }
            }
        } catch (Exception exception) {
            LOG.error("Failed to connect to database, catched Exception : ", exception);
        }
        return response;
    }
}
