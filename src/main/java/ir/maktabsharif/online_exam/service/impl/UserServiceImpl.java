package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.EntityNotFoundException;
import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.model.dto.UserDto;
import ir.maktabsharif.online_exam.model.dto.response.UserResponseDto;
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


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with this username: " + username));
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with this id: " +id));
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
    public List<UserResponseDto> searchUsers(String keyword) {
        Specification<User> spec = UserSpecification.searchByKeyword(keyword);
        List<User> users = userRepository.findAll(spec);
        List<User> usersWithoutManager = new ArrayList<>();
        for (User user : users) {
            if (!user.getRole().getName().equals("MANAGER")) {
                usersWithoutManager.add(user);
            }
        }
        List<UserResponseDto> usersDto = new ArrayList<>();
        for (User user : usersWithoutManager){
            usersDto.add(UserResponseDto.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .registerState(user.getRegisterState())
                    .dob(user.getDob())
                    .build());
        }
        return usersDto;
    }

    @Override
    public List<UserResponseDto> filterByRoleAndName(String roleName, String name) {
        List<User> userByRoleAndName = userRepository.findUserByRoleAndName(roleName, name);
        List<UserResponseDto> usersDto = new ArrayList<>();
        for (User user : userByRoleAndName){
            usersDto.add(UserResponseDto.builder()
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .registerState(user.getRegisterState())
                    .dob(user.getDob())
                    .build());
        }
        return usersDto;
    }


}
