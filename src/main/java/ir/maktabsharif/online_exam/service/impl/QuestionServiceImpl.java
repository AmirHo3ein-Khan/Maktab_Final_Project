package ir.maktabsharif.online_exam.service.impl;


import ir.maktabsharif.online_exam.exception.EntityNotFoundException;
import ir.maktabsharif.online_exam.exception.QuestionAlreadyExistsInExamException;
import ir.maktabsharif.online_exam.exception.QuestionNotFoundInExamException;
import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.questiondto.*;
import ir.maktabsharif.online_exam.repository.*;
import ir.maktabsharif.online_exam.service.QuestionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    private final DescriptiveQuestionRepository descriptiveQuestionRepository;
    private final ExamQuestionRepository examQuestionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository, ExamRepository examRepository,
                               CourseRepository courseRepository, MultipleChoiceQuestionRepository multipleChoiceQuestionRepository,
                               DescriptiveQuestionRepository descriptiveQuestionRepository, ExamQuestionRepository examQuestionRepository) {
        this.multipleChoiceQuestionRepository = multipleChoiceQuestionRepository;
        this.descriptiveQuestionRepository = descriptiveQuestionRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.examRepository = examRepository;
    }

    public void createMultipleChoiceQuestion(Long examId, MultipleChoiceQuestionDto dto) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + examId));

        Course course = courseRepository.findById(exam.getCourse().getId())
                .orElseThrow(() -> new EntityNotFoundException("Course with this id not found: " + exam.getCourse().getId()));

        MultipleChoiceQuestion multipleChoiceQuestion = MultipleChoiceQuestion.builder()
                .questionText(dto.getQuestionText())
                .title(dto.getTitle())
                .course(course)
                .defaultScore(dto.getDefaultScore())
                .options(new ArrayList<>())
                .examQuestions(new ArrayList<>())
                .build();


        List<Option> options = dto.getOptions().stream()
                .map(optionDto -> {
                    Option option = new Option();
                    option.setOptionText(optionDto.getOptionText());
                    option.setCorrect(optionDto.isCorrect());
                    option.setQuestion(multipleChoiceQuestion);
                    return option;
                }).toList();


        multipleChoiceQuestion.getOptions().addAll(options);
        multipleChoiceQuestionRepository.save(multipleChoiceQuestion);

        ExamQuestion examQuestion = ExamQuestion.builder()
                .exam(exam)
                .question(multipleChoiceQuestion)
                .build();

        examQuestionRepository.save(examQuestion);

        calculateTotalScore(exam);
        examRepository.save(exam);
    }

    @Override
    public void createDescriptiveQuestion(Long examId, DescriptiveQuestionDto dto) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found with this id: " + examId));

        Course course = courseRepository.findById(exam.getCourse().getId())
                .orElseThrow(() -> new EntityNotFoundException("Course with this id not found: " + exam.getCourse().getId()));

        DescriptiveQuestion descriptiveQuestion = DescriptiveQuestion.builder()
                .questionText(dto.getQuestionText())
                .title(dto.getTitle())
                .defaultScore(dto.getScore())
                .course(course)
                .examQuestions(new ArrayList<>())
                .build();

        descriptiveQuestionRepository.save(descriptiveQuestion);

        ExamQuestion examQuestion = ExamQuestion.builder()
                .exam(exam)
                .question(descriptiveQuestion)
                .build();
        examQuestionRepository.save(examQuestion);

        calculateTotalScore(exam);
        examRepository.save(exam);
    }

    @Override
    public void addQuestionToExam(AddQuestionToExamDto dto) {
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found: " + dto.getQuestionId()));

        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + dto.getExamId()));

        boolean questionExistsInExam = examQuestionRepository.existsByExamAndQuestion(exam, question);

        if (questionExistsInExam) {
            throw new QuestionAlreadyExistsInExamException("This question already added to this exam!");
        }

        ExamQuestion examQuestion = ExamQuestion.builder()
                .exam(exam)
                .question(question)
                .questionScore(dto.getScore())
                .build();

        examQuestionRepository.save(examQuestion);

        calculateTotalScore(exam);
        examRepository.save(exam);
    }

    public void calculateTotalScore(Exam exam) {
        double totalScore = 0.0;
        for (ExamQuestion examQuestion : exam.getExamQuestions()) {
            Double score = examQuestion.getQuestionScore();
            if (score == null) {
                score = examQuestion.getQuestion().getDefaultScore();
            }
            totalScore += score != null ? score : 0.0;
        }
        exam.setTotalScore(totalScore);
    }

    @Override
    public void deleteQuestionFromExam(DeleteQuestionFromExamDto dto) {
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found: " + dto.getQuestionId()));

        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + dto.getExamId()));

        ExamQuestion examQuestion = examQuestionRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new QuestionNotFoundInExamException("Not question added for this exam!"));

        examQuestionRepository.delete(examQuestion);

        calculateTotalScore(exam);
        examRepository.save(exam);
    }

    @Override
    public void createMultipleChoiceQuestionForBank(Long courseId, MultipleChoiceQuestionDto dto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with this id not found: " + courseId));


        MultipleChoiceQuestion multipleChoiceQuestion = MultipleChoiceQuestion.builder()
                .questionText(dto.getQuestionText())
                .title(dto.getTitle())
                .course(course)
                .defaultScore(dto.getDefaultScore())
                .options(new ArrayList<>())
                .build();

        List<Option> options = dto.getOptions().stream()
                .map(optionDto -> {
                    Option option = new Option();
                    option.setOptionText(optionDto.getOptionText());
                    option.setCorrect(optionDto.isCorrect());
                    option.setQuestion(multipleChoiceQuestion);
                    return option;
                }).toList();


        multipleChoiceQuestion.getOptions().addAll(options);
        multipleChoiceQuestionRepository.save(multipleChoiceQuestion);
    }

    @Override
    public void createDescriptiveQuestionForBank(Long courseId, DescriptiveQuestionDto dto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with this id not found: " + courseId));

        DescriptiveQuestion descriptiveQuestion = DescriptiveQuestion.builder()
                .questionText(dto.getQuestionText())
                .title(dto.getTitle())
                .defaultScore(dto.getScore())
                .course(course)
                .build();

        descriptiveQuestionRepository.save(descriptiveQuestion);
    }

    @Override
    public void updateMultipleChoiceQuestion(UpdateMCQDto dto) {
        MultipleChoiceQuestion multipleChoiceQuestion = multipleChoiceQuestionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found :" + dto.getQuestionId()));

        multipleChoiceQuestion.setQuestionText(dto.getQuestionText());
        multipleChoiceQuestion.setTitle(dto.getTitle());
        multipleChoiceQuestion.setDefaultScore(dto.getDefaultScore());

        multipleChoiceQuestion.getOptions().clear();

        List<Option> options = dto.getOptions().stream()
                .map(optionDto -> {
                    Option option = new Option();
                    option.setOptionText(optionDto.getOptionText());
                    option.setCorrect(optionDto.isCorrect());
                    option.setQuestion(multipleChoiceQuestion);
                    return option;
                }).toList();


        multipleChoiceQuestion.getOptions().addAll(options);
        multipleChoiceQuestionRepository.save(multipleChoiceQuestion);

        multipleChoiceQuestionRepository.save(multipleChoiceQuestion);

    }

    @Override
    public void updateDescriptiveQuestion(UpdateDQBankDto dto) {
        DescriptiveQuestion descriptiveQuestion = descriptiveQuestionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found :" + dto.getQuestionId()));

        descriptiveQuestion.setTitle(dto.getTitle());
        descriptiveQuestion.setQuestionText(dto.getQuestionText());
        descriptiveQuestion.setDefaultScore(dto.getDefaultScore());

        descriptiveQuestionRepository.save(descriptiveQuestion);
    }

    @Override
    public MultipleChoiceQuestion findMCQById(Long questionId) {
        return multipleChoiceQuestionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found :" + questionId));
    }

    @Override
    public DescriptiveQuestion findDQById(Long questionId) {
        return descriptiveQuestionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found :" + questionId));
    }

    @Override
    public List<Question> deletedQuestions() {
        return questionRepository.findQuestionsWithNoCourseAndNoExams();
    }

    @Override
    public void deletedQuestionFromTrash(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found :" + questionId));
        questionRepository.delete(question);
    }

    @Override
    public void addQuestionFromTrashToBank(Long questionId, Long courseId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found :" + questionId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found :" + courseId));

        question.setCourse(course);
        questionRepository.save(question);
    }

    @Override
    public Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found :" + id));
    }

    @Override
    public List<ExamQuestion> findQuestionExamByExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found :" + examId));
        return examQuestionRepository.findByExam(exam);
    }
}
