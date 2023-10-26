package com.qronicle.dao.interfaces;

import com.qronicle.entity.Role;

public interface RoleDao {
    Role findRoleByName(String name);
}
