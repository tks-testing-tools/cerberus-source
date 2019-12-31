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
public class DashboardGroupConfigDTO implements Serializable {

    private static final long serialVersionUID = 1350092881346723539L;

    private String title;
    private Integer sort;
    private String type;
    private boolean isActive;
    private boolean isSelect;
    private List<DashboardIndicatorConfigDTO> availabilityList;

    public DashboardGroupConfigDTO(String title, Integer sort, String type, List<DashboardIndicatorConfigDTO> availabilityList, boolean isActive, boolean isSelect) {
        this.title = title;
        this.sort = sort;
        this.type = type;
        this.availabilityList = availabilityList;
        this.isActive = isActive;
        this.isSelect = isSelect;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public List<DashboardIndicatorConfigDTO> getAvailabilityList() {
        return availabilityList;
    }

    public void setAvailabilityList(List<DashboardIndicatorConfigDTO> availabilityList) {
        this.availabilityList = availabilityList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
