package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.ResourcesNotFundException;
import ir.maktabsharif.online_exam.model.Course;
import ir.maktabsharif.online_exam.model.Exam;
import ir.maktabsharif.online_exam.model.dto.ExamDto;
import ir.maktabsharif.online_exam.repository.CourseRepository;
import ir.maktabsharif.online_exam.repository.ExamRepository;
import ir.maktabsharif.online_exam.service.ExamService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;

    public ExamServiceImpl(ExamRepository examRepository, CourseRepository courseRepository) {
        this.examRepository = examRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void addExamToCourse(Long courseId, ExamDto examDto) {
        Exam exam = Exam.builder()
                .title(examDto.getTitle())
                .description(examDto.getDescription())
                .examTime(examDto.getExamTime())
                .examDate(examDto.getExamDate())
                .build();
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            course.get().getExams().add(exam);
            exam.setCourse(course.get());
            examRepository.save(exam);
            courseRepository.save(course.get());
        }
    }

    @Override
    public boolean updateExam(Long id, ExamDto examDto) {
        Optional<Exam> exam = examRepository.findById(id);
        if (exam.isPresent()) {
            Exam updatedExam = exam.get();
            updatedExam.setTitle(examDto.getTitle());
            updatedExam.setDescription(examDto.getDescription());
            updatedExam.setExamTime(examDto.getExamTime());
            updatedExam.setExamDate(examDto.getExamDate());
            examRepository.save(updatedExam);
            return true;
        }
        return false;
    }

    @Override
    public Exam findById(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFundException("Exam not found!"));
    }

    @Override
    public boolean deleteExam(Long id) {
        Optional<Exam> exam = examRepository.findById(id);
        if (exam.isPresent()) {
            examRepository.delete(exam.get());
            return true;
        }
        return false;
    }
}
