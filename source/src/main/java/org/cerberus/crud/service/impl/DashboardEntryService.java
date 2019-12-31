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
import org.cerberus.crud.dao.IDashboardEntryDAO;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroup;
import org.cerberus.crud.service.IDashboardEntryDataService;
import org.cerberus.crud.service.IDashboardEntryService;
import org.cerberus.dto.DashboardEntryDTO;
import org.cerberus.dto.MessageEventSlimDTO;
import org.cerberus.enums.DashboardIndicatorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author cDelage
 */
@Service
public class DashboardEntryService implements IDashboardEntryService {

    private static final Logger LOG = LogManager.getLogger(DashboardEntryService.class);

    @Autowired
    private IDashboardEntryDAO dashboardEntryDAO;

    @Autowired
    private IDashboardEntryDataService dashboardEntryDataService;

    /**
     * Read all entry for a group. Call when groups is reading in
     * DashboardGroupService.
     *
     * @param dashboardGroup
     * @return
     */
    @Override
    public List<DashboardEntry> readByGroupEntriesWithData(DashboardGroup dashboardGroup) {
        List<DashboardEntry> response = new ArrayList();
        response = this.readByGroupEntries(dashboardGroup);
        for (DashboardEntry ent : response) {
            ent.setAssociateElement(dashboardGroup.getAssociateElement());
            ent.setEntryData(this.dashboardEntryDataService.read(ent));
        }
        return response;
    }

    /**
     * Convert list of entries to DTO Object.
     * DTO object content is Title and EntryData (for DashboardContent)
     * @param dashboardEntry
     * @return 
     */
    @Override
    public List<DashboardEntryDTO> convertEntryListToDTO(List<DashboardEntry> dashboardEntry) {

        List<DashboardEntryDTO> response = new ArrayList();

        for (DashboardEntry ent : dashboardEntry) {

            response.add(new DashboardEntryDTO(DashboardIndicatorEnum.getTitleByCodeIndicator(ent.getCodeIndicator()), ent.getEntryData()));
        }
        return response;
    }

    /**
     * read by group entries. Made to call DAO to read by group entries.
     *
     * @param dashboardGroupEntries
     * @return
     */
    @Override
    public List<DashboardEntry> readByGroupEntries(DashboardGroup dashboardGroupEntries) {
        return dashboardEntryDAO.readByGroupEntries(dashboardGroupEntries);
    }

    /**
     * Create a dashboard entry. Made to call DAO.
     *
     * @param dashboard entry
     * @return message
     */
    @Override
    public MessageEventSlimDTO create(DashboardEntry dashboardEntry) {
        return dashboardEntryDAO.create(dashboardEntry);
    }
}
