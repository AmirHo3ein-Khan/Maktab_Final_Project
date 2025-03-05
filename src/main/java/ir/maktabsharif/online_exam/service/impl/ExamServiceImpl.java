package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.EntityNotFoundException;
import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.ExamDto;
import ir.maktabsharif.online_exam.repository.*;
import ir.maktabsharif.online_exam.service.ExamService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final MasterRepository masterRepository;
    private final QuestionExamRepository questionExamRepository;

    public ExamServiceImpl(ExamRepository examRepository, CourseRepository courseRepository,
                           MasterRepository masterRepository, QuestionExamRepository questionExamRepository) {
        this.questionExamRepository = questionExamRepository;
        this.courseRepository = courseRepository;
        this.masterRepository = masterRepository;
        this.examRepository = examRepository;
    }

    @Override
    public void addExamToCourse(Long courseId, Long masterId, ExamDto examDto) {
        Master master = masterRepository.findById(masterId)
                .orElseThrow(() -> new EntityNotFoundException("Master not found with this id: " + masterId));
        Exam exam = Exam.builder()
                .title(examDto.getTitle())
                .description(examDto.getDescription())
                .examTime(examDto.getExamTime())
                .examDate(examDto.getExamDate())
                .master(master)
                .build();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with this id: " + courseId));

        exam.setCourse(course);
        examRepository.save(exam);
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
                .orElseThrow(() -> new EntityNotFoundException("Exam not found with this id:" + id));
    }

    @Override
    public boolean deleteExam(Long id) {
        Optional<Exam> exam = examRepository.findById(id);
        if (exam.isPresent()) {
            questionExamRepository.deleteAll(questionExamRepository.findAllQuestionExamByExamId(exam.get().getId()));
            examRepository.delete(exam.get());

            return true;
        }
        return false;
    }

    @Override
    public List<MultipleChoiceQuestion> multipleChoiceQuestionsOfExam(Long examId) {
        List<Question> questions = questionExamRepository.findQuestionsByExamId(examId);
        List<MultipleChoiceQuestion> multipleChoiceQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (question instanceof MultipleChoiceQuestion) {
                multipleChoiceQuestions.add((MultipleChoiceQuestion) question);
            }
        }
        return multipleChoiceQuestions;
    }

    @Override
    public List<DescriptiveQuestion> descriptiveQuestionsOfExam(Long examId) {
        List<Question> questions = questionExamRepository.findQuestionsByExamId(examId);
        List<DescriptiveQuestion> descriptiveQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (question instanceof DescriptiveQuestion) {
                descriptiveQuestions.add((DescriptiveQuestion) question);
            }
        }
        return descriptiveQuestions;
    }

}
