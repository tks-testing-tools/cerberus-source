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
import org.cerberus.crud.dao.IDashboardGroupDAO;
import org.cerberus.crud.entity.Campaign;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroup;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.factory.IFactoryDashboardGroup;
import org.cerberus.crud.service.IApplicationService;
import org.cerberus.crud.service.ICampaignService;
import org.cerberus.crud.service.IDashboardEntryService;
import org.cerberus.crud.service.IDashboardGroupService;
import org.cerberus.dto.DashboardGroupDTO;
import org.cerberus.dto.DashboardTypeIndicatorDTO;
import org.cerberus.engine.entity.MessageEvent;
import org.cerberus.enums.DashboardIndicatorEnum;
import org.cerberus.enums.DashboardTypeIndicatorEnum;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.util.StringUtil;
import org.cerberus.util.answer.AnswerItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author utilisateur
 */
@Service
public class DashboardGroupService implements IDashboardGroupService {

    private static final Logger LOG = LogManager.getLogger(DashboardGroupService.class);

    @Autowired
    private IDashboardGroupDAO dashboardGroupEntriesDAO;

    @Autowired
    private IDashboardEntryService dashboardEntryService;

    @Autowired
    private IFactoryDashboardGroup factoryDashboardGroupEntries;

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private ICampaignService campaignService;

    @Override
    public Map<String, Object> readDashboard(User user) {

        LOG.debug("Read dashboard for user : ", user.getLogin());

        Map<String, Object> response = new HashMap();
        List<DashboardGroupDTO> dashboardGroupListDTO = new ArrayList();
        List<DashboardGroup> dashboardGroupList = new ArrayList();

        //Read all group for Dashboard
        dashboardGroupList = this.readByUser(user);

        //Get all dashboard entry for groups
        for (DashboardGroup grp : dashboardGroupList) {
            grp.setDashboardEntries(this.dashboardEntryService.readByGroupEntriesWithData(grp));
            dashboardGroupListDTO.add(this.dashboardGroupEntriesToDTO(grp));
        }
        response.put("DashboardGroup", dashboardGroupListDTO);

        //Control size of groups for send MESSAGE_EVENT empty or success
        if (dashboardGroupListDTO.isEmpty()) {
            MessageEvent event = new MessageEvent(MessageEventEnum.DASHBOARD_READ_GROUP_EMPTY);
            response.put("MessageEvent_Group", event);
        } else if (dashboardGroupListDTO.size() > 0) {
            MessageEvent event = new MessageEvent(MessageEventEnum.DASHBOARD_READ_GROUP_SUCCESS);
            response.put("MessageEvent_Group", event);
        }

        List<DashboardTypeIndicatorDTO> availableIndicator = new ArrayList();

        availableIndicator = DashboardTypeIndicatorEnum.getDashboardPossibility();
        response.put("Dashboard_Indicator_Availability", availableIndicator);

        if (availableIndicator.isEmpty()) {
            MessageEvent event = new MessageEvent(MessageEventEnum.DASHBOARD_READ_AVAILABILITY_FAILED);
            response.put("MessageEvent_availability", event);
        } else {
            MessageEvent event = new MessageEvent(MessageEventEnum.DASHBOARD_READ_AVAILABILITY_SUCCESS);
            response.put("MessageEvent_availability", event);
        }

        return response;
    }

    @Override
    public List<MessageEvent> updateDashboard(List<DashboardGroupDTO> dashboardGroupDTO, User user) {
        List<DashboardGroup> dashboardGroup = new ArrayList();
        List<MessageEvent> responseEvent = new ArrayList();

        MessageEvent checkerDashboard = this.checkDashboardIntegrity(dashboardGroupDTO);
        responseEvent.add(checkerDashboard);
        if (checkerDashboard.getCode() == 200) {
            //Convert from DTO to dashboardgroup
            for (DashboardGroupDTO grp : dashboardGroupDTO) {
                dashboardGroup.add(this.dashboardGroupFromDTO(grp, user));
            }

            //Clean user dashboard and put result in response
            responseEvent.add(this.cleanByUser(user));

            //For each group -> insert in database
            for (DashboardGroup it : dashboardGroup) {
                LOG.debug("DashboardGroup iterator : ", it.getAssociateElement());
                Integer idGroup = this.create(it);

                if (idGroup > 0) {
                    // For each entry of group -> insert in base
                    for (DashboardEntry entry : it.getDashboardEntries()) {
                        entry.setIdGroup(idGroup);
                        responseEvent.add(this.dashboardEntryService.create(entry));
                    }
                    //Event insert group success
                    MessageEvent event = new MessageEvent(MessageEventEnum.DASHBOARD_CREATE_GROUP_SUCCESS);
                    event.setDescription(event.getDescription().replace("%GROUP%", it.getAssociateElement()));
                    responseEvent.add(event);
                } else {
                    LOG.debug("Insert group failed");
                    //Event insert group failed
                    MessageEvent event = new MessageEvent(MessageEventEnum.DASHBOARD_CREATE_GROUP_FAILED);
                    event.setDescription(event.getDescription().replace("%GROUP%", it.getAssociateElement()));
                    responseEvent.add(event);
                }
            }
        }

        return responseEvent;
    }

    @Override
    public MessageEvent checkDashboardIntegrity(List<DashboardGroupDTO> listGroup) {
        MessageEvent response = new MessageEvent(MessageEventEnum.DASHBOARD_CHECKER_SUCCESS);
        LOG.info("Start checker for dashboard integrity");

        //Control no duplication group
        for (int i = 0; i < listGroup.size(); i++) {
            for (int j = 0; j < listGroup.size(); j++) {
                if (listGroup.get(i).getAssociateElement().equals(listGroup.get(j).getAssociateElement()) && i != j) {
                    LOG.error("Check dashboard update integrity failed cause duplicate group" + listGroup.get(i).getAssociateElement());
                    response = new MessageEvent(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                    response.setDescription(response.getDescription().replace("%CAUSE%", "duplicate group %GROUP%"));
                    response.setDescription(response.getDescription().replace("%GROUP%", listGroup.get(i).getAssociateElement()));
                    return response;
                }
            }
        }

        for (DashboardGroupDTO grp : listGroup) {

            //Verify type of group is correct
            if (!DashboardTypeIndicatorEnum.verifyType(grp.getType())) {
                LOG.error("Check dashboard update integrity failed cause type don't exist for group " + grp.getAssociateElement());
                response = new MessageEvent(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                response.setDescription(response.getDescription().replace("%CAUSE%", "suspicious type indicator %TYPE%"));
                response.setDescription(response.getDescription().replace("%TYPE%", grp.getType()));
                return response;
            }

            //If type is APPLICATION
            if (grp.getType().equals("APPLICATION")) {
                //Then verify application exist
                if (!this.applicationService.exist(grp.getAssociateElement())) {
                    // if not return failed message
                    LOG.error("Check dashboard update integrity failed cause application don't exist " + grp.getAssociateElement());
                    response = new MessageEvent(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                    response.setDescription(response.getDescription().replace("%CAUSE%", "undeclared application"));
                    return response;
                }
            }

            //If type is CAMPAIGN
            if (grp.getType().equals("CAMPAIGN")) {
                //Then i verify campaign associated existing
                AnswerItem<Campaign> campaignAns = this.campaignService.readByKey(grp.getAssociateElement());
                if (!campaignAns.isCodeEquals(MessageEventEnum.DATA_OPERATION_OK.getCode()) || campaignAns.getItem() == null) {
                    //if not return failed message
                    LOG.error("Check dashboard update integrity failed cause campaign don't exist " + grp.getAssociateElement());
                    response = new MessageEvent(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                    response.setDescription(response.getDescription().replace("%CAUSE%", "undeclared campaign %CAMPAIGN%"));
                    response.setDescription(response.getDescription().replace("%CAMPAIGN%", grp.getAssociateElement()));

                    return response;
                }
            }

            //Control there isn't duplicate indicator for group
            for (int i = 0; i < grp.getDashboardEntries().size(); i++) {
                for (int j = 0; j < grp.getDashboardEntries().size(); j++) {
                    if (grp.getDashboardEntries().get(i).getCodeIndicator().equals(grp.getDashboardEntries().get(j).getCodeIndicator()) && i != j) {
                        LOG.error("Check dashboard update integrity failed cause indicator duplicate" + grp.getDashboardEntries().get(i).getCodeIndicator());
                        response = new MessageEvent(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                        response.setDescription(response.getDescription().replace("%CAUSE%", "duplicate indicator %INDICATOR%"));
                        response.setDescription(response.getDescription().replace("%INDICATOR%", grp.getDashboardEntries().get(i).getCodeIndicator()));
                        return response;
                    }
                }
            }

            //Dashboard entry verif
            for (DashboardEntry entry : grp.getDashboardEntries()) {

                //Control entry existing in indicator enum
                if (!DashboardIndicatorEnum.verifyCode(entry.getCodeIndicator())) {
                    LOG.error("Check dashboard update integrity failed cause invalid indicator " + entry.getCodeIndicator());
                    response = new MessageEvent(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                    response.setDescription(response.getDescription().replace("%CAUSE%", "suspicious indicator %INDICATOR%"));
                    response.setDescription(response.getDescription().replace("%INDICATOR%", entry.getCodeIndicator()));
                    return response;
                }

                //Control entry indicator type and group type is equals 
                if (!DashboardIndicatorEnum.getTypeByIndicator(entry.getCodeIndicator()).equals(grp.getType())) {
                    LOG.error("Check dashboard update integrity failed cause inconsistent entry " + entry.getCodeIndicator());
                    response = new MessageEvent(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                    response.setDescription(response.getDescription().replace("%CAUSE%", "inconsistent entry %ENTRY% of type %TYPE% in group %GROUP% of type %GROUPTYPE% "));
                    response.setDescription(response.getDescription().replace("%ENTRY%", entry.getCodeIndicator()));
                    response.setDescription(response.getDescription().replace("%TYPE%", DashboardIndicatorEnum.getTypeByIndicator(entry.getCodeIndicator())));
                    response.setDescription(response.getDescription().replace("%GROUP%", grp.getAssociateElement()));
                    response.setDescription(response.getDescription().replace("%GROUPTYPE%", grp.getType()));
                    return response;
                }

                //Verify if param1 is not null or empty
                if (StringUtil.isNullOrEmpty(entry.getParam1Val())) {
                    LOG.error("Check dashboard update integrity failed cause param1 is null or empty for " + entry.getCodeIndicator());
                    response = new MessageEvent(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                    response.setDescription(response.getDescription().replace("%CAUSE%", "Param 1 is null or empty for %INDICATOR% in %GROUP%"));
                    response.setDescription(response.getDescription().replace("%INDICATOR%", entry.getCodeIndicator()));
                    response.setDescription(response.getDescription().replace("%GROUP%", grp.getAssociateElement()));

                    return response;
                }

                //Verify if param2 is not null or empty
                if (StringUtil.isNullOrEmpty(entry.getParam2Val())) {
                    LOG.error("Check dashboard update integrity failed cause param2 is null or empty for " + entry.getCodeIndicator());
                    response = new MessageEvent(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                    response.setDescription(response.getDescription().replace("%CAUSE%", "Param 2 is null or empty for %INDICATOR% in %GROUP%"));
                    response.setDescription(response.getDescription().replace("%INDICATOR%", entry.getCodeIndicator()));
                    response.setDescription(response.getDescription().replace("%GROUP%", grp.getAssociateElement()));

                    return response;
                }
            }
        }
        LOG.info("Checker approuved dashboard integrity");
        return response;
    }

    @Override
    public List<DashboardTypeIndicatorDTO> readDashboardPossibility() {
        return DashboardTypeIndicatorEnum.getDashboardPossibility();
    }

    @Override
    public DashboardGroupDTO dashboardGroupEntriesToDTO(DashboardGroup dashboardGroupEntries) {
        Integer id = dashboardGroupEntries.getId();
        List<DashboardEntry> dashboardEntries = dashboardGroupEntries.getDashboardEntries();
        Integer sort = dashboardGroupEntries.getSort();
        String type = dashboardGroupEntries.getType();
        String associateElement = dashboardGroupEntries.getAssociateElement();
        return new DashboardGroupDTO(id, dashboardEntries, sort, associateElement, type);
    }

    public DashboardGroup dashboardGroupFromDTO(DashboardGroupDTO dashboardGroupDTO, User user) {
        List<DashboardEntry> dashboardEntries = dashboardGroupDTO.getDashboardEntries();
        Integer sort = dashboardGroupDTO.getSort();
        String associateElement = dashboardGroupDTO.getAssociateElement();
        String type = dashboardGroupDTO.getType();
        return this.factoryDashboardGroupEntries.create(null, user, dashboardEntries, sort, associateElement, type);
    }

    @Override
    public Integer create(DashboardGroup dashboardGroup) {
        return dashboardGroupEntriesDAO.create(dashboardGroup);
    }

    @Override
    public MessageEvent cleanByUser(User user) {
        return dashboardGroupEntriesDAO.cleanByUser(user);
    }

    @Override
    public List<DashboardGroup> readByUser(User user) {
        return this.dashboardGroupEntriesDAO.readByUser(user);
    }
}
