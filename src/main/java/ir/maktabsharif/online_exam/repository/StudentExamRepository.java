package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Exam;
import ir.maktabsharif.online_exam.model.Student;
import ir.maktabsharif.online_exam.model.StudentExam;
import ir.maktabsharif.online_exam.model.dto.StudentExamDto;
import ir.maktabsharif.online_exam.model.enums.ExamState;
import ir.maktabsharif.online_exam.model.enums.StudentExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam , Long> {
    Optional<StudentExam> findByStudentAndExam(Student student , Exam exam);
    List<StudentExam> findByStudentAndStudentExamStatus(Student student, StudentExamStatus state);
    List<StudentExam> findAllByExam(Exam exam);
}
