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
package org.cerberus.crud.service;

import java.util.List;
import java.util.Map;
import org.cerberus.crud.entity.DashboardConfig;
import org.cerberus.crud.entity.DashboardEntry;
import org.cerberus.crud.entity.DashboardGroup;
import org.cerberus.crud.entity.User;
import org.cerberus.dto.DashboardGroupConfigDTO;
import org.cerberus.dto.DashboardIndicatorConfigDTO;
import org.cerberus.dto.DashboardTypeConfigDTO;
import org.cerberus.dto.MessageEventSlimDTO;

/**
 *
 * @author cDelage
 */
public interface IDashboardConfigService {

    public List<DashboardTypeConfigDTO> readStatement(DashboardConfig conf);

    public DashboardGroup dashboardGroupFromConfigDTO(DashboardGroupConfigDTO dashboardGroupConfigDTO, long idConfig);

    public List<DashboardEntry> convertEntryFromConfigDTO(List<DashboardIndicatorConfigDTO> dashboardIndicator);

    public long create(DashboardConfig conf, User user);

    public List<DashboardConfig> readAllConfigsForUser(User user);

    public DashboardConfig read(String title, User user);

    public MessageEventSlimDTO delete(String title, User user);

    public Map<String, Object> readDashboard(User user, String title);

    public MessageEventSlimDTO checkDashboardIntegrity(List<DashboardGroup> listGroup);

    public List<MessageEventSlimDTO> saveConfig(List<DashboardTypeConfigDTO> dashboardGroupDTO, String titleConfig, User user, User usrCreated);

    public boolean isExistingConfig(String title, User user);

    public List<MessageEventSlimDTO> switchConfig(String title, User user);
}
