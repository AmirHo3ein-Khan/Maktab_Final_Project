package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.Master;
import ir.maktabsharif.online_exam.model.Student;
import ir.maktabsharif.online_exam.model.dto.CourseDto;
import ir.maktabsharif.online_exam.repository.CourseRepository;
import ir.maktabsharif.online_exam.repository.MasterRepository;
import ir.maktabsharif.online_exam.repository.StudentRepository;
import ir.maktabsharif.online_exam.service.CourseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final MasterRepository masterRepository;
    private final StudentRepository studentRepository;

    public CourseServiceImpl(CourseRepository courseRepository, MasterRepository masterRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.masterRepository = masterRepository;
        this.studentRepository = studentRepository;
    }


    @Override
    public void createCourse(CourseDto courseDto) {
        courseRepository.save(Course.builder()
                .title(courseDto.getTitle())
                .unit(courseDto.getUnit())
                .courseStartedDate(courseDto.getCourseStartedDate())
                .courseFinishedDate(courseDto.getCourseFinishedDate())
                .build());
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
    @Override
    public void addStudentToCourse(Long courseId , Long studentId) {
        Optional<Course> course = courseRepository.findById(courseId);
        Optional<Student> student = studentRepository.findById(studentId);
        if (course.isPresent() && student.isPresent()){
            Course updateCourse = course.get();
            Student updateStudent = student.get();

            updateCourse.getStudents().add(updateStudent);
            courseRepository.save(updateCourse);

            updateStudent.getCourses().add(updateCourse);
            studentRepository.save(updateStudent);
        }
    }

    @Override
    public void deleteStudentFromCourse(Long courseId, Long studentId) {
        Optional<Course> course = courseRepository.findById(courseId);
        Optional<Student> student = studentRepository.findById(studentId);
        if (course.isPresent() && student.isPresent()){
            Course updateCourse = course.get();
            Student updateStudent = student.get();

            updateCourse.getStudents().remove(updateStudent);
            courseRepository.save(updateCourse);

            updateStudent.getCourses().remove(updateCourse);
            studentRepository.save(updateStudent);
        }
    }

    @Override
    public void addMasterToCourse(Long courseId , Long masterId) {
        Optional<Course> course = courseRepository.findById(courseId);
        Optional<Master> master = masterRepository.findById(masterId);
        if (course.isPresent() && master.isPresent()){
            Course updateCourse = course.get();
            Master updateMaster = master.get();

            updateMaster.getCourses().add(updateCourse);
            masterRepository.save(updateMaster);

            updateCourse.setMaster(updateMaster);
            courseRepository.save(updateCourse);
        }
    }

    @Override
    public Course findDetailsOfCourse(Long courseId) {
        return courseRepository.findCourseWithDetails(courseId);
    }
}
