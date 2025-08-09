package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.ChangePasswordDto;
import ir.maktabsharif.online_exam.model.dto.MasterDto;
import ir.maktabsharif.online_exam.model.dto.response.ApiResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.CourseResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.ExamResponseDto;
import ir.maktabsharif.online_exam.service.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/master")
public class MasterController {
    private final MasterService masterService;
    private final StudentExamService studentExamService;
    private final StudentService studentService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ExamService examService;
    private final GradingService gradingService;

    public MasterController(MasterService masterService, StudentExamService studentExamService, StudentService studentService, QuestionService questionService, AnswerService answerService, ExamService examService, GradingService gradingService) {
        this.masterService = masterService;
        this.studentExamService = studentExamService;
        this.studentService = studentService;

        this.questionService = questionService;
        this.answerService = answerService;
        this.examService = examService;
        this.gradingService = gradingService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> registerMaster(@Valid @RequestBody MasterDto masterDto) {
        masterService.masterRegister(masterDto);
        String msg = "register.master.success";
        ApiResponseDto body = new ApiResponseDto(msg, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponseDto> updateMaster(@PathVariable("id") Long id, @RequestBody @Valid MasterDto masterDto) {
        boolean isUpdate = masterService.updateMaster(id, masterDto);
        if (isUpdate) {
            String msg = "update.master.success";
            return ResponseEntity.ok(new ApiResponseDto(msg, true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/change/pass")
    public ResponseEntity<ApiResponseDto> changePass(@RequestBody ChangePasswordDto changePasswordDto, Principal principal) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        if (master == null) {
            throw new RuntimeException("User not found!");
        }
        if (!masterService.checkPassword(master, changePasswordDto.getOldPassword())) {
            String msg = "check.password.incorrect";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDto(msg, true));
        }
        masterService.changePassword(master, changePasswordDto.getNewPassword());
        String msg = "update.password.success";
        return ResponseEntity.ok(new ApiResponseDto(msg, true));
    }

    @GetMapping("/students/{studentId}/exams/completed")
    public ResponseEntity<List<ExamResponseDto>> getCompletedExamsOfStudent(@PathVariable Long studentId) {
        List<ExamResponseDto> completedExamsOfStudent = studentExamService.findCompletedExamsOfStudent(studentId);
        return ResponseEntity.ok(completedExamsOfStudent);
    }

    @GetMapping("/courses/master")
    public ResponseEntity<List<CourseResponseDto>> getMasterCourses(Principal principal) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        List<CourseResponseDto> masterCourses = masterService.findMasterCourses(master.getId());
        return ResponseEntity.ok(masterCourses);
    }

    @GetMapping("/students/{studentId}/exams/{examId}/grading")
    public ResponseEntity<Map<String, Object>> gradeExam(
            @PathVariable Long studentId,
            @PathVariable Long examId) {

        Student student = studentService.findById(studentId);
        List<ExamQuestion> examQuestions = questionService.findQuestionExamByExam(examId);

        List<Question> questions = examQuestions.stream()
                .map(ExamQuestion::getQuestion)
                .collect(Collectors.toList());
        for (Question question : questions) {
            if (question instanceof MultipleChoiceQuestion) {
                gradingService.autoMultipleChoiceGrading(examId, question.getId());
            }
        }

        Map<Long, Double> maxScores = new HashMap<>();
        for (Question question : questions) {
            Double maxScore = gradingService.getMaximumScoreForAnswer(examId, question.getId());
            maxScores.put(question.getId(), maxScore);
        }

        gradingService.setTotalScoreOfExamForStudent(studentId, examId);
        Double totalScore = gradingService.getScoreOfStudentInExam(studentId, examId);
        Map<Long, Double> answerGrades = gradingService.getAnswerGrades(studentId, examId);

        Map<Long, Object> rawAnswers = answerService.getStudentAnswers(studentId, examId);
        Map<Long, String> studentAnswers = new HashMap<>();
        for (Map.Entry<Long, Object> entry : rawAnswers.entrySet()) {
            Object val = entry.getValue();
            studentAnswers.put(entry.getKey(), val instanceof Option ? ((Option) val).getOptionText() : val.toString());
        }

        Map<Long, Boolean> isMCQMap = new HashMap<>();
        Map<Long, Boolean> isDESCMap = new HashMap<>();
        for (Question q : questions) {
            isMCQMap.put(q.getId(), q instanceof MultipleChoiceQuestion);
            isDESCMap.put(q.getId(), q instanceof DescriptiveQuestion);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("student", student);
        map.put("exam", examService.findById(examId));
        map.put("questions", questions);
        map.put("maxScores", maxScores);
        map.put("totalScore", totalScore);
        map.put("answerGrades", answerGrades);
        map.put("studentAnswers", studentAnswers);
        map.put("isMCQMap", isMCQMap);
        map.put("isDESCMap", isDESCMap);

        return ResponseEntity.ok(map);
    }

    @PostMapping("/students/{studentId}/exams/{examId}/questions/{questionId}/grade")
    public ResponseEntity<String> gradeDescriptiveQuestion(
            @PathVariable Long questionId,
            @PathVariable Long examId,
            @PathVariable Long studentId,
            @RequestParam Double score) {

        gradingService.gradingDescriptiveAnswer(questionId, examId, score);

        return ResponseEntity.ok("Graded question " + questionId + " for student " + studentId);
    }


}
