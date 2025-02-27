package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Exam;
import ir.maktabsharif.online_exam.model.dto.ExamDto;

public interface ExamService {
    void addExamToCourse(Long courseId , ExamDto examDto);
    boolean updateExam(Long id , ExamDto examDto);
    Exam findById(Long id);
    boolean deleteExam(Long id);
}
