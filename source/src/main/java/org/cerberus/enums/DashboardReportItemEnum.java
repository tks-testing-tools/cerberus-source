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


public enum DashboardReportItemEnum {
    NOT_VALID("NOT_VALID","Not valid report item","N",0),
    CAMPAIGN_EVOLUTION("CAMPAIGN_EVOLUTION","Campaign evolution","Y",1);
    
    private String reportItemCode;
    private String reportItemTitle;
    private String isConfigurable;
    private int reportItemType;

    private DashboardReportItemEnum(String reportItemCode, String reportItemTitle, String isConfigurable, int reportItemType) {
        this.reportItemCode = reportItemCode;
        this.reportItemTitle = reportItemTitle;
        this.isConfigurable = isConfigurable;
        this.reportItemType = reportItemType;
    }

    public String getReportItemCode() {
        return reportItemCode;
    }

    public void setReportItemCode(String reportItemCode) {
        this.reportItemCode = reportItemCode;
    }

    public String getReportItemTitle() {
        return reportItemTitle;
    }

    public void setReportItemTitle(String reportItemTitle) {
        this.reportItemTitle = reportItemTitle;
    }

    public String getIsConfigurable() {
        return isConfigurable;
    }

    public void setIsConfigurable(String isConfigurable) {
        this.isConfigurable = isConfigurable;
    }

    public int getReportItemType() {
        return reportItemType;
    }

    public void setReportItemType(int reportItemType) {
        this.reportItemType = reportItemType;
    }
    
    
}
