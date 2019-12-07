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
package org.cerberus.crud.factory.impl;

import java.util.List;
import javax.annotation.Nullable;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroupEntries;
import org.cerberus.crud.factory.IFactoryDashboardGroupEntries;
import org.springframework.stereotype.Service;

/**
 *
 * @author utilisateur
 */
@Service
public class FactoryDashboardGroupEntries implements IFactoryDashboardGroupEntries {
    
    @Override
    public DashboardGroupEntries create(String codeGroupEntries, int idUser, @Nullable List<DashboardEntry> dashboardEntries, int sort) {
        DashboardGroupEntries dashboardGroupEntries = new DashboardGroupEntries();
        dashboardGroupEntries.setCodeGroupEntries(codeGroupEntries);
        dashboardGroupEntries.setIdUser(idUser);
        if (dashboardEntries != null) {
            dashboardGroupEntries.setDashboardEntries(dashboardEntries);
        }
        dashboardGroupEntries.setSort(sort);
        return dashboardGroupEntries;
    }
}
