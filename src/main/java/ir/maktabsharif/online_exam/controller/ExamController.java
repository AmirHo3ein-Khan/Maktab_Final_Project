package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.*;
import ir.maktabsharif.online_exam.model.dto.questiondto.*;
import ir.maktabsharif.online_exam.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/exam")
public class ExamController {
    private final ExamService examService;
    private final MasterService masterService;
    private final CourseService courseService;

    public ExamController(ExamService examService, MasterService masterService, CourseService courseService) {
        this.examService = examService;
        this.masterService = masterService;
        this.courseService = courseService;
    }

    @GetMapping("/{courseId}/save")
    public String showFormCreateExam(@PathVariable Long courseId, Principal principal, Model model) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        model.addAttribute("exam", new ExamDto());
        model.addAttribute("courseId", courseId);
        model.addAttribute("masterId", master.getId());
        return "exam/saveExam";
    }

    @PostMapping("/save")
    public String createExam(@RequestParam Long masterId, @RequestParam("courseId") Long courseId,
                             @Valid @ModelAttribute("exam") ExamDto examDto, BindingResult result) {
        if (result.hasErrors()) {
            return "exam/saveExam";
        }
        examService.addExamToCourse(courseId, masterId, examDto);
        return "redirect:/exam/coursesForAddExam?success";
    }


    @GetMapping("/edit/{id}")
    public String updateExamForm(@PathVariable Long id, Model model) {
        Exam exam = examService.findById(id);
        model.addAttribute("exam", exam);
        model.addAttribute("examId", exam.getId());
        return "exam/edit-exam";
    }

    @PostMapping("/edit/{id}")
    public String updateExam(@PathVariable Long id, @Valid @ModelAttribute("exam") ExamDto examDto, BindingResult result) {
        if (result.hasErrors()) {
            return "exam/edit-exam";
        }
        boolean isUpdated = examService.updateExam(id, examDto);
        if (isUpdated) {
            return "redirect:/master/panel?successUpdate";
        }
        return "redirect:/master/panel?errorUpdate";
    }
    //todo
    @PostMapping("/delete/{id}")
    public String deleteExam(@PathVariable Long id) {
        boolean isDeleted = examService.deleteExam(id);
        if (isDeleted) {
            return "redirect:/master/panel?successDelete";
        }
        return "redirect:/master/panel?errorDelete";
    }



    @GetMapping("/{courseId}/examsCourseForAddQuestion")
    private String courseExams(@PathVariable Long courseId, Model model) {
        Course course = courseService.findById(courseId);
        model.addAttribute("exams", course.getExams());
        model.addAttribute("courseId", courseId);
        return "exam/examsOfCourseForAddQuestion";
    }

    @GetMapping("/coursesForAddExam")
    public String masterCourses(Model model, Principal principal) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        List<Course> courses = masterService.findMasterCourses(master.getId());

        model.addAttribute("courses", courses);
        return "exam/courses-for-add-exam";
    }

}