package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.ResourcesNotFundException;
import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.model.enums.RegisterState;
import ir.maktabsharif.online_exam.model.enums.UserType;
import ir.maktabsharif.online_exam.repository.UserRepository;
import ir.maktabsharif.online_exam.service.UserService;
import ir.maktabsharif.online_exam.util.UserSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public void updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUserType().equals(UserType.STUDENT)) {

        }
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setUsername(updatedUser.getUsername());
        user.setEmail(updatedUser.getEmail());
        user.setDob(updatedUser.getDob());
        user.setUserType(updatedUser.getUserType());
        userRepository.save(user);
    }
    @Override
    public List<User> searchUsers(String keyword) {
        Specification<User> spec = UserSpecification.searchByKeyword(keyword);
        List<User> users = userRepository.findAll(spec);
        List<User> usersWithoutManager = new ArrayList<>();
        for (User user : users) {
            if (!user.getUserType().equals(UserType.MANAGER)) {
                usersWithoutManager.add(user);
            }
        }
        return usersWithoutManager;
    }


}
