package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.dto.MasterDto;
import ir.maktabsharif.online_exam.service.CourseService;
import ir.maktabsharif.online_exam.service.MasterService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/master")
public class MasterController {
    private final MasterService masterService;
    private final CourseService courseService;

    public MasterController(MasterService masterService, CourseService courseService) {
        this.masterService = masterService;
        this.courseService = courseService;
    }

    @GetMapping("/panel")
    public String home() {
        return "master/masterPanel";
    }


    @GetMapping("/save")
    public String showSaveMasterForm(Model model) {
        model.addAttribute("master", new MasterDto());
        return "master/register";
    }

    @PostMapping("/save")
    public String saveMaster(@Valid @ModelAttribute("master") MasterDto masterDto, BindingResult result) {
        if (result.hasErrors()) {
            return "master/register";
        }
        masterService.masterRegister(masterDto);
        return "redirect:/login?success";
    }

//    @GetMapping("/verify")
//    public String verifyAccount(@RequestParam("token") String token, Model model) {
//        if (masterService.verifyMaster(token)) {
//            model.addAttribute("message", "Verification successful! You can now log in.");
//        } else {
//            model.addAttribute("message", "Invalid verification link.");
//        }
//        return "verification-result";
//    }

    @GetMapping("/edit")
    public String editMasterForm(Principal principal, Model model) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        model.addAttribute("master", master);
        model.addAttribute("masterId", master.getId());
        return "master/edit-master";
    }

    @PostMapping("/edit/{id}")
    public String updateMaster(@PathVariable("id") Long id, @Valid @ModelAttribute("master") MasterDto masterDto, BindingResult result) {
        if (result.hasErrors()) {
            return "master/edit-master";
        }
        boolean isUpdate = masterService.updateMaster(id, masterDto);
        if (isUpdate) {
            return "redirect:/master/edit?success";
        }
        return "redirect:/master/edit/{id}";
    }

    @GetMapping("/courses")
    public String masterCourses(Model model, Principal principal) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        List<Course> courses = masterService.findMasterCourses(master.getId());

        model.addAttribute("courses", courses);
        return "master/courses";
    }

    @GetMapping("/{courseId}/studentsCourse")
    private String courseStudents(@PathVariable Long courseId, Model model) {
        Course course = courseService.findCourseStudents(courseId);
        model.addAttribute("students", course.getStudents());
        return "master/studentsOfCourse";
    }

    @GetMapping("/{courseId}/examsCourse")
    private String courseExams(@PathVariable Long courseId, Model model) {
        Course course = courseService.findCourseExams(courseId);
        model.addAttribute("exams", course.getExams());
        return "master/examsOfCourse";
    }

    @GetMapping("/changePass")
    public String changePassForm(Principal principal, Model model) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        if (master == null) {
            throw new RuntimeException("User not found!");
        }
        model.addAttribute("master", master);
        return "master/changePass";
    }

    @PostMapping("/changePass")
    public String changePass(@RequestParam("oldPassword") String oldPassword,
                             @RequestParam("newPassword") String newPassword,
                             Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);

        if (master == null) {
            throw new RuntimeException("User not found!");
        }

        if (!masterService.checkPassword(master, oldPassword)) {
            redirectAttributes.addFlashAttribute("error", "Old password is incorrect.");
            return "redirect:/master/changePass";
        }

        masterService.changePassword(master, newPassword);
        redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/master/panel";
    }


}
