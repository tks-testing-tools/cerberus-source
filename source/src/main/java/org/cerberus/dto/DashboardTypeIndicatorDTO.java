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

import java.util.List;
import javax.annotation.Nullable;

/**
 *
 * @author utilisateur
 */


public class DashboardTypeIndicatorDTO {
    
    private String typeIndicator;
    private List<DashboardIndicatorDTO> indicatorList;

    public DashboardTypeIndicatorDTO(String typeReportItem,@Nullable List<DashboardIndicatorDTO> reportItemList) {
        this.typeIndicator = typeReportItem;
        this.indicatorList = reportItemList;
    }

    public String getTypeIndicator() {
        return typeIndicator;
    }

    public void setTypeIndicator(String typeIndicator) {
        this.typeIndicator = typeIndicator;
    }

    public List<DashboardIndicatorDTO> getIndicatorList() {
        return indicatorList;
    }

    public void setIndicatorList(List<DashboardIndicatorDTO> indicatorList) {
        this.indicatorList = indicatorList;
    }

    
}
