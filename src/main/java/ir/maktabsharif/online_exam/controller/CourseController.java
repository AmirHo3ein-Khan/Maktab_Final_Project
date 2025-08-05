package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.dto.*;
import ir.maktabsharif.online_exam.service.CourseService;
import ir.maktabsharif.online_exam.service.MasterService;
import ir.maktabsharif.online_exam.service.StudentService;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/course")
public class CourseController {
    private final CourseService courseService;
    private final MasterService masterService;
    private final StudentService studentService;

    public CourseController(CourseService courseService, MasterService masterService, StudentService studentService) {
        this.courseService = courseService;
        this.masterService = masterService;
        this.studentService = studentService;
    }

    @GetMapping("/save")
    public String createCourseForm(Model model) {
        model.addAttribute("course", new CourseRequestDto());
        return "course/saveCourse";
    }

    @PostMapping("/save")
    public String createCourse(@Valid @ModelAttribute("course") CourseRequestDto courseRequestDto, BindingResult result) {
        if (result.hasErrors()){
            return "course/saveCourse";
        }
        courseService.createCourse(courseRequestDto);
        return "redirect:/course/save?success";
    }

    @GetMapping("/edit/{id}")
    public String updateCourseForm(@PathVariable Long id , Model model) {
        Course course = courseService.findById(id);
        model.addAttribute("course" , course);
        model.addAttribute("courseId" , course.getId());
        return "course/updateCourse";
    }

    @PostMapping("/edit/{id}")
    public String updateCourse(@PathVariable Long id ,@Valid @ModelAttribute("course") CourseRequestDto courseRequestDto, BindingResult result) {
        if (result.hasErrors()){
            return "course/updateCourse";
        }
        courseService.updateCourse(id , courseRequestDto);
        return "redirect:/course/coursesForDetails?successUpdate";
    }

    //todo bug!!!!!!!!!!
    @GetMapping("/{courseId}/editMaster")
    public String showMastersForUpdateCourse(@PathVariable Long courseId, Model model) {
        model.addAttribute("masters", masterService.findAll());
        model.addAttribute("courseId", courseId);
        return "course/mastersForUpdateCourse";
    }
    @PostMapping("/editMaster")
    public String updateMasterOfCourse(@ModelAttribute UpdateMasterOfCourseDto updateMasterOfCourseDto) {
        courseService.updateMasterOfCourse(updateMasterOfCourseDto);
        return "redirect:/course/coursesForDetails?successUpdate";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/course/coursesForDetails?successDelete";
    }


    @GetMapping("/coursesForAddMaster")
    public String showCoursesForAddMaster(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "course/coursesForAddMaster";
    }

    @GetMapping("/{courseId}/addMaster")
    public String showMastersForCourse(@PathVariable Long courseId, Model model) {
        model.addAttribute("masters", masterService.findAll());
        model.addAttribute("courseId", courseId);
        return "course/masters";
    }

    @PostMapping("/addMasterToCourse")
    public String addMasterToCourse(@ModelAttribute AddMasterToCourseDto addMasterToCourseDto) {
        courseService.addMasterToCourse(addMasterToCourseDto);
        return "redirect:/course/coursesForAddMaster?success";
    }

    @GetMapping("/coursesForAddStudent")
    public String showCoursesForAddStudent(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "course/coursesForAddStudent";
    }


    @GetMapping("/{courseId}/addStudent")
    public String showStudentsForCourse(@PathVariable Long courseId, Model model) {
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("courseId", courseId);
        return "course/students";
    }

    @PostMapping("/addStudentToCourse")
    public String addStudentToCourse(@ModelAttribute AddStudentToCourseDto addStudentToCourseDto) {
        courseService.addStudentToCourse(addStudentToCourseDto);
        return "redirect:/course/coursesForAddStudent?success";
    }

    @GetMapping("/coursesForDetails")
    public String showCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "course/coursesForDetails";
    }

    @GetMapping("/{courseId}/details")
    public String showCourseDetails(@PathVariable Long courseId, Model model) {
        Course course = courseService.findById(courseId);
        model.addAttribute("course", course);
        model.addAttribute("students", course.getStudents());
        model.addAttribute("master", course.getMaster());
        return "course/course-details";
    }

    @PostMapping("/deleteStudentFromCourse")
    public String deleteStudentFromCourse(@ModelAttribute DeleteStudentFromCourseDto deleteStudentFromCourseDto) {
        courseService.deleteStudentFromCourse(deleteStudentFromCourseDto);
        return "redirect:/course/coursesForDetails?success";
    }
}
