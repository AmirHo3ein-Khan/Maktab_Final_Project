package ir.maktabsharif.online_exam.util;

import ir.maktabsharif.online_exam.service.ManagerService;
import ir.maktabsharif.online_exam.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ManagerInitializer implements CommandLineRunner {
    private final ManagerService managerService;
    private final RoleService roleService;

    public ManagerInitializer(ManagerService managerService, RoleService roleService) {
        this.managerService = managerService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args){
        roleService.createRolesIfNotExist();
        managerService.createAdminIfNotExists();
    }
}
