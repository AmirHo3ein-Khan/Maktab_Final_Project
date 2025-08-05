package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.model.Manager;
import ir.maktabsharif.online_exam.model.Role;
import ir.maktabsharif.online_exam.repository.ManagerRepository;
import ir.maktabsharif.online_exam.repository.RoleRepository;
import ir.maktabsharif.online_exam.repository.UserRepository;
import ir.maktabsharif.online_exam.service.ManagerService;
import javax.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceImpl implements ManagerService {
    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public ManagerServiceImpl(ManagerRepository managerRepository, UserRepository userRepository,
                              PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.managerRepository = managerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void createAdminIfNotExists() {
        Role managerRole = roleRepository.findByName("MANAGER").get();
        if (userRepository.findByRole(managerRole).isEmpty()) {
            Manager manager = new Manager();
            manager.setUsername("admin");
            manager.setPassword(passwordEncoder.encode("admin"));
            manager.setRole(managerRole);
            managerRepository.save(manager);
        }
    }
}
