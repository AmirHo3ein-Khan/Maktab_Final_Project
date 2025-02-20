package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.ResourcesNotFundException;
import ir.maktabsharif.online_exam.model.Student;
import ir.maktabsharif.online_exam.model.dto.StudentDto;
import ir.maktabsharif.online_exam.model.enums.RegisterState;
import ir.maktabsharif.online_exam.model.enums.UserType;
import ir.maktabsharif.online_exam.repository.StudentRepository;
import ir.maktabsharif.online_exam.service.StudentService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveStudent(StudentDto studentDto) {
        studentRepository.save(Student.builder()
                .firstName(studentDto.getFirstName())
                .lastName(studentDto.getLastName())
                .username(studentDto.getUsername())
                .password(passwordEncoder.encode(studentDto.getPassword()))
                .email(studentDto.getEmail())
                .dob(studentDto.getDob())
                .userType(UserType.STUDENT)
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
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFundException("Student not found!"));
    }


    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }
}
