package ir.maktabsharif.online_exam.service.impl;

import ir.maktabsharif.online_exam.exception.EntityNotFoundException;
import ir.maktabsharif.online_exam.exception.StudentCompletedTheExamException;
import ir.maktabsharif.online_exam.exception.StudentSubmittedTheExamException;
import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.StudentExamDto;
import ir.maktabsharif.online_exam.model.dto.response.ExamResponseDto;
import ir.maktabsharif.online_exam.model.enums.StudentExamStatus;
import ir.maktabsharif.online_exam.repository.*;
import ir.maktabsharif.online_exam.service.StudentExamService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentExamServiceImpl implements StudentExamService {
    private final StudentExamRepository studentExamRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final MultipleChoiceAnswerRepository multipleChoiceAnswerRepository;
    private final DescriptiveAnswerRepository descriptiveAnswerRepository;
    private final QuestionExamRepository questionExamRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    public StudentExamServiceImpl(StudentExamRepository studentExamRepository, StudentRepository studentRepository, ExamRepository examRepository, MultipleChoiceAnswerRepository multipleChoiceAnswerRepository, DescriptiveAnswerRepository descriptiveAnswerRepository, QuestionExamRepository questionExamRepository, QuestionRepository questionRepository, OptionRepository optionRepository) {
        this.studentExamRepository = studentExamRepository;
        this.studentRepository = studentRepository;
        this.examRepository = examRepository;
        this.multipleChoiceAnswerRepository = multipleChoiceAnswerRepository;
        this.descriptiveAnswerRepository = descriptiveAnswerRepository;
        this.questionExamRepository = questionExamRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    @Override
    public StudentExam startExam(StudentExamDto dto) {
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found : " + dto.getStudentId()));

        Exam exam = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found : " + dto.getExamId()));

        Optional<StudentExam> foundedStudentExam = studentExamRepository.findByStudentAndExam(student, exam);

        if (foundedStudentExam.isPresent()) {
            if (foundedStudentExam.get().getStudentExamStatus() == StudentExamStatus.COMPLETED) {
                throw new StudentCompletedTheExamException("You have already completed this exam!");
            }
        } else {
            StudentExam studentExam = StudentExam.builder()
                    .student(student)
                    .exam(exam)
                    .studentExamStatus(StudentExamStatus.IN_PROGRESS)
                    .startedAt(LocalDateTime.now())
                    .endAt(LocalDateTime.now().plusMinutes(exam.getExamTime()))
                    .build();
            studentExamRepository.save(studentExam);
        }
        return foundedStudentExam.get();
    }

    @Override
    public void submitExam(Long studentId, Long examId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found : " + studentId));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found : " + examId));

        StudentExam studentExam = studentExamRepository.findByStudentAndExam(student, exam)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found for this student"));

        if (studentExam.getStudentExamStatus() == StudentExamStatus.COMPLETED) {
            throw new StudentSubmittedTheExamException("You have already submitted this exam!");
        }
        studentExam.setStudentExamStatus(StudentExamStatus.COMPLETED);
        studentExam.setEndAt(LocalDateTime.now());

        studentExamRepository.save(studentExam);
    }

    @Override
    public StudentExam findStudentExam(Long studentId, Long examId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found : " + studentId));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found : " + examId));

        return studentExamRepository.findByStudentAndExam(student, exam)
                .orElseThrow(() -> new EntityNotFoundException("Student not start this exam yet : " + examId));
    }

    @Override
    public List<ExamResponseDto> findCompletedExamsOfStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with this id not found : " + studentId));

        List<StudentExam> studentExams = studentExamRepository.findByStudentAndStudentExamStatus(
                student,
                StudentExamStatus.COMPLETED
        );

        return studentExams.stream()
                .map(StudentExam::getExam)
                .map(exam -> ExamResponseDto.builder()
                        .examTitle(exam.getTitle())
                        .examDescription(exam.getDescription())
                        .examDate(exam.getExamDate())
                        .examTime(exam.getExamTime())
                        .numberOfQuestions(exam.getQuestionExams().size())
                        .courseName(exam.getCourse().getTitle())
                        .build()
                )
                .collect(Collectors.toList());
    }

}
