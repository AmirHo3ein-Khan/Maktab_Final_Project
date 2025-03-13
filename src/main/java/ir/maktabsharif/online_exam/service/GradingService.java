package ir.maktabsharif.online_exam.service;

import java.util.Map;

public interface GradingService {
    void autoMultipleChoiceGrading(Long examId, Long questionId);
    void gradingDescriptiveAnswer(Long questionId , Long examId , Double score);
    Map<Long, Double> getAnswerGrades(Long studentId, Long examId);
    Double getMaximumScoreForAnswer(Long examId, Long questionId);
    void setTotalScoreOfExamForStudent(Long studentId, Long examId);
    Double getScoreOfStudentInExam(Long studentId, Long examId);
}
