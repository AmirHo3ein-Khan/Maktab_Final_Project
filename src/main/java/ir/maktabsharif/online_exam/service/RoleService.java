package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Role;

public interface RoleService {
    void createRolesIfNotExist();
    Role findByName(String name);
}
