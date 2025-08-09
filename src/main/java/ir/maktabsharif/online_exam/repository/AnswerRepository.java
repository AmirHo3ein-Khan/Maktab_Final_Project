package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Answer;
import ir.maktabsharif.online_exam.model.ExamQuestion;
import ir.maktabsharif.online_exam.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer , Long> {
    Optional<Answer> findByExamQuestion(ExamQuestion examQuestion);
    Optional<Answer> findByExamQuestionAndStudent(ExamQuestion examQuestion, Student student);
    List<Answer> findByStudentAndExamQuestion(Student student , ExamQuestion examQuestion);
    List<Answer> findAllByExamQuestion(ExamQuestion examQuestion);
}
