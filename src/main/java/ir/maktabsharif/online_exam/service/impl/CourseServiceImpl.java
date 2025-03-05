package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.EntityNotFoundException;
import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.*;
import ir.maktabsharif.online_exam.model.dto.questiondto.DeleteQuestionFromQuestionBankDto;
import ir.maktabsharif.online_exam.repository.*;
import ir.maktabsharif.online_exam.service.CourseService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final MasterRepository masterRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;

    public CourseServiceImpl(CourseRepository courseRepository, MasterRepository masterRepository,
                             StudentRepository studentRepository, ExamRepository examRepository,
                             QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.masterRepository = masterRepository;
        this.examRepository = examRepository;
    }


    @Override
    public void createCourse(CourseRequestDto courseRequestDto) {
        courseRepository.save(Course.builder()
                .title(courseRequestDto.getTitle())
                .unit(courseRequestDto.getUnit())
                .courseStartedDate(courseRequestDto.getCourseStartedDate())
                .courseFinishedDate(courseRequestDto.getCourseFinishedDate())
                .build());
    }

    @Override
    public void updateCourse(Long id, CourseRequestDto courseRequestDto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Course with this id not found :"+ id));
        course.setTitle(courseRequestDto.getTitle());
        course.setUnit(courseRequestDto.getUnit());
        course.setCourseStartedDate(courseRequestDto.getCourseStartedDate());
        course.setCourseFinishedDate(courseRequestDto.getCourseFinishedDate());
        courseRepository.save(course);
    }

    @Override
    public void updateMasterOfCourse(UpdateMasterOfCourseDto updateMasterOfCourseDto) {
        Course course = courseRepository.findById(updateMasterOfCourseDto.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course with this id not found :" + updateMasterOfCourseDto.getCourseId()));

        Master master = masterRepository.findById(updateMasterOfCourseDto.getMasterId())
                .orElseThrow(() -> new EntityNotFoundException("Master with this id not found :" + updateMasterOfCourseDto.getMasterId()));

        course.setMaster(master);
        courseRepository.save(course);

    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Course with this id not found :"+ id));
        List<Exam> exams = course.getExams();
        List<Question> questions = course.getQuestions();
        questionRepository.deleteAll(questions);
        examRepository.deleteAll(exams);
        courseRepository.delete(course);
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
    @Override
    public void addStudentToCourse(AddStudentToCourseDto addStudentToCourseDto) {

        Course course = courseRepository.findById(addStudentToCourseDto.getCourseId())
                .orElseThrow(()-> new EntityNotFoundException("Course with this id not found :"+ addStudentToCourseDto.getCourseId()));

        Student student = studentRepository.findById(addStudentToCourseDto.getStudentId())
                .orElseThrow(()-> new EntityNotFoundException("Student with this id not found :"+ addStudentToCourseDto.getStudentId()));

            course.getStudents().add(student);
            student.getCourses().add(course);
            studentRepository.save(student);
    }

    @Override
    public void deleteStudentFromCourse(DeleteStudentFromCourseDto deleteStudentFromCourseDto) {
        Course course = courseRepository.findById(deleteStudentFromCourseDto.getCourseId())
                .orElseThrow(()-> new EntityNotFoundException("Course with this id not found :"+ deleteStudentFromCourseDto.getCourseId()));

        Student student = studentRepository.findById(deleteStudentFromCourseDto.getStudentId())
                .orElseThrow(()-> new EntityNotFoundException("Student with this id not found :"+ deleteStudentFromCourseDto.getStudentId()));

            course.getStudents().remove(student);
            student.getCourses().remove(course);
            studentRepository.save(student);
    }

    @Override
    public void addMasterToCourse(AddMasterToCourseDto addMasterToCourseDto) {
       Course course = courseRepository.findById(addMasterToCourseDto.getCourseId())
               .orElseThrow(()-> new EntityNotFoundException("Course with this id not found :"+ addMasterToCourseDto.getCourseId()));

       Master master = masterRepository.findById(addMasterToCourseDto.getMasterId())
               .orElseThrow(()-> new EntityNotFoundException("Master with this id not found :"+ addMasterToCourseDto.getMasterId()));

            course.setMaster(master);
            master.getCourses().add(course);
            masterRepository.save(master);

    }

    @Override
    public Course findById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with this id not found :" + courseId));
    }

    @Override
    public List<MultipleChoiceQuestion> mcqBank(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with this id not found :" + courseId));
        List<Question> questions = course.getQuestions();
        List<MultipleChoiceQuestion> multipleChoiceQuestions = new ArrayList<>();
        for (Question question:questions){
            if (question instanceof MultipleChoiceQuestion){
                multipleChoiceQuestions.add((MultipleChoiceQuestion) question);
            }
        }
        return multipleChoiceQuestions;
    }

    @Override
    public List<DescriptiveQuestion> dqBank(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with this id not found :" + courseId));
        List<Question> questions = course.getQuestions();
        List<DescriptiveQuestion> descriptiveQuestions = new ArrayList<>();
        for (Question question:questions){
            if (question instanceof DescriptiveQuestion){
                descriptiveQuestions.add((DescriptiveQuestion) question);
            }
        }
        return descriptiveQuestions;
    }

    @Override
    public void deleteQuestionFromQuestionBank(DeleteQuestionFromQuestionBankDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course with this id not found :" + dto.getCourseId()));
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found :" + dto.getQuestionId()));


        course.getQuestions().remove(question);
        question.setCourse(null);
        courseRepository.save(course);
        questionRepository.save(question);
    }




}
