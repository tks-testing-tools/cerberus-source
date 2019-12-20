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

/**
 *
 * @author utilisateur
 */
public class DashboardIndicatorDTO {

    private String codeIndicator;
    private String param1Title;
    private String param1Type;
    private String param2Title;
    private String param2Type;

    public DashboardIndicatorDTO(String reportItemCode, String param1, String param1Type, String param2, String param2Type) {
        this.codeIndicator = reportItemCode;
        this.param1Title = param1;
        this.param1Type = param1Type;
        this.param2Title = param2;
        this.param2Type = param2Type;
    }

    public String getCodeIndicator() {
        return codeIndicator;
    }

    public void setCodeIndicator(String codeIndicator) {
        this.codeIndicator = codeIndicator;
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

}
