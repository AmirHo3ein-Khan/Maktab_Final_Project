package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.MasterDto;
import ir.maktabsharif.online_exam.model.dto.response.ApiResponseDto;
import ir.maktabsharif.online_exam.service.*;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/master")
public class MasterController {
    private final MasterService masterService;
    private final CourseService courseService;
    private final StudentExamService studentExamService;
    private final StudentService studentService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ExamService examService;
    private final GradingService gradingService;

    public MasterController(MasterService masterService, CourseService courseService, StudentExamService studentExamService, StudentService studentService, QuestionService questionService, AnswerService answerService, ExamService examService, GradingService gradingService) {
        this.masterService = masterService;
        this.courseService = courseService;
        this.studentExamService = studentExamService;
        this.studentService = studentService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.examService = examService;
        this.gradingService = gradingService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> registerMaster(@Valid @RequestBody MasterDto masterDto) {
        masterService.masterRegister(masterDto);
        String msg = "register.master.success";
        ApiResponseDto body = new ApiResponseDto(msg , true);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<ApiResponseDto> updateMaster(@PathVariable("id") Long id, @Valid MasterDto masterDto) {
        boolean isUpdate = masterService.updateMaster(id, masterDto);
        if (isUpdate) {
            String msg = "register.master.success";
            return ResponseEntity.ok(new ApiResponseDto(msg , true));
        } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/courses")
    public String masterCourses(Model model, Principal principal) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        List<Course> courses = masterService.findMasterCourses(master.getId());

        model.addAttribute("courses", courses);
        return "master/courses";
    }

    @GetMapping("/{courseId}/studentsCourse")
    private String courseStudents(@PathVariable Long courseId, Model model) {
        Course course = courseService.findById(courseId);
        model.addAttribute("students", course.getStudents());
        return "master/studentsOfCourse";
    }

    @GetMapping("/{courseId}/examsCourse")
    private String courseExams(@PathVariable Long courseId, Model model) {
        Course course = courseService.findById(courseId);
        model.addAttribute("exams", course.getExams());
        return "master/examsOfCourse";
    }

    @GetMapping("/changePass")
    public String changePassForm(Principal principal, Model model) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        if (master == null) {
            throw new RuntimeException("User not found!");
        }
        model.addAttribute("master", master);
        return "master/changePass";
    }

    @PostMapping("/changePass")
    public String changePass(@RequestParam("oldPassword") String oldPassword,
                             @RequestParam("newPassword") String newPassword,
                             Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        if (master == null) {
            throw new RuntimeException("User not found!");
        }
        if (!masterService.checkPassword(master, oldPassword)) {
            redirectAttributes.addFlashAttribute("error", "Old password is incorrect.");
            return "redirect:/master/changePass";
        }
        masterService.changePassword(master, newPassword);
        redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/master/panel";
    }

    @GetMapping("/coursesForGrading")
    public String coursesForGrading(Principal principal, Model model) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        List<Course> courses = masterService.findMasterCourses(master.getId());

        model.addAttribute("courses", courses);
        return "master/coursesForGrading";
    }

    @GetMapping("/{courseId}/studentsOfCourseForGrading")
    private String studentsOfCourseForGrading(@PathVariable Long courseId, Model model) {
        Course course = courseService.findById(courseId);
        model.addAttribute("students", course.getStudents());
        return "master/studentsOfCourseForGrading";
    }

    @GetMapping("/{studentId}/examsOfStudent")
    private String completedExamsOfStudent(@PathVariable Long studentId, Model model) {
        List<Exam> completedExamsOfStudent = studentExamService.findCompletedExamsOfStudent(studentId);
        model.addAttribute("completedExamsOfStudent", completedExamsOfStudent);
        model.addAttribute("studentId", studentId);
        return "master/examsOfStudent";
    }

    @GetMapping("/{examId}/{studentId}/gradingExam")
    private String gradingExam(@PathVariable Long examId, @PathVariable Long studentId, Model model) {

        Student student = studentService.findById(studentId);

        List<QuestionExam> questionExams = questionService.findQuestionExamByExam(examId);

        List<Question> questionsOfExam = questionExams.stream()
                .map(QuestionExam::getQuestion)
                .collect(Collectors.toList());

        for (Question question : questionsOfExam) {
            if (question instanceof MultipleChoiceQuestion) {
                gradingService.autoMultipleChoiceGrading(examId, question.getId());
            }
        }

        Map<Long, Double> maxScores = new HashMap<>();
        for (Question question : questionsOfExam) {
            Double maxScore = gradingService.getMaximumScoreForAnswer(examId, question.getId());
            maxScores.put(question.getId(), maxScore);
        }
        model.addAttribute("maxScores", maxScores);

        gradingService.setTotalScoreOfExamForStudent(studentId, examId);
        Double scoreOfStudentInExam = gradingService.getScoreOfStudentInExam(studentId, examId);
        model.addAttribute("scoreOfStudentInExam", scoreOfStudentInExam);

        Map<Long, Double> answerGrades = gradingService.getAnswerGrades(studentId, examId);
        model.addAttribute("answerGrades", answerGrades);

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
        for (Question question : questionsOfExam) {
            isMCQMap.put(question.getId(), question instanceof MultipleChoiceQuestion);
            isDESCMap.put(question.getId(), question instanceof DescriptiveQuestion);
        }

        model.addAttribute("student", student);
        model.addAttribute("exam", examService.findById(examId));
        model.addAttribute("questions", questionsOfExam);
        model.addAttribute("studentAnswers", studentAnswers);
        model.addAttribute("isMCQMap", isMCQMap);
        model.addAttribute("isDESCMap", isDESCMap);

        return "master/gradingExam";
    }

    @PostMapping("/{questionId}/{examId}/{studentId}/gradingQuestion")
    public String gradingQuestion(@PathVariable Long questionId, @PathVariable Long examId, @PathVariable Long studentId, Double score, Model model) {
        gradingService.gradingDescriptiveAnswer(questionId, examId, score);
        return "redirect:/master/{examId}/{studentId}/gradingExam";
    }

}
