package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.EntityNotFoundException;
import ir.maktabsharif.online_exam.exception.QuestionNotFoundInExamException;
import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.answerdto.DescriptiveAnswerDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.MultipleChoiceAnswerDto;
import ir.maktabsharif.online_exam.repository.*;
import ir.maktabsharif.online_exam.service.AnswerService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final MultipleChoiceAnswerRepository multipleChoiceAnswerRepository;
    private final AnswerRepository answerRepository;
    private final DescriptiveAnswerRepository descriptiveAnswerRepository;
    private final StudentRepository studentRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final OptionRepository optionRepository;

    public AnswerServiceImpl(MultipleChoiceAnswerRepository multipleChoiceAnswerRepository, AnswerRepository answerRepository, DescriptiveAnswerRepository descriptiveAnswerRepository,
                             StudentRepository studentRepository, ExamQuestionRepository examQuestionRepository, QuestionRepository questionRepository,
                             ExamRepository examRepository, OptionRepository optionRepository) {
        this.multipleChoiceAnswerRepository = multipleChoiceAnswerRepository;
        this.answerRepository = answerRepository;
        this.descriptiveAnswerRepository = descriptiveAnswerRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.questionRepository = questionRepository;
        this.studentRepository = studentRepository;
        this.optionRepository = optionRepository;
        this.examRepository = examRepository;

    }

    @Override
    public void saveMultipleChoiceAnswer(MultipleChoiceAnswerDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found: " + dto.getStudentId()));

        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found: " + dto.getQuestionId()));

        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found: " + dto.getExamId()));

        ExamQuestion examQuestion = examQuestionRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new QuestionNotFoundInExamException("Not question added for this exam: " + dto.getExamId()));

        Option selectedOption = optionRepository.findById(dto.getSelectedOptionId())
                .orElseThrow(() -> new EntityNotFoundException("Option with this id not found: " + dto.getSelectedOptionId()));

        MultipleChoiceAnswer multipleChoiceAnswer = MultipleChoiceAnswer.builder()
                .option(selectedOption)
                .examQuestion(examQuestion)
                .student(student)
                .build();

        multipleChoiceAnswerRepository.save(multipleChoiceAnswer);
    }

    @Override
    public void saveDescriptiveAnswer(DescriptiveAnswerDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found: " + dto.getStudentId()));

        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found: " + dto.getQuestionId()));

        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found: " + dto.getExamId()));

        ExamQuestion examQuestion = examQuestionRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new QuestionNotFoundInExamException("Not question added for this exam!"));


        DescriptiveAnswer descriptiveAnswer = DescriptiveAnswer.builder()
                .answerText(dto.getAnswer())
                .examQuestion(examQuestion)
                .student(student)
                .build();

        descriptiveAnswerRepository.save(descriptiveAnswer);
    }

    public Map<Long, Object> getStudentAnswers(Long studentId, Long examId) {
        Map<Long, Object> answers = new HashMap<>();

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found: " + studentId));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + examId));

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExam(exam);

        for (ExamQuestion examQuestion : examQuestions) {
            Question question = examQuestion.getQuestion();

            List<MultipleChoiceAnswer> mcAnswers = multipleChoiceAnswerRepository.findByStudentAndExamQuestion(student, examQuestion);
            for (MultipleChoiceAnswer answer : mcAnswers) {
                answers.put(question.getId(), answer.getOption());
            }

            List<DescriptiveAnswer> descAnswers = descriptiveAnswerRepository.findByStudentAndExamQuestion(student, examQuestion);
            for (DescriptiveAnswer answer : descAnswers) {
                answers.put(question.getId(), answer.getAnswerText());
            }
        }

        return answers;
    }

    @Override
    public Answer findAnswer(Long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("Answer with this id not found: " + answerId));
    }


}
