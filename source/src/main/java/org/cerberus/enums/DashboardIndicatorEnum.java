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
import org.cerberus.dto.DashboardIndicatorConfigDTO;

/**
 * Enum to store dashboard indicator. 
 * Used in Dashboard configuration.
 * @author cDelage
 */
public enum DashboardIndicatorEnum {

    // Store code indicator, title, params and type. For params set DISABLE to title to if necessary.
    NOT_VALID("NOT_VALID", "Not valid report item", "DISABLE", "", "DISABLE", "", "DISABLE", "", "DISABLE", "", DashboardTypeIndicatorEnum.NOT_VALID),
    CAMPAIGN_EVOLUTION("CAMPAIGN_EVOLUTION", "Campaign evolution", "Start date", "DATE", "End date", "DATE", "Number of entry [2,10]", "Number", "DISABLE", "", DashboardTypeIndicatorEnum.CAMPAIGN),
    CAMPAIGN_LAST_REPORT("CAMPAIGN_LAST_EXE_DETAIL", "Last execution detail", "DISABLE", "", "DISABLE", "","DISABLE", "", "DISABLE", "", DashboardTypeIndicatorEnum.CAMPAIGN);


    private String codeIndicator;
    private String titleIndicator;
    private String param1Title;
    private String param1Type;
    private String param2Title;
    private String param2Type;
    private String param3Title;
    private String param3Type;
    private String param4Title;
    private String param4Type;
    private DashboardTypeIndicatorEnum type;

    private DashboardIndicatorEnum(String codeIndicator, String titleIndicator, String param1Title, String param1Type, String param2Title, String param2Type, String param3Title, String param3Type, String param4Title, String param4Type, DashboardTypeIndicatorEnum type) {
        this.codeIndicator = codeIndicator;
        this.titleIndicator = titleIndicator;
        this.param1Title = param1Title;
        this.param1Type = param1Type;
        this.param2Title = param2Title;
        this.param2Type = param2Type;
        this.param3Title = param3Title;
        this.param3Type = param3Type;
        this.param4Title = param4Title;
        this.param4Type = param4Type;
        this.type = type;
    }

    public static List<DashboardIndicatorConfigDTO> getIndicatorByType(String type) {
        List<DashboardIndicatorConfigDTO> response = new ArrayList();
        for (DashboardIndicatorEnum it : values()) {
            if (it.getType().getTypeIndicator().equals(type)) {
                response.add(new DashboardIndicatorConfigDTO(it.getCodeIndicator(), it.getTitleIndicator(), it.getParam1Title(), it.getParam1Type(), "DEFAULT", it.getParam2Title(), it.getParam2Type(), "DEFAULT", it.getParam3Title(), it.getParam3Type(), "DEFAULT", it.getParam4Title(), it.getParam4Type(), "DEFAULT", false, false, ""));
            }
        }
        return response;
    }

    public static String getTypeByIndicator(String indicator) {
        for (DashboardIndicatorEnum it : values()) {
            if (it.getCodeIndicator().equals(indicator)) {
                return it.getType().getTypeIndicator();
            }
        }
        return NOT_VALID.getType().getTypeIndicator();
    }

    public static boolean verifyCode(String codeIndicator) {
        for (DashboardIndicatorEnum it : values()) {
            if (it.getCodeIndicator().equals(codeIndicator)) {
                if (it.getCodeIndicator() != "NOT_VALID") {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getTitleByCodeIndicator(String codeIndicator) {
        for (DashboardIndicatorEnum it : values()) {
            if(it.getCodeIndicator().equals(codeIndicator)){
                return it.getTitleIndicator();
            }
        }
        return NOT_VALID.getTitleIndicator();
    }

    public String getCodeIndicator() {
        return codeIndicator;
    }

    public void setCodeIndicator(String codeIndicator) {
        this.codeIndicator = codeIndicator;
    }

    public String getTitleIndicator() {
        return titleIndicator;
    }

    public void setTitleIndicator(String titleIndicator) {
        this.titleIndicator = titleIndicator;
    }

    public String getParam1Title() {
        return param1Title;
    }

    public void setParam1Title(String param1Title) {
        this.param1Title = param1Title;
    }

    public String getParam1Type() {
        return param1Type;
    }

    public void setParam1Type(String param1Type) {
        this.param1Type = param1Type;
    }

    public String getParam2Title() {
        return param2Title;
    }

    public void setParam2Title(String param2Title) {
        this.param2Title = param2Title;
    }

    public String getParam2Type() {
        return param2Type;
    }

    public void setParam2Type(String param2Type) {
        this.param2Type = param2Type;
    }

    public DashboardTypeIndicatorEnum getType() {
        return type;
    }

    public void setType(DashboardTypeIndicatorEnum type) {
        this.type = type;
    }

    public String getParam3Title() {
        return param3Title;
    }

    public void setParam3Title(String param3Title) {
        this.param3Title = param3Title;
    }

    public String getParam3Type() {
        return param3Type;
    }

    public void setParam3Type(String param3Type) {
        this.param3Type = param3Type;
    }

    public String getParam4Title() {
        return param4Title;
    }

    public void setParam4Title(String param4Title) {
        this.param4Title = param4Title;
    }

    public String getParam4Type() {
        return param4Type;
    }

    public void setParam4Type(String param4Type) {
        this.param4Type = param4Type;
    }
}
