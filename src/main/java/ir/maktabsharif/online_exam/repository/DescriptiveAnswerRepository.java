package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DescriptiveAnswerRepository extends JpaRepository<DescriptiveAnswer , Long> {
    List<DescriptiveAnswer> findByStudentAndQuestionExam(Student student, QuestionExam questionExam);
}
