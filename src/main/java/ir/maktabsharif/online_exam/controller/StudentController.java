package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.ChangePasswordDto;
import ir.maktabsharif.online_exam.model.dto.StudentDto;
import ir.maktabsharif.online_exam.model.dto.StudentExamDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.DescriptiveAnswerDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.MultipleChoiceAnswerDto;
import ir.maktabsharif.online_exam.model.dto.response.ApiResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.CourseResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.ExamResponseDto;
import ir.maktabsharif.online_exam.model.enums.ExamState;
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
        Course course = courseService.findById(courseId);
        List<ExamResponseDto> exams = examService.getCourseExams(courseId);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{examId}/questions/{questionId}")
    public ResponseEntity<?> getQuestionById(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            Principal principal) {

        Question question = questionService.findById(questionId); // Make sure this exists

        Student student = studentService.findByUsername(principal.getName());
        StudentExam studentExam = studentExamService.findStudentExam(student.getId(), examId);
        if (studentExam == null) return ResponseEntity.status(404).body("Exam not started.");

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(studentExam.getEndAt())) {
            return ResponseEntity.status(400).body("Exam expired.");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("questionId", questionId);
        map.put("questionText", question.getQuestionText());
        map.put("questionType", question instanceof MultipleChoiceQuestion ? "MCQ" : "DESC");
        map.put("remainingTimeSeconds", Duration.between(now, studentExam.getEndAt()).getSeconds());

        String cacheKey = examId + "_" + student.getId() + "_" + questionId;
        Object cachedAnswer = answerCacheService.getAnswerFromCache(cacheKey);

        if (cachedAnswer instanceof MultipleChoiceAnswerDto mcq) {
            map.put("selectedOptionId", mcq.getSelectedOptionId());
        } else if (cachedAnswer instanceof DescriptiveAnswerDto desc) {
            map.put("answer", desc.getAnswer());
        }

        map.put("question", question);

        return ResponseEntity.ok(map);
    }


    @PostMapping("/{examId}/saveAnswer")
    public ResponseEntity<?> saveAnswer(
            @PathVariable Long examId,
            @RequestParam Long studentId,
            @RequestParam int questionIndex,
            @RequestParam(required = false) Long selectedOptionId,
            @RequestParam(required = false) String answer) {

        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(examService.multipleChoiceQuestionsOfExam(examId));
        allQuestions.addAll(examService.descriptiveQuestionsOfExam(examId));

        if (questionIndex < 0 || questionIndex >= allQuestions.size()) {
            return ResponseEntity.badRequest().body("Invalid question index.");
        }

        Question question = allQuestions.get(questionIndex);
        String cacheKey = examId + "_" + studentId + "_" + questionIndex;

        if (question instanceof MultipleChoiceQuestion && selectedOptionId != null) {
            MultipleChoiceAnswerDto mcAnswerDto = new MultipleChoiceAnswerDto(question.getId(), examId, studentId, selectedOptionId);
            answerCacheService.saveAnswerToCache(cacheKey, mcAnswerDto);
        } else if (question instanceof DescriptiveQuestion && answer != null) {
            DescriptiveAnswerDto descAnswerDto = new DescriptiveAnswerDto(question.getId(), examId, studentId, answer);
            answerCacheService.saveAnswerToCache(cacheKey, descAnswerDto);
        } else {
            return ResponseEntity.badRequest().body("Invalid answer data.");
        }

        return ResponseEntity.ok("Answer saved successfully.");
    }

    @GetMapping("/{examId}/submitExam")
    public ResponseEntity<?> endExam(@PathVariable Long examId, Principal principal) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);

        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(examService.multipleChoiceQuestionsOfExam(examId));
        allQuestions.addAll(examService.descriptiveQuestionsOfExam(examId));

        for (int questionIndex = 0; questionIndex < allQuestions.size(); questionIndex++) {
            String cacheKey = examId + "_" + student.getId() + "_" + questionIndex;
            Object cachedAnswer = answerCacheService.getAnswerFromCache(cacheKey);

            if (cachedAnswer instanceof MultipleChoiceAnswerDto mcAnswerDto) {
                answerService.saveMultipleChoiceAnswer(mcAnswerDto);
            } else if (cachedAnswer instanceof DescriptiveAnswerDto descAnswerDto) {
                answerService.saveDescriptiveAnswer(descAnswerDto);
            }

            answerCacheService.clearAnswerFromCache(cacheKey);
        }

        studentExamService.submitExam(student.getId(), examId);
        answerCacheService.clearAllAnswers();

        return ResponseEntity.ok("Exam submitted successfully.");
    }

    @GetMapping("/{examId}/reviewExam")
    public ResponseEntity<?> reviewExam(@PathVariable Long examId, Principal principal) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);

        List<QuestionExam> questionExams = questionService.findQuestionExamByExam(examId);
        List<Question> allQuestions = questionExams.stream()
                .map(QuestionExam::getQuestion)
                .collect(Collectors.toList());

        Map<Long, Object> rawStudentAnswers = answerService.getStudentAnswers(student.getId(), examId);

        List<Map<String, Object>> reviewedQuestions = new ArrayList<>();

        for (Question question : allQuestions) {
            Map<String, Object> qData = new HashMap<>();
            qData.put("questionId", question.getId());
            qData.put("questionText", question.getQuestionText());
            qData.put("type", question instanceof MultipleChoiceQuestion ? "MCQ" : "DESC");

            Object answer = rawStudentAnswers.get(question.getId());
            if (answer instanceof Option option) {
                qData.put("answer", option.getOptionText());
            } else {
                qData.put("answer", answer != null ? answer.toString() : null);
            }

            reviewedQuestions.add(qData);
        }

        return ResponseEntity.ok(reviewedQuestions);
    }



}
