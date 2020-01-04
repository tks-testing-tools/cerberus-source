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
import org.cerberus.crud.dao.IDashboardConfigDAO;
import org.cerberus.crud.entity.DashboardConfig;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.factory.IFactoryDashboardConfig;
import org.cerberus.crud.service.impl.DashboardConfigService;
import org.cerberus.database.DatabaseSpring;
import org.cerberus.dto.MessageEventSlimDTO;
import org.cerberus.enums.MessageEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author cDelage
 */
@Repository
public class DashboardConfigDAO implements IDashboardConfigDAO {

    @Autowired
    DatabaseSpring databaseSpring;

    @Autowired
    IFactoryDashboardConfig factoryDashboardConfig;

    private static final Logger LOG = LogManager.getLogger(DashboardConfigService.class);

    @Override
    public long create(DashboardConfig conf, User userCreated) {
        long response = 0;
        String query = "INSERT INTO `dashboardconfig`(`title`,`usr_id`,`UsrCreated`) VALUES (?,?,?);";

        try {
            Connection connection = databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int i = 1;
            preStat.setString(i++, conf.getTitle());
            preStat.setInt(i++, conf.getUser().getUserID());
            preStat.setString(i++, userCreated.getLogin());
            preStat.executeUpdate();
            ResultSet rs = preStat.getGeneratedKeys();
            if (rs.first()) {
                response = rs.getInt(1);
            }
            connection.close();
        } catch (SQLException exception) {
            LOG.error("Error during insert dashboard configuration in database, exception : ", exception);
        }
        return response;
    }

    @Override
    public MessageEventSlimDTO delete(String title) {
        MessageEventSlimDTO response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_DELETE_CONFIG_SUCCESS);
        String query = "DELETE FROM `dashboardconfig` WHERE `title`= ?;";
        try {
            Connection connection = databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query);
            int i = 1;
            preStat.setString(i++, title);
            preStat.executeUpdate();
        } catch (SQLException exception) {
            LOG.error("Unable to delete dashboardconfig, catch exception : ", exception);
            response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_DELETE_CONFIG_FAILED);
        }
        return response;
    }

    @Override
    public DashboardConfig read(String title, User user) {
        String query = "SELECT `id_config` FROM `dashboardconfig` WHERE `usr_id` = ? AND `title` = ?;";
        DashboardConfig conf = new DashboardConfig();
        try {
            Connection connection = databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query);
            int i = 1;
            preStat.setInt(i++, user.getUserID());
            preStat.setString(i++, title);
            ResultSet rs = preStat.executeQuery();

            if (rs.first()) {
                Integer id = rs.getInt("id_config");
                conf = factoryDashboardConfig.create(id, title, user);
            }

            connection.close();
        } catch (SQLException exception) {
            LOG.error("Failed to read Dashboard config, exception : ", exception);
        }
        return conf;
    }

    @Override
    public List<DashboardConfig> readConfigForUser(User user) {
        String query = "SELECT `id_config`,`title` FROM `dashboardconfig` WHERE `usr_id` = ?;";
        List<DashboardConfig> response = new ArrayList();

        try {
            Connection connection = databaseSpring.connect();
            PreparedStatement preStat = connection.prepareStatement(query);
            preStat.setInt(1, user.getUserID());
            ResultSet rs = preStat.executeQuery();
            while (rs.next()) {
                response.add(this.loadFromResultSet(rs, user));
            }
            connection.close();
        } catch (SQLException exception) {
            LOG.error("Exception during read configs for user, catch exception : ", exception);
        }
        return response;
    }

    @Override
    public DashboardConfig loadFromResultSet(ResultSet rs, User user) {
        DashboardConfig conf = new DashboardConfig();
        try {
            int idConfig = rs.getInt("id_config");
            String title = rs.getString("title");
            conf = factoryDashboardConfig.create(idConfig, title, user);
        } catch (SQLException exception) {
            LOG.error("Error during read config, catch exception : ", exception);
        }
        return conf;
    }
}
