package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.Student;
import ir.maktabsharif.online_exam.model.dto.StudentDto;

import java.util.List;

public interface StudentService {
    void studentRegister(StudentDto studentDto);
    boolean updateStudent(Long id , StudentDto studentDto);
    boolean checkPassword(Student student, String oldPassword);
    void changePassword(Student student , String newPassword);
    Student findById(Long id);
    List<Student> findAll();
    List<Course> coursesOfStudent(Long studentId);
    Student findByUsername(String username);

}
