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

/**
 *
 * @author utilisateur
 */
public enum DashboardTypeReportItemEnum {

    NOT_VALID("INVALID_TYPE_REPORT_ITEM", "invalid Report item"),
    CAMPAIGN("CAMPAIGN", "Report-Item associés aux campagnes"),
    CAMPAING_GROUP("CAMPAIGN_GROUP", "Report-Item associés aux campagnes"),
    APPLICATION("APPLICATION", "Report-Item associés aux campagnes"),
    GENERIC("GENERIC", "Report-Item associés aux campagnes"),
    ENVIRONMENT("ENVIRONMENT", "Report-Item associés aux campagnes");

    private String codeReportItem;
    private String descReportItem;

    private DashboardTypeReportItemEnum( String codeReportItem, String descReportItem) {
        this.codeReportItem = codeReportItem;
        this.descReportItem = descReportItem;
    }
    
    
        public static String getTypeReportItemByName(String codeReportItem) {
        for (DashboardTypeReportItemEnum en : values()) {
            if (en.getCodeReportItem().compareTo(codeReportItem) == 0) {
                return en.getDescReportItem();
            }
        }
        return NOT_VALID.getDescReportItem();
    }

    public String getCodeReportItem() {
        return codeReportItem;
    }

    public void setCodeReportItem(String codeReportItem) {
        this.codeReportItem = codeReportItem;
    }

    public String getDescReportItem() {
        return descReportItem;
    }

    public void setDescReportItem(String descReportItem) {
        this.descReportItem = descReportItem;
    }
}
