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

    NOT_VALID(0, "INVALID_TYPE_REPORT_ITEM", "invalid Report item"),
    CAMPAING(1, "CAMPAIGN", "Report-Item associés aux campagnes"),
    CAMPAING_GROUP(2, "CAMPAIGN_GROUP", "Report-Item associés aux campagnes"),
    APPLICATION(3, "APPLICATION", "Report-Item associés aux campagnes"),
    GENERIC(4, "GENERIC", "Report-Item associés aux campagnes"),
    ENVIRONMENT(5, "ENVIRONMENT", "Report-Item associés aux campagnes");

    private int idTypeReportItem;
    private String codeReportItem;
    private String descReportItem;

    private DashboardTypeReportItemEnum(int idTypeReportItem, String codeReportItem, String descReportItem) {
        this.idTypeReportItem = idTypeReportItem;
        this.codeReportItem = codeReportItem;
        this.descReportItem = descReportItem;
    }

    public static int getTypeReportItemID(String codeReportItem) {
        for (DashboardTypeReportItemEnum en : values()) {
            if (en.getCodeReportItem().compareTo(codeReportItem) == 0) {
                return en.getIdTypeReportItem();
            }
        }
        return NOT_VALID.getIdTypeReportItem();
    }

    public static String getTypeReportName(int typeReportItemId) {
        for (DashboardTypeReportItemEnum en : values()) {
            if (en.getIdTypeReportItem()== typeReportItemId) {
                return en.getCodeReportItem();
            }
        }
        return NOT_VALID.getCodeReportItem();
    }
    
    public static String getTypeReportDescById(int typeReportItemId) {
        for (DashboardTypeReportItemEnum en : values()) {
            if (en.getIdTypeReportItem()== typeReportItemId) {
                return en.getDescReportItem();
            }
        }
        return NOT_VALID.getDescReportItem();
    }
    
        public static String getTypeReportItemByName(String codeReportItem) {
        for (DashboardTypeReportItemEnum en : values()) {
            if (en.getCodeReportItem().compareTo(codeReportItem) == 0) {
                return en.getDescReportItem();
            }
        }
        return NOT_VALID.getDescReportItem();
    }
    
    public int getIdTypeReportItem() {
        return idTypeReportItem;
    }

    public void setIdTypeReportItem(int idTypeReportItem) {
        this.idTypeReportItem = idTypeReportItem;
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
