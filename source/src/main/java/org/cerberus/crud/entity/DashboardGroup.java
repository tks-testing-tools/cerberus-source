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
package org.cerberus.crud.entity;

import java.util.List;

/**
 *
 * @author utilisateur
 */
public class DashboardGroup {

    private long id;
    private List<DashboardEntry> dashboardEntries;
    private long idConfig;
    private Integer sort;
    private String type;
    private String associateElement;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(long idConfig) {
        this.idConfig = idConfig;
    }

    public List<DashboardEntry> getDashboardEntries() {
        return dashboardEntries;
    }

    public void setDashboardEntries(List<DashboardEntry> dashboardEntries) {
        this.dashboardEntries = dashboardEntries;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getAssociateElement() {
        return associateElement;
    }

    public void setAssociateElement(String associateElement) {
        this.associateElement = associateElement;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
