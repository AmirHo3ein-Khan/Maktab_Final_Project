package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.EntityNotFoundException;
import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.Role;
import ir.maktabsharif.online_exam.model.Student;
import ir.maktabsharif.online_exam.model.dto.StudentDto;
import ir.maktabsharif.online_exam.model.enums.RegisterState;
import ir.maktabsharif.online_exam.repository.CourseRepository;
import ir.maktabsharif.online_exam.repository.RoleRepository;
import ir.maktabsharif.online_exam.repository.StudentRepository;
import ir.maktabsharif.online_exam.service.StudentService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
                              RoleRepository roleRepository, PasswordEncoder passwordEncoder, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.courseRepository = courseRepository;
    }

    @Override
    public void saveStudent(StudentDto studentDto) {
        Role student = roleRepository.findByName("STUDENT").get();
        studentRepository.save(Student.builder()
                .firstName(studentDto.getFirstName())
                .lastName(studentDto.getLastName())
                .username(studentDto.getUsername())
                .password(passwordEncoder.encode(studentDto.getPassword()))
                .email(studentDto.getEmail())
                .dob(studentDto.getDob())
                .role(student)
                .registerState(RegisterState.WAITING)
                .build());
    }

    @Override
    public boolean updateStudent(Long id, StudentDto studentDto) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            Student updatedStudent = student.get();
            updatedStudent.setFirstName(studentDto.getFirstName());
            updatedStudent.setLastName(studentDto.getLastName());
            updatedStudent.setUsername(studentDto.getUsername());
            updatedStudent.setEmail(studentDto.getEmail());
            updatedStudent.setDob(studentDto.getDob());
            studentRepository.save(updatedStudent);
            return true;
        }
        return false;
    }

    @Override
    public boolean checkPassword(Student student, String oldPassword) {
        return passwordEncoder.matches(oldPassword, student.getPassword());
    }

    @Override
    public void changePassword(Student student, String newPassword) {
        student.setPassword(passwordEncoder.encode(newPassword));
        studentRepository.save(student);
    }

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with this id"+ id));
    }


    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public List<Course> coursesOfStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with this id" + studentId));
        return courseRepository.findCoursesByStudent(student);
    }

    @Override
    public Student findByUsername(String username) {
        return studentRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with this username "+ username));
    }
}
