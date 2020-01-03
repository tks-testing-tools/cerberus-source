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
package org.cerberus.crud.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.entity.Application;
import org.cerberus.crud.entity.Campaign;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroup;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.factory.IFactoryDashboardEntry;
import org.cerberus.crud.factory.IFactoryDashboardGroup;
import org.cerberus.crud.service.IApplicationService;
import org.cerberus.crud.service.ICampaignService;
import org.cerberus.crud.service.IDashboardConfigService;
import org.cerberus.dto.DashboardGroupConfigDTO;
import org.cerberus.dto.DashboardIndicatorConfigDTO;
import org.cerberus.dto.DashboardTypeConfigDTO;
import org.cerberus.enums.DashboardIndicatorEnum;
import org.cerberus.enums.DashboardTypeIndicatorEnum;
import org.cerberus.util.answer.AnswerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author cDelage
 */
@Service
public class DashboardConfigService implements IDashboardConfigService {

    @Autowired
    private ICampaignService campaignService;

    @Autowired
    private IApplicationService applicationService;
    
    private static final Logger LOG = LogManager.getLogger(DashboardConfigService.class);
    
    @Autowired
    private IFactoryDashboardEntry factoryDashboardEntry;
    
    @Autowired
    private IFactoryDashboardGroup factoryDashboardGroup;


    /**
     * Read statement of Dashboard with available and active TYPE / GROUP /
     * INDICATOR for config.
     *
     * @param dashboardGroup
     * @return
     */
    @Override
    public List<DashboardTypeConfigDTO> readStatement(List<DashboardGroup> dashboardGroup) {
        List<DashboardTypeConfigDTO> typeList = DashboardTypeIndicatorEnum.getTypeList();

        // For each type set groups available and associate indicator available to group
        for (DashboardTypeConfigDTO type : typeList) {

            List<DashboardGroupConfigDTO> grpList = new ArrayList();

            //Add associate indicator to grpList
            switch (type.getTypeIndicator()) {
                case "CAMPAIGN":
                    AnswerList<Campaign> campaignList = campaignService.readByCriteria(0, 23000, null, null, null, null);
                    for (Campaign camp : campaignList.getDataList()) {
                        DashboardGroupConfigDTO grp = new DashboardGroupConfigDTO(camp.getCampaign(), 10, type.getTypeIndicator(), DashboardIndicatorEnum.getIndicatorByType(type.getTypeIndicator()), false, false);
                        for(DashboardIndicatorConfigDTO ind : grp.getAvailabilityList()){
                            ind.setGroup(grp.getTitle());
                        }
                        grpList.add(grp);
                    }

                    break;
                case "APPLICATION":
                    AnswerList<Application> appList = applicationService.readAll();
                    for (Application app : appList.getDataList()) {
                        grpList.add(new DashboardGroupConfigDTO(app.getApplication(), 10, type.getTypeIndicator(), DashboardIndicatorEnum.getIndicatorByType(type.getTypeIndicator()), false, false));
                    }
                    break;

                case "CAMPAIGN_GROUP":
                    grpList.add(new DashboardGroupConfigDTO("GROUP", 10, type.getTypeIndicator(), DashboardIndicatorEnum.getIndicatorByType(type.getTypeIndicator()), false, false));
                    break;
                case "GENERIC":
                    grpList.add(new DashboardGroupConfigDTO("GENERIC", 10, type.getTypeIndicator(), DashboardIndicatorEnum.getIndicatorByType(type.getTypeIndicator()), false, false));
                    break;
                case "ENVIRONMENT":
                    grpList.add(new DashboardGroupConfigDTO("ENVIRONMENT", 10, type.getTypeIndicator(), DashboardIndicatorEnum.getIndicatorByType(type.getTypeIndicator()), false, false));
                    break;
            }
            type.setGroupList(grpList);
        }

        //Compare current dashboard group to config group
        for (DashboardTypeConfigDTO type : typeList) {
            for (DashboardGroupConfigDTO grp : type.getGroupList()) {
                for (DashboardGroup it : dashboardGroup) {
                    //If group is already in Dashboard, active config group
                    if (it.getAssociateElement().equals(grp.getTitle())) {
                        grp.setIsActive(true);

                        //Compare current dashboard entries to config entry 
                        for (DashboardEntry ent : it.getDashboardEntries()) {
                            for (DashboardIndicatorConfigDTO ind : grp.getAvailabilityList()) {

                                //activate if entry is already in dashboard
                                if (ent.getCodeIndicator().equals(ind.getCodeIndicator())) {
                                    ind.setIsActive(true);
                                    ind.setParam1Value(ent.getParam1Val());
                                    ind.setParam2Value(ent.getParam2Val());
                                }
                            }
                        }
                    }
                }
            }
        }
        return typeList;
    }

    /**
     * Give sent front config object to convert to group and entry object.
     * Made to use during update dashboard process
     * @param dashboardGroupConfigDTO
     * @param user
     * @return
     */
    @Override
    public DashboardGroup dashboardGroupFromConfigDTO(DashboardGroupConfigDTO dashboardGroupConfigDTO, User user) {
        LOG.debug("CONVERT CONFIG TO GROUP FOR GROUP " + dashboardGroupConfigDTO.getTitle());
        List<DashboardEntry> dashboardEntries = this.convertEntryFromConfigDTO(dashboardGroupConfigDTO.getAvailabilityList());
        Integer sort = dashboardGroupConfigDTO.getSort();
        String associateElement = dashboardGroupConfigDTO.getTitle();
        String type = dashboardGroupConfigDTO.getType();
        return this.factoryDashboardGroup.create(null, user, dashboardEntries, sort, associateElement, type);
    }
    
    /**
     * Convert entry object from config DTO object.
     * Give indicator name and param to create an entry.
     * Is call when group is convert.
     * 
     * @param dashboardIndicator
     * @return 
     */
    @Override 
    public List<DashboardEntry> convertEntryFromConfigDTO(List<DashboardIndicatorConfigDTO> dashboardIndicator){
        List<DashboardEntry> response = new ArrayList();
            for(DashboardIndicatorConfigDTO ent : dashboardIndicator){
                response.add(factoryDashboardEntry.create(0, ent.getCodeIndicator(), ent.getParam1Value(), ent.getParam2Value(), ent.getParam3Value(), ent.getParam4Value(), ""));
            }
        return response;
    }
}
