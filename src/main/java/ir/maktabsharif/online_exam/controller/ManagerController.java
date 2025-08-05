package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.model.dto.UserDto;
import ir.maktabsharif.online_exam.service.RoleService;
import ir.maktabsharif.online_exam.service.UserService;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    private final UserService userService;
    private final RoleService roleService;

    public ManagerController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/panel")
    public String home() {
        return "manager/managerPanel";
    }

    @PostMapping("/approve/{id}")
    public String approveUser(@PathVariable Long id) {
        userService.approveUser(id);
        return "redirect:/manager/search";
    }

    @PostMapping("/disapprove/{id}")
    public String disapprove(@PathVariable Long id) {
        userService.disApproveUser(id);
        return "redirect:/manager/search";
    }

    @GetMapping("/edit/{id}")
    public String updateUserForm(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "manager/edit-user";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") UserDto user , BindingResult result) {
        if (result.hasErrors()){
            return "manager/edit-user";
        }
        userService.updateUser(id, user);
        return "redirect:/manager/search?success";
    }

    @GetMapping("/search")
    public String searchUser(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<User> users = userService.searchUsers(keyword);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "manager/search";
    }

    @GetMapping("/filter")
    public String filterUsers(@RequestParam(required = false) String role, @RequestParam String name, Model model) {
        List<User> users = userService.filterByRoleAndName(role, name);
        model.addAttribute("users", users);
        model.addAttribute("role", roleService.findByName(role));
        model.addAttribute("name", name);
        return "manager/search";
    }

}
