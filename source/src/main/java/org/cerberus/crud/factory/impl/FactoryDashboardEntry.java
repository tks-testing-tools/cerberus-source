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

import java.util.Map;
import javax.annotation.Nullable;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.factory.IFactoryDashboardEntry;
import org.springframework.stereotype.Service;

/**
 *
 * @author utilisateur
 */
@Service
public class FactoryDashboardEntry implements IFactoryDashboardEntry {

    @Override
    public DashboardEntry create(String codeReportItem, @Nullable Map<String, Object> entryData, String paramFirst, String paramSecond) {
        DashboardEntry dashboardEntry = new DashboardEntry();
        dashboardEntry.setCodeReportItem(codeReportItem);
        if (entryData != null) {
            dashboardEntry.setEntryData(entryData);
        }
        dashboardEntry.setParamFirst(paramFirst);
        dashboardEntry.setParamSecond(paramSecond);
        return dashboardEntry;
    }
}
