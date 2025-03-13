package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.AnswerTypeException;
import ir.maktabsharif.online_exam.exception.EntityNotFoundException;
import ir.maktabsharif.online_exam.exception.ScoreOfAnswerException;
import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.repository.*;
import ir.maktabsharif.online_exam.service.GradingService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GradingServiceImpl implements GradingService {
    private final StudentRepository studentRepository;
    private final QuestionExamRepository questionExamRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final AnswerRepository answerRepository;
    private final StudentExamRepository studentExamRepository;
    private final MultipleChoiceAnswerRepository multipleChoiceAnswerRepository;
    private final DescriptiveAnswerRepository descriptiveAnswerRepository;

    public GradingServiceImpl(StudentRepository studentRepository, QuestionExamRepository questionExamRepository,
                              AnswerRepository answerRepository, StudentExamRepository studentExamRepository,
                              QuestionRepository questionRepository, ExamRepository examRepository,
                              MultipleChoiceAnswerRepository multipleChoiceAnswerRepository,
                              DescriptiveAnswerRepository descriptiveAnswerRepository) {
        this.multipleChoiceAnswerRepository = multipleChoiceAnswerRepository;
        this.descriptiveAnswerRepository = descriptiveAnswerRepository;
        this.questionExamRepository = questionExamRepository;
        this.studentExamRepository = studentExamRepository;
        this.questionRepository = questionRepository;
        this.studentRepository = studentRepository;
        this.answerRepository = answerRepository;
        this.examRepository = examRepository;
    }


    @Override
    public void autoMultipleChoiceGrading(Long examId, Long questionId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + examId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found: " + questionId));

        QuestionExam questionExam = questionExamRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new EntityNotFoundException("Not question added for this exam: " + examId));

        Answer answer = answerRepository.findByQuestionExam(questionExam)
                .orElseThrow(() -> new EntityNotFoundException("No answer added for this question!"));

        Double scoreOfQuestion = questionExam.getQuestionScore();
        if (scoreOfQuestion == null) {
            scoreOfQuestion = questionExam.getQuestion().getDefaultScore();
        }

        if (answer instanceof MultipleChoiceAnswer) {
            if (((MultipleChoiceAnswer) answer).getOption().isCorrect()) {
                answer.setScore(scoreOfQuestion);
                answerRepository.save(answer);
            }
        }

    }


    @Override
    public void gradingDescriptiveAnswer(Long questionId, Long examId, Double score) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + examId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found: " + questionId));

        QuestionExam questionExam = questionExamRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new EntityNotFoundException("Not question added for this exam: " + examId));

        Answer answer = answerRepository.findByQuestionExam(questionExam)
                .orElseThrow(() -> new EntityNotFoundException("No answer added for this question!"));

        Double maxScoreOfQuestion = questionExam.getQuestionScore();
        if (maxScoreOfQuestion == null) {
            maxScoreOfQuestion = questionExam.getQuestion().getDefaultScore();
        }

        if (answer instanceof DescriptiveAnswer) {
            if (score > maxScoreOfQuestion) {
                throw new ScoreOfAnswerException("Assigned score exceeds the maximum allowed for this question.");
            }
            answer.setScore(score);
            answerRepository.save(answer);
        } else {
            throw new AnswerTypeException("The answer is not of type DescriptiveAnswer.");
        }

    }

    @Override
    public Map<Long, Double> getAnswerGrades(Long studentId, Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + examId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found: " + studentId));

        List<QuestionExam> questionExams = questionExamRepository.findByExam(exam);
        Map<Long, Double> answerGrades = new HashMap<>();

        for (QuestionExam questionExam : questionExams) {
            Long questionId = questionExam.getQuestion().getId();

            Optional<Answer> answerOpt = answerRepository.findByQuestionExamAndStudent(questionExam, student);

            answerGrades.put(questionId, answerOpt.map(Answer::getScore).orElse(0.0));
        }
        return answerGrades;
    }

    @Override
    public Double getMaximumScoreForAnswer(Long examId, Long questionId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + examId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question with this id not found: " + questionId));

        QuestionExam questionExam = questionExamRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new EntityNotFoundException("Not question added for this exam: " + examId));

        Double maxScoreOfQuestion = questionExam.getQuestionScore();
        if (maxScoreOfQuestion == null) {
            maxScoreOfQuestion = questionExam.getQuestion().getDefaultScore();
        }
        return maxScoreOfQuestion;
    }


    @Override
    public void setTotalScoreOfExamForStudent(Long studentId, Long examId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found: " + studentId));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + examId));

        List<QuestionExam> questionExams = questionExamRepository.findByExam(exam);

        double totalStudentScoreForExam = 0.0;

        for (QuestionExam questionExam : questionExams) {
            List<Answer> answers = answerRepository.findByStudentAndQuestionExam(student, questionExam);
            for (Answer answer : answers) {
                totalStudentScoreForExam += answer.getScore();
            }
        }

        StudentExam studentExam = studentExamRepository.findByStudentAndExam(student, exam)
                .orElseThrow(() -> new EntityNotFoundException("This student not completed this exam!"));

        studentExam.setExamScoreForStudent(totalStudentScoreForExam);
        studentExamRepository.save(studentExam);
    }

    @Override
    public Double getScoreOfStudentInExam(Long studentId, Long examId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found: " + studentId));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + examId));

        StudentExam studentExam = studentExamRepository.findByStudentAndExam(student, exam)
                .orElseThrow(() -> new EntityNotFoundException("This student not completed this exam!"));

        return studentExam.getExamScoreForStudent();
    }
}
