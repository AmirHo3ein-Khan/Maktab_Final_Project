package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.DescriptiveQuestion;
import ir.maktabsharif.online_exam.model.MultipleChoiceQuestion;
import ir.maktabsharif.online_exam.model.Question;
import ir.maktabsharif.online_exam.model.dto.questiondto.*;
import ir.maktabsharif.online_exam.model.dto.response.ApiResponseDto;
import ir.maktabsharif.online_exam.service.CourseService;
import ir.maktabsharif.online_exam.service.ExamService;
import ir.maktabsharif.online_exam.service.MasterService;
import ir.maktabsharif.online_exam.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    private final ExamService examService;
    private final MasterService masterService;
    private final CourseService courseService;
    private final QuestionService questionService;

    public QuestionController(ExamService examService, MasterService masterService, CourseService courseService, QuestionService questionService) {
        this.examService = examService;
        this.masterService = masterService;
        this.courseService = courseService;
        this.questionService = questionService;
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/{courseId}/{examId}/multipleQuestion")
    public ResponseEntity<ApiResponseDto> createMCQ(@PathVariable Long courseId, @PathVariable Long examId,
                                                    @RequestBody MultipleChoiceQuestionDto multipleChoiceQuestionDto) {
        questionService.createMultipleChoiceQuestion(courseId, examId, multipleChoiceQuestionDto);
        return ResponseEntity.ok(new ApiResponseDto("multiple.question.created.success", true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/{courseId}/{examId}/descriptiveQuestion")
    public ResponseEntity<ApiResponseDto> createDescriptiveQuestion(@PathVariable Long courseId, @PathVariable Long examId,
                                                                    @RequestBody DescriptiveQuestionDto descriptiveQuestionDto) {
        questionService.createDescriptiveQuestion(courseId, examId, descriptiveQuestionDto);
        return ResponseEntity.ok(new ApiResponseDto("descriptive.question.created.success", true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/{courseId}/bank/questions")
    private ResponseEntity<List<Question>> questionBankForDeleteOrUpdate(@PathVariable Long courseId, Model model) {
        List<MultipleChoiceQuestion> multipleChoiceQuestions = courseService.mcqBank(courseId);
        List<DescriptiveQuestion> descriptiveQuestions = courseService.dqBank(courseId);
        List<Question> questions = new ArrayList<>();
        questions.addAll(multipleChoiceQuestions);
        questions.addAll(descriptiveQuestions);
        return ResponseEntity.ok(questions);
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/bank/questions/exam/assign")
    public ResponseEntity<ApiResponseDto> addQuestionToExam(
            @RequestBody AddQuestionToExamDto addQuestionToExamDto) {
        questionService.addQuestionToExam(addQuestionToExamDto);
        return ResponseEntity.ok(new ApiResponseDto("question.exam.added.success", true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/delete/question/exam")
    public ResponseEntity<ApiResponseDto> deleteQuestionFromExam(
            @RequestBody DeleteQuestionFromExamDto deleteQuestionFromExamDto) {
        questionService.deleteQuestionFromExam(deleteQuestionFromExamDto);
        return ResponseEntity.ok(new ApiResponseDto("question.exam.deleted.success", true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @DeleteMapping("/delete/question/bank")
    public ResponseEntity<ApiResponseDto> deleteQuestionFromQuestionBank(
            @RequestBody DeleteQuestionFromQuestionBankDto dto) {
        courseService.deleteQuestionFromQuestionBank(dto);
        return ResponseEntity.ok(new ApiResponseDto("bank.question.deleted.success", true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/mcq/update")
    public ResponseEntity<ApiResponseDto> updateMCQ(@RequestBody UpdateMCQDto dto) {
        questionService.updateMultipleChoiceQuestion(dto);
        return ResponseEntity.ok(new ApiResponseDto("multiple.question.updated.success", true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/descriptive/update")
    public ResponseEntity<ApiResponseDto> updateDescriptiveQuestion(@RequestBody UpdateDQBankDto dto) {
        questionService.updateDescriptiveQuestion(dto);
        return ResponseEntity.ok(new ApiResponseDto("descriptive.question.updated.success", true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/{courseId}/bank/mcq/assign")
    public ResponseEntity<ApiResponseDto> createMCQForBank(@PathVariable Long courseId,
                                   @ModelAttribute MultipleChoiceQuestionDto multipleChoiceQuestionDto) {
        questionService.createMultipleChoiceQuestionForBank(courseId, multipleChoiceQuestionDto);
        return  ResponseEntity.ok(new ApiResponseDto("multiple.question.created.bank.success", true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/{courseId}/bank/descriptive/assign")
    public ResponseEntity<ApiResponseDto> createDescriptiveQuestionForBank(@PathVariable Long courseId,
                                                   @ModelAttribute DescriptiveQuestionDto descriptiveQuestionDto) {
        questionService.createDescriptiveQuestionForBank(courseId, descriptiveQuestionDto);
        return ResponseEntity.ok(new ApiResponseDto("descriptive.question.created.bank.success", true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/{questionId}/delete/question/trash")
    public ResponseEntity<ApiResponseDto> deleteQuestionFromTrash(@PathVariable Long questionId) {
        questionService.deletedQuestionFromTrash(questionId);
        return ResponseEntity.ok(new ApiResponseDto("trash.question.deleted.success", true));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/{questionId}/{courseId}/add/question/trash")
    public ResponseEntity<ApiResponseDto> addQuestionFronTrashToBank(@PathVariable Long questionId , @PathVariable Long courseId) {
        questionService.addQuestionFromTrashToBank(questionId , courseId);
        return ResponseEntity.ok(new ApiResponseDto("trash.question.added.success", true));
    }
}
