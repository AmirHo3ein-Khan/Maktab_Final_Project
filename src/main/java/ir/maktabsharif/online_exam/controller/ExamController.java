package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.Exam;
import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.dto.ExamDto;
import ir.maktabsharif.online_exam.service.ExamService;
import ir.maktabsharif.online_exam.service.MasterService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/exam")
public class ExamController {
    private final ExamService examService;
    private final MasterService masterService;

    public ExamController(ExamService examService, MasterService masterService) {
        this.examService = examService;
        this.masterService = masterService;
    }

    @GetMapping("/{courseId}/save")
    public String showFormCreateExam(@PathVariable Long courseId, Model model) {
        model.addAttribute("exam", new ExamDto());
        model.addAttribute("courseId", courseId);
        return "exam/saveExam";
    }

    @PostMapping("/save")
    public String createExam(@RequestParam("courseId") Long courseId, @Valid @ModelAttribute("exam") ExamDto examDto , BindingResult result) {
        if (result.hasErrors()){
            return "exam/saveExam";
        }
        examService.addExamToCourse(courseId, examDto);
        return "redirect:/exam/coursesForAddExam?success";
    }


    @GetMapping("/coursesForAddExam")
    public String masterCourses(Model model, Principal principal) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        List<Course> courses = masterService.findMasterCourses(master.getId());

        model.addAttribute("courses", courses);
        return "exam/courses-for-add-exam";
    }

    @GetMapping("/edit/{id}")
    public String updateExamForm(@PathVariable Long id, Model model) {
        Exam exam = examService.findById(id);
        model.addAttribute("exam", exam);
        model.addAttribute("examId", exam.getId());
        return "exam/edit-exam";
    }

    @PostMapping("/edit/{id}")
    public String updateExam(@PathVariable Long id,@Valid @ModelAttribute("exam") ExamDto examDto , BindingResult result) {
        if (result.hasErrors()){
            return "exam/edit-exam";
        }
        boolean isUpdated = examService.updateExam(id, examDto);
        if (isUpdated) {
            return "redirect:/master/panel?successUpdate";
        }
        return "redirect:/master/panel?errorUpdate";
    }

    @PostMapping("/delete/{id}")
    public String deleteExam(@PathVariable Long id) {
        boolean isDeleted = examService.deleteExam(id);
        if (isDeleted) {
            return "redirect:/master/panel?successDelete";
        }
        return "redirect:/master/panel?errorDelete";
    }

}
