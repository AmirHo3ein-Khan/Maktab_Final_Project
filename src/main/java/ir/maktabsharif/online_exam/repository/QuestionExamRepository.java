package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Exam;
import ir.maktabsharif.online_exam.model.Question;
import ir.maktabsharif.online_exam.model.QuestionExam;
import ir.maktabsharif.online_exam.service.QuestionService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionExamRepository extends JpaRepository<QuestionExam , Long> {
    Optional<QuestionExam> findByExamAndQuestion(Exam exam, Question question);
    @Query("SELECT qe.question FROM QuestionExam qe WHERE qe.exam.id = :examId")
    List<Question> findQuestionsByExamId(@Param("examId") Long examId);
    boolean existsByExamAndQuestion(Exam exam, Question question);
    List<QuestionExam> findAllByExam( Exam exam);
    List<QuestionExam> findByExam(Exam exam);
}
