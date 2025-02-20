package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void updateUser(Long id, User updatedUser);
    Optional<User> findByUsername(String username);
    User findUserById(Long id);
    void approveUser(Long id);
    void disApproveUser(Long id);
    List<User> searchUsers(String keyword);
}
