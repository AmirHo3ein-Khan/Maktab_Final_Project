package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.dto.CourseDto;

import java.util.List;

public interface CourseService {
    void createCourse(CourseDto courseDto);
    List<Course> findAll();
    void addStudentToCourse(Long courseId , Long studentId);
    void deleteStudentFromCourse(Long courseId , Long studentId);
    void addMasterToCourse(Long courseId , Long masterId);
    Course findDetailsOfCourse(Long courseId);
    Course findCourseStudents(Long courseId);
    Course findCourseExams(Long courseId);

}
