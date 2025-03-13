package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.controller.QuestionController;
import ir.maktabsharif.online_exam.model.Answer;
import ir.maktabsharif.online_exam.model.Option;
import ir.maktabsharif.online_exam.model.QuestionExam;
import ir.maktabsharif.online_exam.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer , Long> {
    Optional<Answer> findByQuestionExam(QuestionExam questionExam);
    Optional<Answer> findByQuestionExamAndStudent(QuestionExam questionExam , Student student);
    List<Answer> findByStudentAndQuestionExam(Student student , QuestionExam questionExam);
    List<Answer> findAllByQuestionExam(QuestionExam questionExam);
}
