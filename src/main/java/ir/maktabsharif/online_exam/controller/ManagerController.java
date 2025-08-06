package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.model.dto.response.ApiResponseDto;
import ir.maktabsharif.online_exam.service.RoleService;
import ir.maktabsharif.online_exam.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    private final UserService userService;
    private final RoleService roleService;

    public ManagerController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/approve/{id}")
    public ResponseEntity<ApiResponseDto> approveUser(@PathVariable Long id) {
        userService.approveUser(id);
        String msg = "user approved";
        return ResponseEntity.ok(new ApiResponseDto(msg , true));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/disapprove/{id}")
    public ResponseEntity<ApiResponseDto> disapprove(@PathVariable Long id) {
        userService.disApproveUser(id);
        String msg = "user disapproved";
        return ResponseEntity.ok(new ApiResponseDto(msg , true));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUser(@RequestParam(name = "keyword", required = false) String keyword) {
        List<User> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/filter")
    public ResponseEntity<List<User>> filterUsers(@RequestParam(required = false) String role, @RequestParam String name) {
        List<User> users = userService.filterByRoleAndName(role, name);
        return ResponseEntity.ok(users);
    }
}
