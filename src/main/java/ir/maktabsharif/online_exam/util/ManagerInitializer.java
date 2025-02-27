package ir.maktabsharif.online_exam.util;

import ir.maktabsharif.online_exam.service.ManagerService;
import ir.maktabsharif.online_exam.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ManagerInitializer implements CommandLineRunner {

    @Autowired
    private ManagerService managerService;
    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        roleService.createRolesIfNotExist();
        managerService.createAdminIfNotExists();
    }
}
