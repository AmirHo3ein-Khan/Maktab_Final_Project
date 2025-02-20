package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.model.Manager;
import ir.maktabsharif.online_exam.model.enums.UserType;
import ir.maktabsharif.online_exam.repository.ManagerRepository;
import ir.maktabsharif.online_exam.repository.UserRepository;
import ir.maktabsharif.online_exam.service.ManagerService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl implements ManagerService {
    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerServiceImpl(ManagerRepository managerRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createAdminIfNotExists() {
        if (userRepository.findByUserType(UserType.MANAGER).isEmpty()) {
            Manager manager = new Manager();
            manager.setUsername("admin");
            manager.setPassword(passwordEncoder.encode("admin"));
            manager.setUserType(UserType.MANAGER);
            managerRepository.save(manager);
        }
    }
}
