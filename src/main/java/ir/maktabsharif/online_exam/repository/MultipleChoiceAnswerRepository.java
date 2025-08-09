package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.MultipleChoiceAnswer;
import ir.maktabsharif.online_exam.model.ExamQuestion;
import ir.maktabsharif.online_exam.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleChoiceAnswerRepository extends JpaRepository<MultipleChoiceAnswer , Long> {
    List<MultipleChoiceAnswer> findByStudentAndExamQuestion(Student student, ExamQuestion examQuestion);
}
