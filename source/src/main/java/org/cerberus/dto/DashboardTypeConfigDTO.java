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
package org.cerberus.dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author cDelage
 */

public class DashboardTypeConfigDTO implements Serializable {

    private static final long serialVersionUID = 1350092881346723538L;

    private String typeIndicator;
    private List<DashboardGroupConfigDTO> groupList;

    public DashboardTypeConfigDTO(String typeIndicator, List<DashboardGroupConfigDTO> groupList) {
        this.typeIndicator = typeIndicator;
        this.groupList = groupList;
    }

    public String getTypeIndicator() {
        return typeIndicator;
    }

    public void setTypeIndicator(String typeIndicator) {
        this.typeIndicator = typeIndicator;
    }

    public List<DashboardGroupConfigDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<DashboardGroupConfigDTO> groupList) {
        this.groupList = groupList;
    }
}
