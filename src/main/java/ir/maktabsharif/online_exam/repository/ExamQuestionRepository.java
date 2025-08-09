package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Exam;
import ir.maktabsharif.online_exam.model.Question;
import ir.maktabsharif.online_exam.model.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    Optional<ExamQuestion> findByExamAndQuestion(Exam exam, Question question);
    @Query("SELECT qe.question FROM ExamQuestion qe WHERE qe.exam.id = :examId")
    List<Question> findQuestionsByExamId(@Param("examId") Long examId);
    boolean existsByExamAndQuestion(Exam exam, Question question);
    List<ExamQuestion> findAllByExam(Exam exam);
    List<ExamQuestion> findByExam(Exam exam);
}
