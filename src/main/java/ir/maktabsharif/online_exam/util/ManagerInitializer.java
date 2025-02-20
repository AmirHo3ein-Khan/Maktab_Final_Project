package ir.maktabsharif.online_exam.util;

import ir.maktabsharif.online_exam.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ManagerInitializer implements ApplicationRunner {

    @Autowired
    private ManagerService managerService;

    @Override
    public void run(ApplicationArguments args) {
        managerService.createAdminIfNotExists();
    }
}
