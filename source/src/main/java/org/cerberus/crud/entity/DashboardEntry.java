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

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author utilisateur
 */
public class DashboardEntry{


    private Integer idGroup;
    private String codeIndicator;
    private Map<String, Object> entryData;
    private String param1Val;
    private String param2Val;
    private String param3Val;
    private String param4Val;
    private String associateElement;

    public Integer getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Integer idGroup) {
        this.idGroup = idGroup;
    }

    public String getCodeIndicator() {
        return codeIndicator;
    }

    public void setCodeIndicator(String codeIndicator) {
        this.codeIndicator = codeIndicator;
    }

    public Map<String, Object> getEntryData() {
        return entryData;
    }

    public void setEntryData(Map<String, Object> entryData) {
        this.entryData = entryData;
    }

    public String getParam1Val() {
        return param1Val;
    }

    public void setParam1Val(String param1Val) {
        this.param1Val = param1Val;
    }

    public String getParam2Val() {
        return param2Val;
    }

    public void setParam2Val(String param2Val) {
        this.param2Val = param2Val;
    }

    public String getAssociateElement() {
        return associateElement;
    }

    public void setAssociateElement(String associateElement) {
        this.associateElement = associateElement;
    }

    public String getParam3Val() {
        return param3Val;
    }

    public void setParam3Val(String param3Val) {
        this.param3Val = param3Val;
    }

    public String getParam4Val() {
        return param4Val;
    }

    public void setParam4Val(String param4Val) {
        this.param4Val = param4Val;
    }
    
    
}
