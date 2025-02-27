package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.model.Role;
import ir.maktabsharif.online_exam.repository.RoleRepository;
import ir.maktabsharif.online_exam.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void createRolesIfNotExist() {
        Optional<Role> student = roleRepository.findByName("STUDENT");
        Optional<Role> master = roleRepository.findByName("MASTER");
        Optional<Role> manager = roleRepository.findByName("MANAGER");
        if (student.isEmpty() && master.isEmpty() && manager.isEmpty()) {
            roleRepository.save(Role.builder().name("STUDENT").build());
            roleRepository.save(Role.builder().name("MASTER").build());
            roleRepository.save(Role.builder().name("MANAGER").build());
        }
    }


    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).get();
    }


}
