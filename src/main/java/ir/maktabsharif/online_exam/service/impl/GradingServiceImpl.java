package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.*;
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
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final AnswerRepository answerRepository;
    private final StudentExamRepository studentExamRepository;
    private final MultipleChoiceAnswerRepository multipleChoiceAnswerRepository;
    private final DescriptiveAnswerRepository descriptiveAnswerRepository;

    public GradingServiceImpl(StudentRepository studentRepository, ExamQuestionRepository examQuestionRepository,
                              AnswerRepository answerRepository, StudentExamRepository studentExamRepository,
                              QuestionRepository questionRepository, ExamRepository examRepository,
                              MultipleChoiceAnswerRepository multipleChoiceAnswerRepository,
                              DescriptiveAnswerRepository descriptiveAnswerRepository) {
        this.multipleChoiceAnswerRepository = multipleChoiceAnswerRepository;
        this.descriptiveAnswerRepository = descriptiveAnswerRepository;
        this.examQuestionRepository = examQuestionRepository;
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

        ExamQuestion examQuestion = examQuestionRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new QuestionNotFoundInExamException("Not question added for this exam!"));

        Answer answer = answerRepository.findByExamQuestion(examQuestion)
                .orElseThrow(() -> new EntityNotFoundException("Student answer non of questions!"));

        Double scoreOfQuestion = examQuestion.getQuestionScore();
        if (scoreOfQuestion == null) {
            scoreOfQuestion = examQuestion.getQuestion().getDefaultScore();
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

        ExamQuestion examQuestion = examQuestionRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new QuestionNotFoundInExamException("Not question added for this exam!"));

        Answer answer = answerRepository.findByExamQuestion(examQuestion)
                .orElseThrow(() -> new EntityNotFoundException("Student answer non of questions!"));

        Double maxScoreOfQuestion = examQuestion.getQuestionScore();
        if (maxScoreOfQuestion == null) {
            maxScoreOfQuestion = examQuestion.getQuestion().getDefaultScore();
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

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExam(exam);
        Map<Long, Double> answerGrades = new HashMap<>();

        for (ExamQuestion examQuestion : examQuestions) {
            Long questionId = examQuestion.getQuestion().getId();

            Optional<Answer> answerOpt = answerRepository.findByExamQuestionAndStudent(examQuestion, student);

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

        ExamQuestion examQuestion = examQuestionRepository.findByExamAndQuestion(exam, question)
                .orElseThrow(() -> new QuestionNotFoundInExamException("Not question added for this exam!"));

        Double maxScoreOfQuestion = examQuestion.getQuestionScore();
        if (maxScoreOfQuestion == null) {
            maxScoreOfQuestion = examQuestion.getQuestion().getDefaultScore();
        }
        return maxScoreOfQuestion;
    }


    @Override
    public void setTotalScoreOfExamForStudent(Long studentId, Long examId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found: " + studentId));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with this id not found: " + examId));

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExam(exam);

        double totalStudentScoreForExam = 0.0;

        for (ExamQuestion examQuestion : examQuestions) {
            List<Answer> answers = answerRepository.findByStudentAndExamQuestion(student, examQuestion);
            for (Answer answer : answers) {
                totalStudentScoreForExam += answer.getScore();
            }
        }

        StudentExam studentExam = studentExamRepository.findByStudentAndExam(student, exam)
                .orElseThrow(() -> new NotCompletedExamException("This student not completed this exam!"));

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
                .orElseThrow(() -> new NotCompletedExamException("This student not completed this exam!"));

        return studentExam.getExamScoreForStudent();
    }
}
