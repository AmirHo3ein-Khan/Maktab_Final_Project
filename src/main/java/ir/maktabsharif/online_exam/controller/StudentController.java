package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.StudentDto;
import ir.maktabsharif.online_exam.model.dto.StudentExamDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.DescriptiveAnswerDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.MultipleChoiceAnswerDto;
import ir.maktabsharif.online_exam.model.enums.ExamState;
import ir.maktabsharif.online_exam.service.*;
import ir.maktabsharif.online_exam.util.AnswerCacheService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final CourseService courseService;
    private final ExamService examService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final StudentExamService studentExamService;
    private final AnswerCacheService answerCacheService;
    private final GradingService gradingService;

    public StudentController(StudentService studentService, CourseService courseService, ExamService examService, QuestionService questionService,
                             AnswerService answerService, StudentExamService studentExamService, AnswerCacheService answerCacheService, GradingService gradingService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.examService = examService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.studentExamService = studentExamService;
        this.answerCacheService = answerCacheService;
        this.gradingService = gradingService;
    }

    @GetMapping("/panel")
    public String home() {
        return "student/studentPanel";
    }

    @GetMapping("/save")
    public String showSaveStudentForm(Model model) {
        model.addAttribute("student", new StudentDto());
        return "student/register";
    }

    @PostMapping("/save")
    public String saveStudent(@Valid @ModelAttribute("student") StudentDto studentDto, BindingResult result) {
        if (result.hasErrors()) {
            return "student/register";
        }
        studentService.saveStudent(studentDto);
        return "redirect:/login?success";
    }

    @GetMapping("/edit")
    public String updateStudentForm(Principal principal, Model model) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);
        model.addAttribute("student", student);
        model.addAttribute("studentId", student.getId());
        return "student/edit-student";
    }

    @PostMapping("/edit/{id}")
    public String updateStudent(@PathVariable("id") Long id, @Valid @ModelAttribute("student") StudentDto studentDto, BindingResult result) {
        if (result.hasErrors()) {
            return "student/edit-student";
        }
        boolean isUpdated = studentService.updateStudent(id, studentDto);
        if (isUpdated) {
            return "redirect:/student/edit?success";
        }
        return "redirect:/student/edit/{id}";
    }

    @GetMapping("/changePass")
    public String changePassForm(Principal principal, Model model) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);
        if (student == null) {
            throw new RuntimeException("User not found!");
        }
        model.addAttribute("master", student);
        return "student/changePass";
    }

    @PostMapping("/changePass")
    public String changePass(@RequestParam("oldPassword") String oldPassword,
                             @RequestParam("newPassword") String newPassword,
                             Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);

        if (student == null) {
            throw new RuntimeException("User not found!");
        }

        if (!studentService.checkPassword(student, oldPassword)) {
            redirectAttributes.addFlashAttribute("error", "Old password is incorrect.");
            return "redirect:/student/changePass";
        }

        studentService.changePassword(student, newPassword);
        redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/student/panel";
    }

    @GetMapping("/coursesOfStudent")
    public String coursesOfStudent(Model model, Principal principal) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);
        model.addAttribute("courses", studentService.coursesOfStudent(student.getId()));
        return "student/coursesOfStudent";
    }

    @GetMapping("/viewExamsGrades")
    private String viewGrad(Principal principal, Model model) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);
        List<Exam> completedExamsOfStudent = studentExamService.findCompletedExamsOfStudent(student.getId());
        Map<Long, Exam> exams = new HashMap<>();
        for (Exam exam : completedExamsOfStudent) {
            exams.put(exam.getId(), exam);
        }

        Map<Long, Double> examGrades = new HashMap<>();
        for (Map.Entry<Long, Exam> entry : exams.entrySet()) {
            Double scoreOfStudentInExam = gradingService.getScoreOfStudentInExam(student.getId(), entry.getValue().getId());
            examGrades.put(entry.getKey(), scoreOfStudentInExam);
        }

        model.addAttribute("examsGrade", examGrades);
        model.addAttribute("completedExamsOfStudent", completedExamsOfStudent);
        return "student/viewGrade";
    }

    @GetMapping("/{courseId}/examsOfCourse")
    private String courseExams(@PathVariable Long courseId, Model model) {
        Course course = courseService.findById(courseId);
        model.addAttribute("exams", course.getExams());
        model.addAttribute("courseId", courseId);
        return "student/examsOfCourse";
    }


    @GetMapping("/{examId}/examDetail")
    private String examDetails(@PathVariable Long examId, Model model) {
        Exam exam = examService.findById(examId);
        List<DescriptiveQuestion> descriptiveQuestions = examService.descriptiveQuestionsOfExam(examId);
        List<MultipleChoiceQuestion> multipleChoiceQuestions = examService.multipleChoiceQuestionsOfExam(examId);
        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(multipleChoiceQuestions);
        allQuestions.addAll(descriptiveQuestions);

        model.addAttribute("exam", exam);
        model.addAttribute("numberOfQuestions", allQuestions.size());
        model.addAttribute("examState", exam.getExamState());
        model.addAttribute("examCourse", exam.getCourse());
        model.addAttribute("examTime", exam.getExamTime());
        return "student/examsDetail";
    }

    @GetMapping("/{examId}/startExam/{questionIndex}")
    private String startExam(@PathVariable Long examId, @PathVariable int questionIndex, Model model, Principal principal) {
        Exam exam = examService.findById(examId);
        List<DescriptiveQuestion> descriptiveQuestions = examService.descriptiveQuestionsOfExam(examId);
        List<MultipleChoiceQuestion> multipleChoiceQuestions = examService.multipleChoiceQuestionsOfExam(examId);
        String username = principal.getName();
        Student student = studentService.findByUsername(username);

        StudentExamDto studentExamDto = new StudentExamDto(student.getId(), examId);
        studentExamService.startExam(studentExamDto);
        Optional<StudentExam> studentExam = studentExamService.findStudentExam(student.getId(), examId);

        LocalDateTime endAt = studentExam.get().getEndAt();
        LocalDateTime currentTime = LocalDateTime.now();

        Duration remainingTime = Duration.between(currentTime, endAt);
        long secondsLeft = remainingTime.getSeconds();

        if (currentTime.isAfter(endAt)) {
            model.addAttribute("error", "Your exam time has expired.");
            return "student/examErrorPage";
        }

        model.addAttribute("student", student);
        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(multipleChoiceQuestions);
        allQuestions.addAll(descriptiveQuestions);

        if (questionIndex < 0 || questionIndex >= allQuestions.size()) {
            return "redirect:/student/examErrorPage";
        }

        if (exam.getExamState() == ExamState.NOT_STARTED) {
            model.addAttribute("error", "Exam has not started yet.");
            return "student/examErrorPage";
        }
        if (exam.getExamState() == ExamState.FINISHED) {
            model.addAttribute("error", "Exam has already finished.");
            return "student/examErrorPage";
        }
        Question question = allQuestions.get(questionIndex);
        model.addAttribute("question", question);
        model.addAttribute("questionIndex", questionIndex);
        model.addAttribute("totalQuestions", allQuestions.size());

        String cacheKey = examId + "_" + student.getId() + "_" + questionIndex;
        Object cachedAnswer = answerCacheService.getAnswerFromCache(cacheKey);

        if (cachedAnswer != null) {
            if (cachedAnswer instanceof MultipleChoiceAnswerDto mcAnswerDto) {
                model.addAttribute("selectedOptionId", mcAnswerDto.getSelectedOptionId());
            }
            if (cachedAnswer instanceof DescriptiveAnswerDto descAnswerDto) {
                model.addAttribute("answer", descAnswerDto.getAnswer()); // Add the answer to the model
            }
        }

        String questionType;
        if (question instanceof MultipleChoiceQuestion) {
            questionType = "MCQ";
            model.addAttribute("multipleChoiceQuestion", question);
        } else if (question instanceof DescriptiveQuestion) {
            questionType = "DESC";
            model.addAttribute("descriptiveQuestion", question);
        } else {
            return "redirect:/student/examErrorPage";
        }

        model.addAttribute("questionType", questionType);
        model.addAttribute("examTime", exam.getExamTime());
        model.addAttribute("remainingTime", secondsLeft);
        model.addAttribute("exam", exam);
        return "student/startExam";
    }

    @PostMapping("/{examId}/saveAnswer")
    public String saveAnswer(@PathVariable Long examId,
                             @RequestParam Long studentId,
                             @RequestParam int questionIndex,
                             @RequestParam Long selectedOptionId,
                             @RequestParam String answer,
                             Model model) {

        Exam exam = examService.findById(examId);
        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(examService.multipleChoiceQuestionsOfExam(examId));
        allQuestions.addAll(examService.descriptiveQuestionsOfExam(examId));

        if (questionIndex < 0 || questionIndex >= allQuestions.size()) {
            return "redirect:/student/examErrorPage";
        }

        Question question = allQuestions.get(questionIndex);

        String cacheKey = examId + "_" + studentId + "_" + questionIndex;

        if (question instanceof MultipleChoiceQuestion && selectedOptionId != null) {
            MultipleChoiceAnswerDto mcAnswerDto = new MultipleChoiceAnswerDto(question.getId(), examId, studentId, selectedOptionId);
            answerCacheService.saveAnswerToCache(cacheKey, mcAnswerDto);
        } else if (question instanceof DescriptiveQuestion && answer != null) {
            DescriptiveAnswerDto descAnswerDto = new DescriptiveAnswerDto(question.getId(), examId, studentId, answer);
            answerCacheService.saveAnswerToCache(cacheKey, descAnswerDto);
        }

        model.addAttribute("exam", exam);
        model.addAttribute("questionIndex", questionIndex);
        model.addAttribute("totalQuestions", allQuestions.size());

        return "redirect:/student/" + examId + "/startExam/" + questionIndex;
    }


    @GetMapping("/{examId}/submitExam")
    private String endExam(@PathVariable Long examId, Model model, Principal principal) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);

        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(examService.multipleChoiceQuestionsOfExam(examId));
        allQuestions.addAll(examService.descriptiveQuestionsOfExam(examId));

        for (int questionIndex = 0; questionIndex < allQuestions.size(); questionIndex++) {
            String cacheKey = examId + "_" + student.getId() + "_" + questionIndex;
            Object cachedAnswer = answerCacheService.getAnswerFromCache(cacheKey);

            if (cachedAnswer != null) {
                if (cachedAnswer instanceof MultipleChoiceAnswerDto) {
                    MultipleChoiceAnswerDto mcAnswerDto = (MultipleChoiceAnswerDto) cachedAnswer;
                    answerService.saveMultipleChoiceAnswer(mcAnswerDto);
                } else if (cachedAnswer instanceof DescriptiveAnswerDto) {
                    DescriptiveAnswerDto descAnswerDto = (DescriptiveAnswerDto) cachedAnswer;
                    answerService.saveDescriptiveAnswer(descAnswerDto);
                }
                answerCacheService.clearAnswerFromCache(cacheKey);
            }
        }

        studentExamService.submitExam(student.getId(), examId);

        model.addAttribute("student", student);
        answerCacheService.clearAllAnswers();
        return "redirect:/student/" + examId + "/reviewExam";
    }

    @GetMapping("/{examId}/reviewExam")
    private String reviewExam(@PathVariable Long examId, Model model, Principal principal) {
        String username = principal.getName();
        Student student = studentService.findByUsername(username);

        List<QuestionExam> questionExams = questionService.findQuestionExamByExam(examId);

        List<Question> allQuestions = questionExams.stream()
                .map(QuestionExam::getQuestion)
                .collect(Collectors.toList());

        Map<Long, Object> rawStudentAnswers = answerService.getStudentAnswers(student.getId(), examId);

        Map<Long, String> studentAnswers = new HashMap<>();
        for (Map.Entry<Long, Object> entry : rawStudentAnswers.entrySet()) {
            if (entry.getValue() instanceof Option) {
                studentAnswers.put(entry.getKey(), ((Option) entry.getValue()).getOptionText());
            } else {
                studentAnswers.put(entry.getKey(), entry.getValue().toString());
            }
        }

        Map<Long, Boolean> isMCQMap = new HashMap<>();
        Map<Long, Boolean> isDESCMap = new HashMap<>();
        for (Question question : allQuestions) {
            isMCQMap.put(question.getId(), question instanceof MultipleChoiceQuestion);
            isDESCMap.put(question.getId(), question instanceof DescriptiveQuestion);
        }

        model.addAttribute("student", student);
        model.addAttribute("exam", examService.findById(examId));
        model.addAttribute("questions", allQuestions);
        model.addAttribute("studentAnswers", studentAnswers);
        model.addAttribute("isMCQMap", isMCQMap);
        model.addAttribute("isDESCMap", isDESCMap);

        return "student/reviewExam";
    }

}
