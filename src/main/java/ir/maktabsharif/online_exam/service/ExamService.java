package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.ExamDto;
import ir.maktabsharif.online_exam.model.dto.request.ExamRequestDto;
import ir.maktabsharif.online_exam.model.dto.response.ExamDetailsDto;
import ir.maktabsharif.online_exam.model.dto.response.ExamResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.QuestionResponseDto;

import java.util.List;

public interface ExamService {
    void createExamForCourse(ExamRequestDto examRequestDto);
    boolean updateExam(Long id , ExamDto examDto);
    Exam findById(Long id);
    boolean deleteExam(Long id);
    List<MultipleChoiceQuestion> multipleChoiceQuestionsOfExam(Long examId);
    List<DescriptiveQuestion> descriptiveQuestionsOfExam(Long examId);
    ExamDetailsDto examDetails(Long examId);
    List<QuestionResponseDto> getQuestionsOfExam(Long examId);
    List<ExamResponseDto> getCourseExams(Long courseId);
}
