package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
