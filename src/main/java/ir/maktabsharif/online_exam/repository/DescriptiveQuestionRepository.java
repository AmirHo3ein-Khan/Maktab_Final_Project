package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.DescriptiveQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescriptiveQuestionRepository extends JpaRepository<DescriptiveQuestion, Long> {
}
