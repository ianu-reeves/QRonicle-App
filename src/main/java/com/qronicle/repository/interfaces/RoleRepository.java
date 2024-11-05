package com.qronicle.repository.interfaces;

import com.qronicle.entity.Role;

public interface RoleRepository {
    Role findRoleByName(String name);
}
