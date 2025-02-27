package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.ResourcesNotFundException;
import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.model.dto.UserDto;
import ir.maktabsharif.online_exam.model.enums.RegisterState;
import ir.maktabsharif.online_exam.repository.RoleRepository;
import ir.maktabsharif.online_exam.repository.UserRepository;
import ir.maktabsharif.online_exam.service.UserService;
import ir.maktabsharif.online_exam.util.UserSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFundException("User not found!"));
    }


    @Override
    public void approveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRegisterState(RegisterState.CONFIRM);
        userRepository.save(user);
    }

    @Override
    public void disApproveUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRegisterState(RegisterState.WAITING);
        userRepository.save(user);
    }


    @Override
    public void updateUser(Long id, UserDto updatedUser) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setDob(updatedUser.getDob());
        userRepository.save(user);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        Specification<User> spec = UserSpecification.searchByKeyword(keyword);
        List<User> users = userRepository.findAll(spec);
        List<User> usersWithoutManager = new ArrayList<>();
        for (User user : users) {
            if (!user.getRole().getName().equals("MANAGER")) {
                usersWithoutManager.add(user);
            }
        }
        return usersWithoutManager;
    }

    @Override
    public List<User> filterByRoleAndName(String roleName, String name) {
        return userRepository.findUserByRoleAndName(roleName, name);
    }


}
