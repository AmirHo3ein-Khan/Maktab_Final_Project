package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.ExamDto;
import ir.maktabsharif.online_exam.model.dto.SubmitAnswersDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.DescriptiveAnswerDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.MultipleChoiceAnswerDto;
import ir.maktabsharif.online_exam.model.dto.request.ExamRequestDto;
import ir.maktabsharif.online_exam.model.dto.response.*;
import ir.maktabsharif.online_exam.service.*;
import ir.maktabsharif.online_exam.util.AnswerCacheService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exam")
public class ExamController {
    private final StudentExamService studentExamService;
    private final AnswerCacheService answerCacheService;
    private final QuestionService questionService;
    private final StudentService studentService;
    private final AnswerService answerService;
    private final CourseService courseService;
    private final ExamService examService;

    public ExamController(ExamService examService, StudentExamService studentExamService,
                          CourseService courseService, QuestionService questionService,
                          AnswerService answerService, AnswerCacheService answerCacheService, StudentService studentService) {
        this.examService = examService;
        this.studentExamService = studentExamService;
        this.courseService = courseService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.answerCacheService = answerCacheService;
        this.studentService = studentService;
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/exam/course/assign")
    public ResponseEntity<ApiResponseDto> createExam(@Valid @RequestBody ExamRequestDto examDto) {
        examService.createExamForCourse(examDto);
        String msg = "exam.create.success";
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto(msg, true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponseDto> updateExam(@PathVariable Long id,
                                                     @Valid @RequestBody ExamDto examDto) {
        boolean isUpdated = examService.updateExam(id, examDto);
        if (isUpdated) {
            String msg = "update.exam.success";
            return ResponseEntity.ok(new ApiResponseDto(msg, true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseDto> deleteExam(@PathVariable Long id) {
        boolean isDeleted = examService.deleteExam(id);
        if (isDeleted) {
            String msg = "delete.exam.success";
            return ResponseEntity.ok(new ApiResponseDto(msg, true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PreAuthorize("{hasRole('MASTER') , hasRole('STUDENT')}")
    @GetMapping("/{examId}/questions/exam")
    public ResponseEntity<List<QuestionResponseDto>> getExamQuestions( @PathVariable Long examId) {
        List<QuestionResponseDto> questionsOfExam = examService.getQuestionsOfExam(examId);
        return ResponseEntity.ok(questionsOfExam);
    }

    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/{examId}/exam/details")
    public ResponseEntity<ExamDetailsDto> examDetails(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.examDetails(examId));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/start/exam/{examId}/{studentId}")
    public ResponseEntity<StudentExamResponseDTO> startExam(@PathVariable Long examId,@PathVariable Long studentId) {
        StudentExamResponseDTO studentExamResponseDTO = studentExamService.startExam(examId, studentId);
        return  ResponseEntity.ok(studentExamResponseDTO);
    }


    @PostMapping("/saveAnswer")
    public ResponseEntity<?> saveAnswer(@RequestBody SubmitAnswersDto dto) {

        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(examService.multipleChoiceQuestionsOfExam(dto.getExamId()));
        allQuestions.addAll(examService.descriptiveQuestionsOfExam(dto.getExamId()));

        if (dto.getQuestionId() < 0 ) { //|| dto.getQuestionId() >= allQuestions.size()
            return ResponseEntity.badRequest().body("Invalid question index.");
        }

        Question question = allQuestions.get(dto.getQuestionId()-1);
        String cacheKey = dto.getExamId() + "_" + dto.getStudentId() + "_" + dto.getQuestionId();

        if (question instanceof MultipleChoiceQuestion && dto.getSelectedOptionId() != null) {
            MultipleChoiceAnswerDto mcAnswerDto = new MultipleChoiceAnswerDto(question.getId(), dto.getExamId(), dto.getStudentId(), dto.getSelectedOptionId());
            answerCacheService.saveAnswerToCache(cacheKey, mcAnswerDto);
        } else if (question instanceof DescriptiveQuestion && dto.getAnswer() != null) {
            DescriptiveAnswerDto descAnswerDto = new DescriptiveAnswerDto(question.getId(), dto.getExamId(), dto.getStudentId(), dto.getAnswer());
            answerCacheService.saveAnswerToCache(cacheKey, descAnswerDto);
        } else {
            return ResponseEntity.badRequest().body("Invalid answer data.");
        }

        return ResponseEntity.ok("Answer saved successfully.");
    }

    @PostMapping("/{examId}/submitExam")
    public ResponseEntity<?> endExam(@PathVariable Long examId, Principal principal) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);

        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(examService.multipleChoiceQuestionsOfExam(examId));
        allQuestions.addAll(examService.descriptiveQuestionsOfExam(examId));

        for (int questionIndex = 1; questionIndex < allQuestions.size()+1; questionIndex++) {
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

        List<ExamQuestion> examQuestions = questionService.findQuestionExamByExam(examId);
        List<Question> allQuestions = examQuestions.stream()
                .map(ExamQuestion::getQuestion)
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
