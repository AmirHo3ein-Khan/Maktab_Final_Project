package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.ExamDto;

import java.util.List;

public interface ExamService {
    void addExamToCourse(Long courseId, Long masterId , ExamDto examDto);
    boolean updateExam(Long id , ExamDto examDto);
    Exam findById(Long id);
    boolean deleteExam(Long id);
    List<MultipleChoiceQuestion> multipleChoiceQuestionsOfExam(Long examId);
    List<DescriptiveQuestion> descriptiveQuestionsOfExam(Long examId);
}
