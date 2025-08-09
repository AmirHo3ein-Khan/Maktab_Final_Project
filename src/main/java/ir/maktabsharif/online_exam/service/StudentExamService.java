package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.StudentExam;
import ir.maktabsharif.online_exam.model.dto.response.ExamResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.StudentExamResponseDTO;

import java.util.List;
import java.util.Map;

public interface StudentExamService {
    StudentExamResponseDTO startExam(Long examId , Long studentId);
    void submitExam(Long studentId, Long examId);
    StudentExam findStudentExam(Long studentId, Long examId);
    List<ExamResponseDto> findCompletedExamsOfStudent(Long studentId);
}
