package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.dto.CourseDto;
import ir.maktabsharif.online_exam.service.CourseService;
import ir.maktabsharif.online_exam.service.MasterService;
import ir.maktabsharif.online_exam.service.StudentService;
import ir.maktabsharif.online_exam.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {
    private final CourseService courseService;
    private final MasterService masterService;
    private final StudentService studentService;

    public CourseController(CourseService courseService, UserService userService, StudentService studentService, MasterService masterService, StudentService studentService1, MasterService masterService1, UserService userService1, MasterService masterService2, StudentService studentService2) {
        this.courseService = courseService;
        this.masterService = masterService2;
        this.studentService = studentService2;
    }

    @GetMapping("/save")
    public String showRegistrationForm(Model model) {
        model.addAttribute("course", new CourseDto());
        return "course/saveCourse";
    }

    @PostMapping("/save")
    public String createCourse(CourseDto courseDto) {
        courseService.createCourse(courseDto);
        return "redirect:/course/save?success";
    }

    @GetMapping("/courses")
    public String courseList(Model model) {
        List<Course> courses = courseService.findAll();
        model.addAttribute("courses", courses);
        return "course/courses";
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
    public String addMasterToCourse(@RequestParam Long courseId, @RequestParam Long masterId) {
        courseService.addMasterToCourse(courseId, masterId);
        return "redirect:/course/coursesForAddMaster";
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
    public String addStudentToCourse(@RequestParam Long courseId, @RequestParam Long studentId) {
        courseService.addStudentToCourse(courseId, studentId);
        return "redirect:/course/coursesForAddStudent";
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/coursesForDetails")
    public String showCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "course/coursesForDetails";
    }

    @GetMapping("/{courseId}/details")
    public String showCourseDetails(@PathVariable Long courseId, Model model) {
        Course course = courseService.findDetailsOfCourse(courseId);
        model.addAttribute("course", course);
        model.addAttribute("students", course.getStudents());
        model.addAttribute("master", course.getMaster());
        model.addAttribute("courseId", courseId);
        return "course/course-details";
    }

    @PostMapping("/deleteStudentFromCourse")
    public String deleteStudentFromCourse(@RequestParam Long courseId, @RequestParam Long studentId) {
        courseService.deleteStudentFromCourse(courseId, studentId);
        return "redirect:/course/coursesForDetails";
    }
}
