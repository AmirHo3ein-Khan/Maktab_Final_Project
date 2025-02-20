package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.User;
import ir.maktabsharif.online_exam.service.MasterService;
import ir.maktabsharif.online_exam.service.StudentService;
import ir.maktabsharif.online_exam.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    private final UserService userService;

    public ManagerController(UserService userService, StudentService studentService, MasterService masterService) {
        this.userService = userService;
    }

    @GetMapping("/panel")
    public String home(){
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
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user) {
        userService.updateUser(id, user);
        return "redirect:/manager/search";
    }

    @GetMapping("/search")
    public String searchUser(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<User> users = userService.searchUsers(keyword);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "manager/search";
    }

}
