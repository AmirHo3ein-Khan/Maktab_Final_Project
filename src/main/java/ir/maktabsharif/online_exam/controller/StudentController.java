package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.ChangePasswordDto;
import ir.maktabsharif.online_exam.model.dto.StudentDto;
import ir.maktabsharif.online_exam.model.dto.StudentExamDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.DescriptiveAnswerDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.MultipleChoiceAnswerDto;
import ir.maktabsharif.online_exam.model.dto.response.ApiResponseDto;
import ir.maktabsharif.online_exam.model.enums.ExamState;
import ir.maktabsharif.online_exam.service.*;
import ir.maktabsharif.online_exam.util.AnswerCacheService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final ExamService examService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final StudentExamService studentExamService;
    private final AnswerCacheService answerCacheService;
    private final GradingService gradingService;

    public StudentController(StudentService studentService, CourseService courseService, ExamService examService, QuestionService questionService, AnswerService answerService, StudentExamService studentExamService, AnswerCacheService answerCacheService, GradingService gradingService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.examService = examService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.studentExamService = studentExamService;
        this.answerCacheService = answerCacheService;
        this.gradingService = gradingService;
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
    public ResponseEntity<ApiResponseDto> updateStudent(@PathVariable("id") Long id, @Valid StudentDto studentDto) {
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
    public ResponseEntity<List<Course>> coursesOfStudent(Principal principal) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);
        List<Course> courses = studentService.coursesOfStudent(student.getId());
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{courseId}/student/exams")
    public ResponseEntity<List<Exam>> studentExams(@PathVariable Long courseId) {
        Course course = courseService.findById(courseId);
        List<Exam> exams = course.getExams();
        return ResponseEntity.ok(exams);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/exam/start")
    public ResponseEntity<StudentExam> startExam(@RequestBody StudentExamDto studentExamDto) {
        studentExamDto.getExamId();
        Exam exam = examService.findById(studentExamDto.getExamId());
        if (exam.getExamState() == ExamState.NOT_STARTED) {
            throw new RuntimeException("exam.state.not_started");
        }
        if (exam.getExamState() == ExamState.FINISHED) {
            throw new RuntimeException("exam.state.finished");
        }
        StudentExam studentExam = studentExamService.startExam(studentExamDto);
        return ResponseEntity.ok(studentExam);
    }

    @PostMapping("/exam/student/answers/{questionIndex}")
    public ResponseEntity<Answer> studentAnswers(@RequestBody StudentExamDto studentExamDto,
                                                      @PathVariable int questionIndex) {
        StudentExam studentExam = studentExamService.findStudentExam(studentExamDto.getStudentId(), studentExamDto.getExamId());
        String cacheKey = studentExam.getExam().getId() + "_" + studentExam.getStudent().getId() + "_" + questionIndex;
        Object cachedAnswer = answerCacheService.getAnswerFromCache(cacheKey);

        if (cachedAnswer != null) {
            if (cachedAnswer instanceof MultipleChoiceAnswerDto mcAnswerDto) {
                Long selectedOptionId = mcAnswerDto.getSelectedOptionId();
                Answer answer = answerService.findAnswer(selectedOptionId);
            }
            if (cachedAnswer instanceof DescriptiveAnswerDto descAnswerDto) {
            }
        }
        return null;
    }


}
