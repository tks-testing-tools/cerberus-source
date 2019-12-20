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
package org.cerberus.enums;

import java.util.ArrayList;
import java.util.List;
import org.cerberus.dto.DashboardTypeIndicatorDTO;

/**
 *
 * @author utilisateur
 */
public enum DashboardTypeIndicatorEnum {

    NOT_VALID("INVALID_TYPE_REPORT_ITEM", "invalid indicator"),
    CAMPAIGN("CAMPAIGN", "indicator associate to campaign"),
    CAMPAING_GROUP("CAMPAIGN_GROUP", "indicator associate to campaign group"),
    APPLICATION("APPLICATION", "indicator associate to application"),
    GENERIC("GENERIC", "indicator associate to instance of cerberus"),
    ENVIRONMENT("ENVIRONMENT", "indicator associate to campaign");

    private String typeIndicator;
    private String descTypeIndicator;

    private DashboardTypeIndicatorEnum(String typeIndicator, String descTypeIndicator) {
        this.typeIndicator = typeIndicator;
        this.descTypeIndicator = descTypeIndicator;
    }

    public static List<DashboardTypeIndicatorDTO> getDashboardPossibility() {
        List<DashboardTypeIndicatorDTO> response = new ArrayList();
            for (DashboardTypeIndicatorEnum it : values()) {
                if (!it.getTypeIndicator().equals("INVALID_TYPE_REPORT_ITEM")) {
                    response.add(new DashboardTypeIndicatorDTO(it.getTypeIndicator(), DashboardIndicatorEnum.getIndicatorByType(it.getDescTypeIndicator())));
                }
            }
        return response;
    }

    public String getTypeIndicator() {
        return typeIndicator;
    }

    public void setTypeIndicator(String typeIndicator) {
        this.typeIndicator = typeIndicator;
    }

    public String getDescTypeIndicator() {
        return descTypeIndicator;
    }

    public void setDescTypeIndicator(String descTypeIndicator) {
        this.descTypeIndicator = descTypeIndicator;
    }

}
