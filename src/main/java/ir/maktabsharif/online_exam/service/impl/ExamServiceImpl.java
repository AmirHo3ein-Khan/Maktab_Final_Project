package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.EntityNotFoundException;
import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.ExamDto;
import ir.maktabsharif.online_exam.model.dto.request.ExamRequestDto;
import ir.maktabsharif.online_exam.model.dto.response.ExamDetailsDto;
import ir.maktabsharif.online_exam.model.dto.response.ExamResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.OptionResponseDto;
import ir.maktabsharif.online_exam.model.dto.response.QuestionResponseDto;
import ir.maktabsharif.online_exam.model.enums.ExamState;
import ir.maktabsharif.online_exam.repository.*;
import ir.maktabsharif.online_exam.service.ExamService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final MasterRepository masterRepository;
    private final QuestionExamRepository questionExamRepository;
    private final StudentExamRepository studentExamRepository;
    private final AnswerRepository answerRepository;

    public ExamServiceImpl(ExamRepository examRepository, CourseRepository courseRepository,
                           MasterRepository masterRepository, QuestionExamRepository questionExamRepository, StudentExamRepository studentExamRepository, AnswerRepository answerRepository) {
        this.questionExamRepository = questionExamRepository;
        this.studentExamRepository = studentExamRepository;
        this.courseRepository = courseRepository;
        this.masterRepository = masterRepository;
        this.examRepository = examRepository;
        this.answerRepository = answerRepository;
    }

    @Override
    public void createExamForCourse(ExamRequestDto examRequestDto) {
        Master master = masterRepository.findById(examRequestDto.getMasterId())
                .orElseThrow(() -> new EntityNotFoundException("Master not found with this id: " + examRequestDto.getMasterId()));

        LocalDate today = LocalDate.now();
        LocalDate examDate = examRequestDto.getExamDate();
        ExamState examState;

        if (examDate.isAfter(today)) {
            examState = ExamState.NOT_STARTED;
        } else if (examDate.isEqual(today)) {
            examState = ExamState.STARTED;
        } else {
            examState = ExamState.FINISHED;
        }

        Exam exam = Exam.builder()
                .title(examRequestDto.getTitle())
                .description(examRequestDto.getDescription())
                .examTime(examRequestDto.getExamTime())
                .examDate(examRequestDto.getExamDate())
                .examState(examState)
                .master(master)
                .build();
        Course course = courseRepository.findById(examRequestDto.getCourseId())
                .orElseThrow(() -> new EntityNotFoundException("Course not found with this id: " + examRequestDto.getCourseId()));

        exam.setCourse(course);
        examRepository.save(exam);
    }

    @Override
    public boolean updateExam(Long id, ExamDto examDto) {
        Optional<Exam> exam = examRepository.findById(id);
        if (exam.isPresent()) {
            LocalDate today = LocalDate.now();
            LocalDate examDate = examDto.getExamDate();
            ExamState examState;

            if (examDate.isAfter(today)) {
                examState = ExamState.NOT_STARTED;
            } else if (examDate.isEqual(today)) {
                examState = ExamState.STARTED;
            } else {
                examState = ExamState.FINISHED;
            }
            Exam updatedExam = exam.get();
            updatedExam.setTitle(examDto.getTitle());
            updatedExam.setDescription(examDto.getDescription());
            updatedExam.setExamTime(examDto.getExamTime());
            updatedExam.setExamDate(examDto.getExamDate());
            updatedExam.setExamState(examState);
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
            List<QuestionExam> foundedQuestionExamByExam = questionExamRepository.findAllByExam(exam.get());
            for (QuestionExam questionExam : foundedQuestionExamByExam){
                answerRepository.deleteAll(answerRepository.findAllByQuestionExam(questionExam));
            }
            questionExamRepository.deleteAll(foundedQuestionExamByExam);
            studentExamRepository.deleteAll(studentExamRepository.findAllByExam(exam.get()));
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

    public ExamDetailsDto examDetails(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found with this id:" + examId));
        List<DescriptiveQuestion> descriptiveQuestions = descriptiveQuestionsOfExam(examId);
        List<MultipleChoiceQuestion> multipleChoiceQuestions = multipleChoiceQuestionsOfExam(examId);
        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(multipleChoiceQuestions);
        allQuestions.addAll(descriptiveQuestions);

        return ExamDetailsDto.builder()
                .examTitle(exam.getTitle())
                .examDescription(exam.getDescription())
                .examDate(exam.getExamDate())
                .examTime(exam.getExamTime())
                .courseName(exam.getCourse().getTitle())
                .numberOfQuestions(allQuestions.size())
                .build();
    }

    @Override
    public List<QuestionResponseDto> getQuestionsOfExam(Long examId) {
        List<DescriptiveQuestion> descriptiveQuestions = descriptiveQuestionsOfExam(examId);
        List<MultipleChoiceQuestion> multipleChoiceQuestions = multipleChoiceQuestionsOfExam(examId);

        List<QuestionResponseDto> questions = new ArrayList<>();

        for (DescriptiveQuestion question : descriptiveQuestions) {
            questions.add(
                    QuestionResponseDto.builder()
                            .title(question.getTitle())
                            .questionText(question.getQuestionText())
                            .defaultScore(question.getDefaultScore())
                            .build()
            );
        }

        for (MultipleChoiceQuestion question : multipleChoiceQuestions) {
            List<OptionResponseDto> optionDtoList = question.getOptions()
                    .stream()
                    .map(option -> OptionResponseDto.builder()
                            .optionText(option.getOptionText())
                            .isCorrect(option.isCorrect())
                            .build()
                    )
                    .collect(Collectors.toList());

            questions.add(
                    QuestionResponseDto.builder()
                            .title(question.getTitle())
                            .questionText(question.getQuestionText())
                            .defaultScore(question.getDefaultScore())
                            .options(optionDtoList)
                            .build()
            );
        }

        return questions;
    }


    @Override
    public List<ExamResponseDto> getCourseExams(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with this id:" + courseId));
        List<Exam> exams = course.getExams();
        List<ExamResponseDto> examDtoList = new ArrayList<>();
        for (Exam exam : exams){
            examDtoList.add(ExamResponseDto.builder()
                    .examTitle(exam.getTitle())
                    .numberOfQuestions(exam.getQuestionExams().size())
                    .examDescription(exam.getDescription())
                    .examDescription(exam.getDescription())
                    .examDate(exam.getExamDate())
                    .examTime(exam.getExamTime())
                    .courseName(course.getTitle())
                    .build());
        }
        return examDtoList;
    }
}
