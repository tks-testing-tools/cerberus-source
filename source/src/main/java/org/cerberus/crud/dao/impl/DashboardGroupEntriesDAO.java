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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.dao.IDashboardGroupEntriesDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroupEntries;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.factory.IFactoryDashboardEntry;
import org.cerberus.crud.factory.IFactoryDashboardGroupEntries;
import org.cerberus.crud.service.IUserService;
import org.cerberus.database.DatabaseSpring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author utilisateur
 */
@Repository
public class DashboardGroupEntriesDAO implements IDashboardGroupEntriesDAO {

    private static final Logger LOG = LogManager.getLogger(DashboardGroupEntriesDAO.class);

    @Autowired
    private DatabaseSpring databaseSpring;

    @Autowired
    private IFactoryDashboardGroupEntries factoryDashboardGroupEntries;

    @Autowired
    private IUserService userService;

    @Override
    public List<DashboardGroupEntries> readByUser(User user) {
        LOG.debug("DASHBOARD GROUP ENTRIES readByUser DAO");
        List<DashboardGroupEntries> response = new ArrayList();
        StringBuilder query = new StringBuilder();
        query.append("SELECT `idGroupEntries`,`codeGroupeEntries`,`sort`,`dashboardUserId`"
                + "FROM `dashboardGroupEntries`"
                + "WHERE `dashboardUserId`= ? ;");

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
    public DashboardGroupEntries loadFromResultSet(ResultSet rs, User user) throws SQLException {
        DashboardGroupEntries response = new DashboardGroupEntries();
        Integer id = rs.getInt("idGroupEntries");
        String codeGroupEntries = rs.getString("codeGroupeEntries");
        Integer sort = rs.getInt("sort");
        return factoryDashboardGroupEntries.create(id, codeGroupEntries, user, null, sort.toString());
    }
}
