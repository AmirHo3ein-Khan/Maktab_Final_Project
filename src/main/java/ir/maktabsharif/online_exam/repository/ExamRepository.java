package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam , Long> {
}
