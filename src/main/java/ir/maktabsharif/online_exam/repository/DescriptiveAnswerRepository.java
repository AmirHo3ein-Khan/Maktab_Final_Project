package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescriptiveAnswerRepository extends JpaRepository<DescriptiveAnswer , Long> {
    List<DescriptiveAnswer> findByStudentAndExamQuestion(Student student, ExamQuestion examQuestion);
}
