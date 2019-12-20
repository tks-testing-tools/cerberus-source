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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.dto.DashboardIndicatorDTO;

/**
 *
 * @author utilisateur
 */
public enum DashboardIndicatorEnum {

    NOT_VALID("NOT_VALID", "Not valid report item", "DISABLE", "", "DISABLE", "", DashboardTypeIndicatorEnum.NOT_VALID),
    CAMPAIGN_EVOLUTION("CAMPAIGN_EVOLUTION", "campaign evolution", "Start date", "DATE", "End date", "DATE", DashboardTypeIndicatorEnum.CAMPAIGN),
    CAMPAIGN_LAST_REPORT("CAMPAIGN_LAST_REPORT_DETAIL", "Last report detail campaign", "DISABLE", "", "DISABLE", "", DashboardTypeIndicatorEnum.CAMPAIGN);

    private String codeIndicator;
    private String titleIndicator;
    private String param1Title;
    private String param1Type;
    private String param2Title;
    private String param2Type;
    private DashboardTypeIndicatorEnum type;

    private static final Logger LOG = LogManager.getLogger(DashboardIndicatorEnum.class);

    private DashboardIndicatorEnum(String codeIndicator, String titleIndicator, String param1Title, String param1Type, String param2Title, String param2Type, DashboardTypeIndicatorEnum type) {
        this.codeIndicator = codeIndicator;
        this.titleIndicator = titleIndicator;
        this.param1Title = param1Title;
        this.param1Type = param1Type;
        this.param2Title = param2Title;
        this.param2Type = param2Type;
        this.type = type;
    }

    public static List<DashboardIndicatorDTO> getIndicatorByType(String type) {
        List<DashboardIndicatorDTO> response = new ArrayList();
        LOG.debug("get indicators for type : ", type);
        try {
            for (DashboardIndicatorEnum it : values()) {
                LOG.debug(it.getCodeIndicator());
                if (it.getType().getTypeIndicator().equals(type)) {
                    LOG.debug("ADD TO LIST");
                    response.add(new DashboardIndicatorDTO(it.getCodeIndicator(), it.getParam1Title(), it.getParam1Type(), it.getParam2Title(), it.getParam2Type()));
                }
            }
        }catch(Exception exception){
            LOG.error("Catch exception during read type : ", exception);
        }
        return response;
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

}
