package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Student;
import ir.maktabsharif.online_exam.model.dto.AddCourseToStudentDto;
import ir.maktabsharif.online_exam.model.dto.StudentDto;

import java.util.List;

public interface StudentService {
    void saveStudent(StudentDto studentDto);
    boolean updateStudent(Long id , StudentDto studentDto);
    Student findById(Long id);
    List<Student> findAll();

}
