package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.ChangePasswordDto;
import ir.maktabsharif.online_exam.model.dto.StudentDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.DescriptiveAnswerDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.MultipleChoiceAnswerDto;
import ir.maktabsharif.online_exam.model.dto.response.ApiResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.CourseResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.ExamResponseDto;
import ir.maktabsharif.online_exam.service.*;
import ir.maktabsharif.online_exam.util.AnswerCacheService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final ExamService examService;

    public StudentController(StudentService studentService, ExamService examService) {
        this.studentService = studentService;
        this.examService = examService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> registerStudent(@Valid @RequestBody StudentDto studentDto) {
        studentService.studentRegister(studentDto);
        String msg = "register.student.success";
        ApiResponseDto body = new ApiResponseDto(msg , true);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponseDto> updateStudent(@PathVariable("id") Long id, @Valid @RequestBody StudentDto studentDto) {
        boolean isUpdate = studentService.updateStudent(id, studentDto);
        if (isUpdate) {
            String msg = "register.student.success";
            return ResponseEntity.ok(new ApiResponseDto(msg , true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PutMapping("/change/pass")
    public ResponseEntity<ApiResponseDto> changePass(@RequestBody ChangePasswordDto changePasswordDto ,Principal principal) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);
        if (student == null) {
            throw new RuntimeException("User not found!");
        }
        if (!studentService.checkPassword(student, changePasswordDto.getOldPassword())) {
            String msg = "check.password.incorrect";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDto(msg , true));
        }
        studentService.changePassword(student, changePasswordDto.getNewPassword());
        String msg = "update.password.success";
        return ResponseEntity.ok(new ApiResponseDto(msg , true));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/courses/student")
    public ResponseEntity<List<CourseResponseDto>> coursesOfStudent(Principal principal) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);
        List<CourseResponseDto> courses = studentService.coursesOfStudent(student.getId());
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{courseId}/student/exams")
    public ResponseEntity<List<ExamResponseDto>> studentExams(@PathVariable Long courseId) {
        List<ExamResponseDto> exams = examService.getCourseExams(courseId);
        return ResponseEntity.ok(exams);
    }

}
