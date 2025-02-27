package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Role;
import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.model.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void updateUser(Long id, UserDto updatedUser);
    Optional<User> findByUsername(String username);
    User findUserById(Long id);
    void approveUser(Long id);
    void disApproveUser(Long id);
    List<User> searchUsers(String keyword);
    List<User> filterByRoleAndName(String roleName, String name);
}
