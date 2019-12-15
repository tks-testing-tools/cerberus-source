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

    NOT_VALID("NOT_VALID", "Not valid report item", "DISABLE", "" , "DISABLE", "" , 0),
    CAMPAIGN_EVOLUTION("CAMPAIGN_EVOLUTION", "campaign evolution" , "Start date", "DATE", "End date", "DATE" , 1),
    CAMPAIGN_LAST_REPORT("CAMPAIGN_LAST_REPORT", "Last report detail campaign", "DISABLE", "", "DISABLE", "", 1);

    private String reportItemCode;
    private String reportItemTitle;
    private String param1;
    private String paramType1;
    private String param2;
    private String paramType2;
    private int reportItemType;

    private DashboardReportItemEnum(String reportItemCode, String reportItemTitle, String param1, String paramType1, String param2, String paramType2, int reportItemType) {
        this.reportItemCode = reportItemCode;
        this.reportItemTitle = reportItemTitle;
        this.param1 = param1;
        this.paramType1 = paramType1;
        this.param2 = param2;
        this.paramType2 = paramType2;
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

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParamType1() {
        return paramType1;
    }

    public void setParamType1(String paramType1) {
        this.paramType1 = paramType1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParamType2() {
        return paramType2;
    }

    public void setParamType2(String paramType2) {
        this.paramType2 = paramType2;
    }

    public int getReportItemType() {
        return reportItemType;
    }

    public void setReportItemType(int reportItemType) {
        this.reportItemType = reportItemType;
    }

    
}
