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

/**
 *
 * @author cDelage
 */
public class DashboardIndicatorConfigDTO implements Serializable {

    private static final long serialVersionUID = 1350092881346723537L;

    private String codeIndicator;
    private String titleIndicator;
    private String param1Title;
    private String param1Type;
    private String param1Value;
    private String param2Title;
    private String param2Type;
    private String param2Value;
    private String param3Title;
    private String param3Type;
    private String param3Value;
    private String param4Title;
    private String param4Type;
    private String param4Value;
    private boolean isActive;
    private boolean isSelect;
    private String group;

    public DashboardIndicatorConfigDTO(String codeIndicator, String titleIndicator, String param1Title, String param1Type, String param1Value, String param2Title, String param2Type, String param2Value, String param3Title, String param3Type, String param3Value, String param4Title, String param4Type, String param4Value, boolean isActive, boolean isSelect, String group) {
        this.codeIndicator = codeIndicator;
        this.titleIndicator = titleIndicator;
        this.param1Title = param1Title;
        this.param1Type = param1Type;
        this.param1Value = param1Value;
        this.param2Title = param2Title;
        this.param2Type = param2Type;
        this.param2Value = param2Value;
        this.param3Title = param3Title;
        this.param3Type = param3Type;
        this.param3Value = param3Value;
        this.param4Title = param4Title;
        this.param4Type = param4Type;
        this.param4Value = param4Value;
        this.isActive = isActive;
        this.isSelect = isSelect;
        this.group = group;
    }

    public DashboardIndicatorConfigDTO(String codeIndicator, String param1Value, String param2Value, String param3Value, String param4Value) {
        this.codeIndicator = codeIndicator;
        this.param1Value = param1Value;
        this.param2Value = param2Value;
        this.param3Value = param3Value;
        this.param4Value = param4Value;
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

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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

    public String getParam1Value() {
        return param1Value;
    }

    public void setParam1Value(String param1Value) {
        this.param1Value = param1Value;
    }

    public String getParam2Value() {
        return param2Value;
    }

    public void setParam2Value(String param2Value) {
        this.param2Value = param2Value;
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

    public String getParam3Value() {
        return param3Value;
    }

    public void setParam3Value(String param3Value) {
        this.param3Value = param3Value;
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

    public String getParam4Value() {
        return param4Value;
    }

    public void setParam4Value(String param4Value) {
        this.param4Value = param4Value;
    }

    public boolean isIsSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
