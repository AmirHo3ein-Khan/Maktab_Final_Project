package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Exam;
import ir.maktabsharif.online_exam.model.StudentExam;
import ir.maktabsharif.online_exam.model.dto.StudentExamDto;

import java.util.List;
import java.util.Optional;

public interface StudentExamService {
    void startExam(StudentExamDto dto);
    void submitExam(Long studentId, Long examId);
    Optional<StudentExam> findStudentExam(Long studentId, Long examId);
    List<Exam> findCompletedExamsOfStudent(Long studentId);

}
