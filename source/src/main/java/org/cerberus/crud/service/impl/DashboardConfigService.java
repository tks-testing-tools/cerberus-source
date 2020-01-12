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
import org.cerberus.crud.dao.IDashboardConfigDAO;
import org.cerberus.crud.entity.Application;
import org.cerberus.crud.entity.Campaign;
import org.cerberus.crud.entity.DashboardConfig;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroup;
import org.cerberus.crud.entity.User;
import org.cerberus.crud.factory.IFactoryDashboardConfig;
import org.cerberus.crud.factory.IFactoryDashboardEntry;
import org.cerberus.crud.factory.IFactoryDashboardGroup;
import org.cerberus.crud.service.IApplicationService;
import org.cerberus.crud.service.ICampaignGroupService;
import org.cerberus.crud.service.ICampaignService;
import org.cerberus.crud.service.IDashboardConfigService;
import org.cerberus.crud.service.IDashboardGroupService;
import org.cerberus.dto.DashboardGroupConfigDTO;
import org.cerberus.dto.DashboardGroupDTO;
import org.cerberus.dto.DashboardIndicatorConfigDTO;
import org.cerberus.dto.DashboardTypeConfigDTO;
import org.cerberus.dto.MessageEventSlimDTO;
import org.cerberus.enums.DashboardIndicatorEnum;
import org.cerberus.enums.DashboardTypeIndicatorEnum;
import org.cerberus.enums.MessageEventEnum;
import org.cerberus.util.StringUtil;
import org.cerberus.util.answer.AnswerItem;
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

    @Autowired
    private IDashboardGroupService dashboardGroupService;

    @Autowired
    private IFactoryDashboardEntry factoryDashboardEntry;

    @Autowired
    private IFactoryDashboardGroup factoryDashboardGroup;

    @Autowired
    private IFactoryDashboardConfig factoryDashboardConfig;

    @Autowired
    private IDashboardConfigDAO dashboardConfigDAO;

    @Autowired
    private ICampaignGroupService campaignGroupService;

    private static final Logger LOG = LogManager.getLogger(DashboardConfigService.class);

    @Override
    public Map<String, Object> readDashboard(User user, String title) {
        Map<String, Object> response = new HashMap();
        List<DashboardGroup> content = new ArrayList();
        List<DashboardGroupDTO> contentDTO = new ArrayList();
        List<MessageEventSlimDTO> msgList = new ArrayList();
        List<String> availableConfig = new ArrayList();
        List<DashboardConfig> configList = new ArrayList();

        try {
            DashboardConfig conf = read(title, user);
            content = dashboardGroupService.readDashboardContent(conf);
            conf.setGroupList(content);

            //Control size of groups for send MESSAGE_EVENT empty or success
            if (content.isEmpty()) {
                msgList.add(new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_READ_GROUP_EMPTY));
            } else if (content.size() > 0) {
                MessageEventSlimDTO msg = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_READ_GROUP_SUCCESS);
                msg.setDescription(msg.getDescription().replace("%CONFIG%", title));
                msgList.add(msg);
            }

            for (DashboardGroup it : content) {
                contentDTO.add(dashboardGroupService.dashboardGroupToDTO(it));
            }

            //Add Dashboard config statement (All type/group/entry active for config)
            Map<String, Object> dashboardStatement = new HashMap();
            dashboardStatement.put("Config", readStatement(conf));
            dashboardStatement.put("TitleConfig", title);
            dashboardStatement.put("SavedConfig", availableConfig);
            response.put("DashboardConfig", dashboardStatement);

            if (dashboardStatement.isEmpty()) {
                msgList.add(new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_READ_AVAILABILITY_FAILED));
            } else {
                msgList.add(new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_READ_AVAILABILITY_SUCCESS));
            }

            configList = readAllConfigsForUser(user);
            for (DashboardConfig it : configList) {
                availableConfig.add(it.getTitle());
            }

            if (availableConfig.size() > 0) {
                msgList.add(new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_READ_SAVED_CONFIG_SUCCESS));
            } else {
                msgList.add(new MessageEventSlimDTO((MessageEventEnum.DASHBOARD_READ_SAVED_CONFIG_EMPTY)));
            }

            response.put("MessageEvent", msgList);
            response.put("DashboardContent", contentDTO);

        } catch (Exception exception) {
            LOG.error("Error during read Dashboard : ", exception);
        }

        return response;
    }

    /**
     * Save a config for user.
     *
     * @param dashboardTypeDTO
     * @param titleConfig
     * @param user
     * @param usrCreated
     * @return
     */
    @Override
    public List<MessageEventSlimDTO> saveConfig(List<DashboardTypeConfigDTO> dashboardTypeDTO, String titleConfig, User user, User usrCreated) {

        List<DashboardGroup> dashboardGroup = new ArrayList();
        List<MessageEventSlimDTO> response = new ArrayList();

        try {
            DashboardConfig conf = factoryDashboardConfig.create(0, titleConfig, user);

            //Convert conf object to groups and entries
            for (DashboardTypeConfigDTO type : dashboardTypeDTO) {
                for (DashboardGroupConfigDTO grp : type.getGroupList()) {
                    if (grp.isIsActive()) {
                        dashboardGroup.add(dashboardGroupFromConfigDTO(grp, conf.getIdConfig()));
                    }
                }
            }

            //Set grouplist to conf and control integrity
            conf.setGroupList(dashboardGroup);
            MessageEventSlimDTO msg = checkDashboardIntegrity(conf.getGroupList());
            response.add(msg);

            //If dashboard integrity is valid
            if (msg.getCode() == 200) {

                //Delete config with same name
                msg = delete(titleConfig, user);
                response.add(msg);

                //If delete is success
                if (msg.getCode() == 200) {

                    //Create new config in database and get id generated
                    long idConfig = create(conf, usrCreated);
                    conf.setIdConfig(idConfig);
                    response.addAll(this.dashboardGroupService.saveGroupList(conf));
                } else {
                    LOG.error("Error during delete, message : ", msg.getDescription());
                    delete(titleConfig, user);
                }

            } else if (msg.getCode() == 300) {
                msg = delete(titleConfig, user);
                msg.setDescription(msg.getDescription().replace("%CONFIG%", titleConfig));
                response.add(msg);
            } else {
                LOG.debug("Error during check dashboard config " + titleConfig);
            }
        } catch (Exception exception) {
            LOG.error("Exception catch during update config : ", exception);
        }
        return response;
    }

    /**
     * Switch current config to saved config for user.
     *
     * @param title
     * @param user
     * @return
     */
    @Override
    public List<MessageEventSlimDTO> switchConfig(String title, User user) {
        List<MessageEventSlimDTO> response = new ArrayList();
        LOG.debug("Switch config launched");
        try {
            if (isExistingConfig(title, user)) {
                LOG.debug("Config exist");
                //Read conf object
                DashboardConfig newConf = read(title, user);

                //Set group content to conf
                newConf.setGroupList(dashboardGroupService.readByIdConfig(newConf.getIdConfig()));

                //Read statement config
                List<DashboardTypeConfigDTO> newConfigStatement = readStatement(newConf);

                //Save new current conf
                response.addAll(saveConfig(newConfigStatement, "CURRENT", user, user));
                LOG.debug("End if exist");
            } else {
                MessageEventSlimDTO msg = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_NOT_EXISTING_CONFIG);
                msg.setDescription(msg.getDescription().replace("%CONFIG%", title));
                response.add(msg);
                LOG.error("Failed to switch config cause config not existing");
            }
        } catch (Exception exception) {
            LOG.error("Exception during switch Dashboard config : ", exception);
            MessageEventSlimDTO msg = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_SWITCH_CONFIG_FAILED);
            msg.setDescription(msg.getDescription().replace("%CAUSE%", exception.getLocalizedMessage()));
            response.add(msg);
        }

        return response;
    }

    /**
     * Read statement of Dashboard with available and active TYPE / GROUP /
     * INDICATOR for config.
     *
     * @param dashboardGroup
     * @return
     */
    @Override
    public List<DashboardTypeConfigDTO> readStatement(DashboardConfig conf) {

        List<DashboardTypeConfigDTO> typeList = DashboardTypeIndicatorEnum.getTypeList();

        try {

            // For each type set groups available and associate indicator available to group
            for (DashboardTypeConfigDTO type : typeList) {

                List<DashboardGroupConfigDTO> grpList = new ArrayList();
                List<DashboardIndicatorConfigDTO> indicatorList = new ArrayList();
                //Add associate indicator to grpList
                switch (type.getTypeIndicator()) {
                    case "CAMPAIGN":
                        AnswerList<Campaign> campaignList = campaignService.readByCriteria(0, 23000, null, null, null, null);
                        indicatorList = DashboardIndicatorEnum.getIndicatorByType("CAMPAIGN");
                        for (Campaign camp : campaignList.getDataList()) {
                            DashboardGroupConfigDTO grp = new DashboardGroupConfigDTO(camp.getCampaign(), 10, type.getTypeIndicator(), indicatorList, false, false);
                            for (DashboardIndicatorConfigDTO ind : grp.getAvailabilityList()) {
                                ind.setGroup(grp.getTitle());
                            }
                            grpList.add(grp);
                        }

                        break;
                    case "APPLICATION":
                        AnswerList<Application> appList = applicationService.readAll();
                        indicatorList = DashboardIndicatorEnum.getIndicatorByType("APPLICATION");
                        for (Application app : appList.getDataList()) {
                            grpList.add(new DashboardGroupConfigDTO(app.getApplication(), 10, type.getTypeIndicator(), indicatorList, false, false));
                        }
                        break;

                    case "CAMPAIGN_GROUP":
                        List<String> campGroupList = campaignGroupService.readGroupList();
                        indicatorList = DashboardIndicatorEnum.getIndicatorByType("CAMPAIGN_GROUP");
                        for (String it : campGroupList) {
                            if (StringUtil.isNullOrEmpty(it)) {
                                grpList.add(new DashboardGroupConfigDTO(it, 10, type.getTypeIndicator(), indicatorList, false, false));
                            }
                        }

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

            List<DashboardGroup> dashboardGroup = new ArrayList();

            if (conf.getGroupList() != null) {
                dashboardGroup = conf.getGroupList();
            }

            //This part is made to set the actual group entry active in typelist
            //Compare current dashboard group to config group
            for (DashboardTypeConfigDTO type : typeList) {
                for (DashboardGroupConfigDTO grp : type.getGroupList()) {
                    for (DashboardGroup it : dashboardGroup) {
                        //If group is already in Dashboard, active config group
                        if (it.getAssociateElement().equals(grp.getTitle()) && !it.getAssociateElement().isEmpty()) {
                            grp.setIsActive(true);
                            grp.setSort(it.getSort());
                            //Compare current dashboard entries to config entry 
                            if (it.getDashboardEntries() != null) {
                                for (DashboardEntry ent : it.getDashboardEntries()) {
                                    if (!StringUtil.isNullOrEmpty(ent.getCodeIndicator())) {
                                        for (DashboardIndicatorConfigDTO ind : grp.getAvailabilityList()) {
                                            //activate if entry is already in dashboard
                                            if (ent.getCodeIndicator().equals(ind.getCodeIndicator())) {
                                                ind.setIsActive(true);
                                                ind.setParam1Value(ent.getParam1Val());
                                                ind.setParam2Value(ent.getParam2Val());
                                                ind.setParam3Value(ent.getParam3Val());
                                                ind.setParam4Value(ent.getParam4Val());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            LOG.error("Error during read dashboard statement, exception : ", exception);
        }
        return typeList;
    }

    /**
     * Checker for dashboard integrity
     *
     * @param listGroup
     * @return
     */
    @Override
    public MessageEventSlimDTO checkDashboardIntegrity(List<DashboardGroup> listGroup) {
        MessageEventSlimDTO response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_SUCCESS);
        LOG.info("Start checker for dashboard integrity");

        if (listGroup.size() == 0) {
            LOG.debug("Dashboard sent is empty");
            response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_EMPTY);
            return response;
        }

        //Control no duplication group
        for (int i = 0; i < listGroup.size(); i++) {
            for (int j = 0; j < listGroup.size(); j++) {
                if (listGroup.get(i).getAssociateElement().equals(listGroup.get(j).getAssociateElement()) && i != j) {
                    LOG.error("Check dashboard update integrity failed cause duplicate group" + listGroup.get(i).getAssociateElement());
                    response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                    response.setDescription(response.getDescription().replace("%CAUSE%", "duplicate group %GROUP%"));
                    response.setDescription(response.getDescription().replace("%GROUP%", listGroup.get(i).getAssociateElement()));
                    return response;
                }
            }
        }

        for (DashboardGroup grp : listGroup) {

            //Verify type of group is correct
            if (!DashboardTypeIndicatorEnum.verifyType(grp.getType())) {
                LOG.error("Check dashboard update integrity failed cause type don't exist for group " + grp.getAssociateElement());
                response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
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
                    response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
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
                    response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
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
                        response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
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
                    response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                    response.setDescription(response.getDescription().replace("%CAUSE%", "suspicious indicator %INDICATOR%"));
                    response.setDescription(response.getDescription().replace("%INDICATOR%", entry.getCodeIndicator()));
                    return response;
                }

                //Control entry indicator type and group type is equals 
                if (!DashboardIndicatorEnum.getTypeByIndicator(entry.getCodeIndicator()).equals(grp.getType())) {
                    LOG.error("Check dashboard update integrity failed cause inconsistent entry " + entry.getCodeIndicator());
                    response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
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
                    response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
                    response.setDescription(response.getDescription().replace("%CAUSE%", "Param 1 is null or empty for %INDICATOR% in %GROUP%"));
                    response.setDescription(response.getDescription().replace("%INDICATOR%", entry.getCodeIndicator()));
                    response.setDescription(response.getDescription().replace("%GROUP%", grp.getAssociateElement()));

                    return response;
                }

                //Verify if param2 is not null or empty
                if (StringUtil.isNullOrEmpty(entry.getParam2Val())) {
                    LOG.error("Check dashboard update integrity failed cause param2 is null or empty for " + entry.getCodeIndicator());
                    response = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CHECKER_FAILED);
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

    /**
     * Give sent front config object to convert to group and entry object. Made
     * to use during update dashboard process
     *
     * @param dashboardGroupConfigDTO
     * @param user
     * @return
     */
    @Override
    public DashboardGroup dashboardGroupFromConfigDTO(DashboardGroupConfigDTO dashboardGroupConfigDTO, long idConfig) {
        List<DashboardEntry> dashboardEntries = this.convertEntryFromConfigDTO(dashboardGroupConfigDTO.getAvailabilityList());
        Integer sort = dashboardGroupConfigDTO.getSort();
        String associateElement = dashboardGroupConfigDTO.getTitle();
        String type = dashboardGroupConfigDTO.getType();
        return this.factoryDashboardGroup.create(0, idConfig, dashboardEntries, sort, associateElement, type);
    }

    /**
     * Convert entry object from config DTO object. Give indicator name and
     * param to create an entry. Is call when group is convert.
     *
     * @param dashboardIndicator
     * @return
     */
    @Override
    public List<DashboardEntry> convertEntryFromConfigDTO(List<DashboardIndicatorConfigDTO> dashboardIndicator) {
        List<DashboardEntry> response = new ArrayList();
        for (DashboardIndicatorConfigDTO ent : dashboardIndicator) {
            response.add(factoryDashboardEntry.create(0, ent.getCodeIndicator(), ent.getParam1Value(), ent.getParam2Value(), ent.getParam3Value(), ent.getParam4Value(), ""));
        }
        return response;
    }

    @Override
    public long create(DashboardConfig conf, User user) {
        return dashboardConfigDAO.create(conf, user);
    }

    @Override
    public List<DashboardConfig> readAllConfigsForUser(User user) {
        return dashboardConfigDAO.readAllConfigsForUser(user);
    }

    @Override
    public DashboardConfig read(String title, User user) {
        return dashboardConfigDAO.read(title, user);
    }

    @Override
    public MessageEventSlimDTO delete(String title, User user) {
        return dashboardConfigDAO.delete(title, user);
    }

    @Override
    public boolean isExistingConfig(String title, User user) {
        return dashboardConfigDAO.isExistingConfig(title, user);
    }
}
