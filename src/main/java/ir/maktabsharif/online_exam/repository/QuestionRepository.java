package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("SELECT q FROM Question q WHERE q.course IS NULL AND q.questionExams IS EMPTY")
    List<Question> findQuestionsWithNoCourseAndNoExams();
}
