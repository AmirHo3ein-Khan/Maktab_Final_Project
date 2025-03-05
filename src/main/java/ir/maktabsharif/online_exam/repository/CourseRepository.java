package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.Master;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByMaster(Master master);
}
