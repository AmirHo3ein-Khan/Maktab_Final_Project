package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Exam;
import ir.maktabsharif.online_exam.model.StudentExam;
import ir.maktabsharif.online_exam.model.dto.StudentExamDto;
import ir.maktabsharif.online_exam.model.dto.response.ExamResponseDto;

import java.util.List;
import java.util.Optional;

public interface StudentExamService {
    StudentExam startExam(StudentExamDto dto);
    void submitExam(Long studentId, Long examId);
    StudentExam findStudentExam(Long studentId, Long examId);
    List<ExamResponseDto> findCompletedExamsOfStudent(Long studentId);

}
