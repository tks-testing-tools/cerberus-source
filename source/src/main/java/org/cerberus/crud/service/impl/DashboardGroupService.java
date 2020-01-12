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
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cerberus.crud.dao.IDashboardGroupDAO;
import org.cerberus.crud.entity.DashboardConfig;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroup;
import org.cerberus.crud.service.IDashboardConfigService;
import org.cerberus.crud.service.IDashboardEntryService;
import org.cerberus.crud.service.IDashboardGroupService;
import org.cerberus.dto.DashboardEntryDTO;
import org.cerberus.dto.DashboardGroupDTO;
import org.cerberus.dto.MessageEventSlimDTO;
import org.cerberus.enums.MessageEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Dashboard group service is made to handle DashboardContent (read and update) and call DashboardConfigService
 * @author cDelage
 */
@Service
public class DashboardGroupService implements IDashboardGroupService {

    private static final Logger LOG = LogManager.getLogger(DashboardGroupService.class);

    @Autowired
    private IDashboardGroupDAO dashboardGroupEntriesDAO;

    @Autowired
    private IDashboardEntryService dashboardEntryService;

    /**
     * Read dashboard statement and content for user
     *
     * @param user
     * @return
     */
    @Override
    public List<DashboardGroup> readDashboardContent(DashboardConfig dashboardConfig) {

        List<DashboardGroup> dashboardGroupList = new ArrayList();

        //Read all group for Dashboard
        dashboardGroupList = this.readByIdConfig(dashboardConfig.getIdConfig());

        //Get all dashboard entry for groups
        for (DashboardGroup grp : dashboardGroupList) {
            grp.setDashboardEntries(this.dashboardEntryService.readByGroupEntriesWithData(grp));
        }
        
        return dashboardGroupList;
    }

    /**
     * Save group of Dashboard
     *
     * @param dashboardGroupDTO
     * @param user
     * @return
     */
    @Override
    public List<MessageEventSlimDTO> saveGroupList(DashboardConfig dashboardConfig) {

        List<DashboardGroup> dashboardGroup = dashboardConfig.getGroupList();
        List<MessageEventSlimDTO> responseEvent = new ArrayList();
        
            //For each group -> insert in database
            for (DashboardGroup it : dashboardGroup) {
                it.setIdConfig(dashboardConfig.getIdConfig());
                Integer idGroup = this.create(it);

                if (idGroup > 0) {
                    // For each entry of group -> insert in base
                    for (DashboardEntry entry : it.getDashboardEntries()) {
                        entry.setIdGroup(idGroup);
                        responseEvent.add(this.dashboardEntryService.create(entry));
                    }
                    //Event insert group success
                    MessageEventSlimDTO event = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CREATE_GROUP_SUCCESS);
                    event.setDescription(event.getDescription().replace("%GROUP%", it.getAssociateElement()));
                    responseEvent.add(event);
                } else {
                    LOG.debug("Insert group failed");
                    //Event insert group failed
                    MessageEventSlimDTO event = new MessageEventSlimDTO(MessageEventEnum.DASHBOARD_CREATE_GROUP_FAILED);
                    event.setDescription(event.getDescription().replace("%GROUP%", it.getAssociateElement()));
                    responseEvent.add(event);
                }
            }
        return responseEvent;
    }

    /**
     * Convert group to dto to send dashboard data to front interface
     *
     * @param dashboardGroupEntries
     * @return
     */
    @Override
    public DashboardGroupDTO dashboardGroupToDTO(DashboardGroup dashboardGroupEntries) {
        List<DashboardEntryDTO> dashboardEntries = this.dashboardEntryService.convertEntryListToDTO(dashboardGroupEntries.getDashboardEntries());
        String associateElement = dashboardGroupEntries.getAssociateElement();
        String type = dashboardGroupEntries.getType();
        return new DashboardGroupDTO(dashboardEntries, associateElement, type);
    }

    @Override
    public Integer create(DashboardGroup dashboardGroup) {
        return dashboardGroupEntriesDAO.create(dashboardGroup);
    }

    @Override
    public List<DashboardGroup> readByIdConfig(long idConfig) {
        return this.dashboardGroupEntriesDAO.readByIdConfig(idConfig);
    }
}
