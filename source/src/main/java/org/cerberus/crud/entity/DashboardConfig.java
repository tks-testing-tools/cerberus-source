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
 * @author cDelage
 */


public class DashboardConfig {
    
    private long idConfig;
    private String title;
    private List<DashboardGroup> groupList;
    private User user;

    public long getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(long idConfig) {
        this.idConfig = idConfig;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<DashboardGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<DashboardGroup> group_list) {
        this.groupList = group_list;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    } 
    
}
