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
package org.cerberus.crud.dao;

import java.sql.ResultSet;
import java.util.List;
import org.cerberus.crud.entity.DashboardConfig;
import org.cerberus.crud.entity.User;
import org.cerberus.dto.MessageEventSlimDTO;

/**
 *
 * @author cDelage
 */
public interface IDashboardConfigDAO {

    public long create(DashboardConfig conf, User user);

    public MessageEventSlimDTO delete(String title);

    public DashboardConfig read(String title, User user);

    public List<DashboardConfig> readConfigForUser(User user);

    public DashboardConfig loadFromResultSet(ResultSet rs, User user);

}
