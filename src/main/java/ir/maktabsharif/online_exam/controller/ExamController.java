package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.DescriptiveQuestion;
import ir.maktabsharif.online_exam.model.MultipleChoiceQuestion;
import ir.maktabsharif.online_exam.model.Question;
import ir.maktabsharif.online_exam.model.dto.ExamDto;
import ir.maktabsharif.online_exam.model.dto.response.ApiResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.ExamDetailsDto;
import ir.maktabsharif.online_exam.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/exam")
public class ExamController {
    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/exam/course/assign")
    public ResponseEntity<ApiResponseDto> createExam(@RequestParam("masterId") Long masterId,
                                                     @RequestParam("courseId") Long courseId,
                                                     @Valid @RequestBody ExamDto examDto) {
        examService.createExamForCourse(courseId, masterId, examDto);
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

    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/{courseId}/{examId}/questions/exam")
    public ResponseEntity<List<Question>> getExamQuestions(@PathVariable Long courseId, @PathVariable Long examId) {
        List<DescriptiveQuestion> descriptiveQuestions = examService.descriptiveQuestionsOfExam(examId);
        List<MultipleChoiceQuestion> multipleChoiceQuestions = examService.multipleChoiceQuestionsOfExam(examId);
        List<Question> questions = new ArrayList<>();
        questions.addAll(multipleChoiceQuestions);
        questions.addAll(descriptiveQuestions);
        return ResponseEntity.ok(questions);
    }

    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/{examId}/exam/details")
    private ResponseEntity<ExamDetailsDto> examDetails(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.examDetails(examId));
    }

}
