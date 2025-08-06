package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.DescriptiveQuestion;
import ir.maktabsharif.online_exam.model.MultipleChoiceQuestion;
import ir.maktabsharif.online_exam.model.Question;
import ir.maktabsharif.online_exam.model.dto.*;
import ir.maktabsharif.online_exam.model.dto.response.ApiResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.CourseResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.QuestionResponseDto;
import ir.maktabsharif.online_exam.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto> createCourse(@Valid @RequestBody CourseRequestDto courseRequestDto) {
        courseService.createCourse(courseRequestDto);
        String msg = "course.saved.success";
        ApiResponseDto body = new ApiResponseDto(msg , true);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponseDto> updateCourse(@PathVariable Long id , @Valid @RequestBody CourseRequestDto courseRequestDto) {
        courseService.updateCourse(id , courseRequestDto);
        String msg = "course.update.success";
        return ResponseEntity.ok(new ApiResponseDto(msg , true));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/master/course/update")
    public ResponseEntity<ApiResponseDto> updateMasterOfCourse(@RequestBody UpdateMasterOfCourseDto updateMasterOfCourseDto) {
        courseService.updateMasterOfCourse(updateMasterOfCourseDto);
        return ResponseEntity.ok(new ApiResponseDto("course.update.success", true));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/courses/masters/assign")
    public ResponseEntity<ApiResponseDto> addMasterToCourse(@RequestBody AddMasterToCourseDto addMasterToCourseDto) {
        courseService.addMasterToCourse(addMasterToCourseDto);
        String msg = "course.add.master.saved";
        return ResponseEntity.ok(new ApiResponseDto(msg , true));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/courses/student/assign")
    public ResponseEntity<ApiResponseDto> addStudentToCourse(@RequestBody AddStudentToCourseDto addStudentToCourseDto) {
        courseService.addStudentToCourse(addStudentToCourseDto);
        String msg = "course.add.student.saved";
        return ResponseEntity.ok(new ApiResponseDto(msg , true));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDto> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        String msg = "course.delete.success";
        return ResponseEntity.ok(new ApiResponseDto(msg , true));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/find/all")
    public ResponseEntity<List<CourseResponseDto>> getAllCourse() {
        List<CourseResponseDto> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/delete/student/course")
    public String deleteStudentFromCourse(@ModelAttribute DeleteStudentFromCourseDto deleteStudentFromCourseDto) {
        courseService.deleteStudentFromCourse(deleteStudentFromCourseDto);
        return "redirect:/course/coursesForDetails?success";
    }

    // todo : add the login in service
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/{courseId}/course/question/bank")
    private ResponseEntity<List<QuestionResponseDto>> courseQuestionBank(@PathVariable Long courseId) {
        List<MultipleChoiceQuestion> multipleChoiceQuestions = courseService.mcqBank(courseId);
        List<DescriptiveQuestion> descriptiveQuestions = courseService.dqBank(courseId);
        List<Question> questions = new ArrayList<>();
        questions.addAll(multipleChoiceQuestions);
        questions.addAll(descriptiveQuestions);
        return null;
    }
}
