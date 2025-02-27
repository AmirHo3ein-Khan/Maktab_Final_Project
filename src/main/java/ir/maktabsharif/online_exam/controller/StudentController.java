package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.Student;
import ir.maktabsharif.online_exam.model.dto.StudentDto;
import ir.maktabsharif.online_exam.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/panel")
    public String home(){
        return "student/studentPanel";
    }

    @GetMapping("/save")
    public String showSaveStudentForm(Model model) {
        model.addAttribute("student", new StudentDto());
        return "student/register";
    }

    @PostMapping("/save")
    public String saveStudent(@Valid @ModelAttribute("student") StudentDto studentDto , BindingResult result) {
        if (result.hasErrors()) {
            return "student/register";
        }
        studentService.saveStudent(studentDto);
        return "redirect:/login?success";
    }

    @GetMapping("/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.findById(id);
        model.addAttribute("student", student);
        return "student/edit-student";
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable Long id,@Valid @ModelAttribute("user") StudentDto studentDto) {
        boolean isUpdated = studentService.updateStudent(id, studentDto);
        if (isUpdated) {
            return "redirect:/student/edit?success";
        }
        return "redirect:/student/edit/{id}";
    }

}
