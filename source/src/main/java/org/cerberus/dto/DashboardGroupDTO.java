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
import javax.annotation.Nullable;
import org.cerberus.crud.entity.DashboardEntry;

/**
 *
 * @author cDelage
 */
public class DashboardGroupDTO implements Serializable{

    private  static  final  long serialVersionUID =  1350092881346723535L;
    
    private String associateElement;
    private String type;
    private List<DashboardEntryDTO> dashboardEntries;

    public DashboardGroupDTO(List<DashboardEntryDTO> dashboardEntries, String associateElement, String type) {
        this.dashboardEntries = dashboardEntries;
        this.associateElement = associateElement;
        this.type = type;
    }

    public List<DashboardEntryDTO> getDashboardEntries() {
        return dashboardEntries;
    }

    public void setDashboardEntries(List<DashboardEntryDTO> dashboardEntries) {
        this.dashboardEntries = dashboardEntries;
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
