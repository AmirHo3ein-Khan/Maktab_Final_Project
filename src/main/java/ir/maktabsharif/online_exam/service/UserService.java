package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Role;
import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.model.dto.UserDto;
import ir.maktabsharif.online_exam.model.dto.request.AuthenticationRequest;
import ir.maktabsharif.online_exam.model.dto.request.RefreshRequest;
import ir.maktabsharif.online_exam.model.dto.response.AuthenticationResponse;
import ir.maktabsharif.online_exam.model.dto.response.UserResponseDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    AuthenticationResponse login(AuthenticationRequest request);
    AuthenticationResponse refreshToken(RefreshRequest req);
    void updateUser(Long id, UserDto updatedUser);
    User findByUsername(String username);
    User findUserById(Long id);
    void approveUser(Long id);
    void disApproveUser(Long id);
    List<UserResponseDto> searchUsers(String keyword);
    List<UserResponseDto> filterByRoleAndName(String roleName, String name);
}
