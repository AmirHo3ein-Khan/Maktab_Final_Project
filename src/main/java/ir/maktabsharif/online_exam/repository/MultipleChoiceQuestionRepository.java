package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.MultipleChoiceQuestion;
import ir.maktabsharif.online_exam.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MultipleChoiceQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {
}
