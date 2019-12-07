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

 package org.cerberus.crud.entity;

 import java.util.Map;

 /**
 *
 * @author utilisateur
 */
public class DashboardEntry {

    private String codeReportItem;
    private Map<String, Object> entryData;
    private String paramFirst;
    private String paramSecond;

    public String getCodeReportItem() {
        return codeReportItem;
    }

    public void setCodeReportItem(String codeReportItem) {
        this.codeReportItem = codeReportItem;
    }

    public Map<String, Object> getEntryData() {
        return entryData;
    }

    public void setEntryData(Map<String, Object> entryData) {
        this.entryData = entryData;
    }

    public String getParamFirst() {
        return paramFirst;
    }

    public void setParamFirst(String paramFirst) {
        this.paramFirst = paramFirst;
    }

    public String getParamSecond() {
        return paramSecond;
    }

    public void setParamSecond(String paramSecond) {
        this.paramSecond = paramSecond;
    }

}
