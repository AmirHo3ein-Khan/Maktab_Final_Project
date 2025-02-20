package ir.maktabsharif.online_exam.repository;

import ir.maktabsharif.online_exam.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.master LEFT JOIN FETCH c.students WHERE c.id = :courseId")
    Course findCourseWithDetails(@Param("courseId") Long courseId);
}
