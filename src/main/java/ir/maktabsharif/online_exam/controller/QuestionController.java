package ir.maktabsharif.online_exam.controller;

import ir.maktabsharif.online_exam.model.*;
import ir.maktabsharif.online_exam.model.dto.questiondto.*;
import ir.maktabsharif.online_exam.service.CourseService;
import ir.maktabsharif.online_exam.service.ExamService;
import ir.maktabsharif.online_exam.service.MasterService;
import ir.maktabsharif.online_exam.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/question")
public class QuestionController {

    private final ExamService examService;
    private final MasterService masterService;
    private final CourseService courseService;
    private final QuestionService questionService;

    public QuestionController(ExamService examService, MasterService masterService, CourseService courseService, QuestionService questionService) {
        this.examService = examService;
        this.masterService = masterService;
        this.courseService = courseService;
        this.questionService = questionService;
    }

    @GetMapping("/{courseId}/{examId}/questionsOfExam")
    private String questionsOfExam(@PathVariable Long courseId, @PathVariable Long examId, Model model) {
        List<DescriptiveQuestion> descriptiveQuestions = examService.descriptiveQuestionsOfExam(examId);
        List<MultipleChoiceQuestion> multipleChoiceQuestions = examService.multipleChoiceQuestionsOfExam(examId);
        Exam exam = examService.findById(examId);
        model.addAttribute("descriptiveQuestions", descriptiveQuestions);
        model.addAttribute("multipleChoiceQuestions", multipleChoiceQuestions);
        model.addAttribute("examId", examId);
        model.addAttribute("exam", exam);
        model.addAttribute("courseId", courseId);

        return "question/questionsOfExam";
    }


    @GetMapping("/{courseId}/questionBankForDeleteOrUpdate")
    private String questionBankForDeleteOrUpdate(@PathVariable Long courseId, Model model) {
        List<MultipleChoiceQuestion> multipleChoiceQuestions = courseService.mcqBank(courseId);
        List<DescriptiveQuestion> descriptiveQuestions = courseService.dqBank(courseId);
        model.addAttribute("multipleChoiceQuestions", multipleChoiceQuestions);
        model.addAttribute("descriptiveQuestions", descriptiveQuestions);
        model.addAttribute("courseId", courseId);
        return "question/questionBankForDeleteOrUpdate";
    }

    @GetMapping("/{courseId}/{examId}/questionBank")
    private String questionBank(@PathVariable Long courseId, @PathVariable Long examId, Model model) {
        List<MultipleChoiceQuestion> multipleChoiceQuestions = courseService.mcqBank(courseId);
        List<DescriptiveQuestion> descriptiveQuestions = courseService.dqBank(courseId);
        model.addAttribute("multipleChoiceQuestions", multipleChoiceQuestions);
        model.addAttribute("descriptiveQuestions", descriptiveQuestions);
        model.addAttribute("courseId", courseId);
        model.addAttribute("examId", examId);
        return "question/questionBank";
    }

    @PostMapping("/addQuestionFromBank")
    public String addQuestionToExam(@ModelAttribute("course") AddQuestionToExamDto addQuestionToExamDto,
                                    @RequestParam("courseId") Long courseId,
                                    @RequestParam("examId") Long examId, RedirectAttributes redirectAttributes) {
        questionService.addQuestionToExam(addQuestionToExamDto);
        redirectAttributes.addAttribute("examId", examId);
        redirectAttributes.addAttribute("courseId", courseId);
        redirectAttributes.addAttribute("successAdded", true);
        return "redirect:/question/{courseId}/{examId}/questionsOfExam";
    }

    @PostMapping("/deleteQuestionFromExam")
    public String deleteQuestionFromExam(@ModelAttribute("course") DeleteQuestionFromExamDto deleteQuestionFromExamDto,
                                         @RequestParam("courseId") Long courseId,
                                         @RequestParam("examId") Long examId, RedirectAttributes redirectAttributes) {
        questionService.deleteQuestionFromExam(deleteQuestionFromExamDto);
        redirectAttributes.addAttribute("examId", examId);
        redirectAttributes.addAttribute("courseId", courseId);
        redirectAttributes.addAttribute("successDeleted", true);
        return "redirect:/question/{courseId}/{examId}/questionsOfExam";
    }

    @PostMapping("/deleteQuestionFromBank")
    public String deleteQuestionFromQuestionBank(@ModelAttribute("course") DeleteQuestionFromQuestionBankDto dto,
                                                 @RequestParam("courseId") Long courseId, RedirectAttributes redirectAttributes) {
        courseService.deleteQuestionFromQuestionBank(dto);
        redirectAttributes.addAttribute("courseId", courseId);
        redirectAttributes.addAttribute("successDeleted", true);
        return "redirect:/question/{courseId}/questionBankForDeleteOrUpdate";
    }

    @GetMapping("/{questionId}/{courseId}/updateMultipleQuestion")
    public String showUpdateMCQForm(@PathVariable Long questionId, @PathVariable Long courseId, Model model) {
        MultipleChoiceQuestion mcq = questionService.findMCQById(questionId);
        UpdateMCQDto updateMCQDto = new UpdateMCQDto();
        updateMCQDto.setOptions(new ArrayList<>());
        updateMCQDto.setQuestionId(questionId);

        model.addAttribute("updateMCQDto", updateMCQDto);
        model.addAttribute("questionId", questionId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("mcq", mcq);

        return "question/updateMCQForm";
    }

    @PostMapping("/{questionId}/{courseId}/updateMultipleQuestion")
    public String updateMCQ(@PathVariable Long questionId,
                            @PathVariable Long courseId,
                            @ModelAttribute UpdateMCQDto dto,
                            RedirectAttributes redirectAttributes) {
        questionService.updateMultipleChoiceQuestion(dto);
        redirectAttributes.addAttribute("questionId", questionId);
        redirectAttributes.addAttribute("courseId", courseId);
        redirectAttributes.addAttribute("successUpdate", true);
        return "redirect:/question/{courseId}/questionBankForDeleteOrUpdate";
    }

    @GetMapping("/{questionId}/{courseId}/updateDescriptiveQuestion")
    public String showUpdateDescriptiveQuestionForm(@PathVariable Long questionId, @PathVariable Long courseId, Model model) {

        UpdateDQBankDto updateDQBankDto = new UpdateDQBankDto();
        updateDQBankDto.setQuestionId(questionId);
        DescriptiveQuestion descriptiveQuestion = questionService.findDQById(questionId);

        model.addAttribute("descriptiveQuestion", descriptiveQuestion);
        model.addAttribute("questionId", questionId);
        model.addAttribute("courseId", courseId);
        model.addAttribute("updateDQBankDto", updateDQBankDto);

        return "question/updateDQFrom";
    }

    @PostMapping("/{questionId}/{courseId}/updateDescriptiveQuestion")
    public String updateDescriptiveQuestion(@PathVariable Long questionId,
                                            @PathVariable Long courseId,
                                            @ModelAttribute UpdateDQBankDto dto,
                                            RedirectAttributes redirectAttributes) {

        questionService.updateDescriptiveQuestion(dto);
        redirectAttributes.addAttribute("questionId", questionId);
        redirectAttributes.addAttribute("courseId", courseId);
        redirectAttributes.addAttribute("successUpdate", true);
        return "redirect:/question/{courseId}/questionBankForDeleteOrUpdate";
    }

    @GetMapping("/{courseId}/{examId}/multipleQuestion")
    public String showCreateMCQForm(@PathVariable Long courseId, @PathVariable Long examId, Model model) {
        Course course = courseService.findById(courseId);
        Exam exam = examService.findById(examId);

        MultipleChoiceQuestionDto multipleChoiceQuestionDto = new MultipleChoiceQuestionDto();
        multipleChoiceQuestionDto.setOptions(new ArrayList<>());

        model.addAttribute("course", course);
        model.addAttribute("exam", exam);
        model.addAttribute("multipleChoiceQuestionDto", multipleChoiceQuestionDto);

        return "question/createMCQForm";
    }

    @PostMapping("/{courseId}/{examId}/multipleQuestion")
    public String createMCQ(@PathVariable Long courseId, @PathVariable Long examId,
                            @ModelAttribute MultipleChoiceQuestionDto multipleChoiceQuestionDto,
                            RedirectAttributes redirectAttributes) {
        questionService.createMultipleChoiceQuestion(courseId, examId, multipleChoiceQuestionDto);
        redirectAttributes.addAttribute("examId", examId);
        redirectAttributes.addAttribute("courseId", courseId);
        redirectAttributes.addAttribute("successCreate", true);
        return "redirect:/question/{courseId}/{examId}/questionsOfExam";
    }

    @GetMapping("/{courseId}/{examId}/descriptiveQuestion")
    public String showCreateDescriptiveQuestionForm(@PathVariable Long courseId, @PathVariable Long examId, Model model) {
        Course course = courseService.findById(courseId);
        Exam exam = examService.findById(examId);

        DescriptiveQuestionDto descriptiveQuestionDto = new DescriptiveQuestionDto();

        model.addAttribute("course", course);
        model.addAttribute("exam", exam);
        model.addAttribute("descriptiveQuestionDto", descriptiveQuestionDto);

        return "question/descriptiveQuestionForm";
    }

    @PostMapping("/{courseId}/{examId}/descriptiveQuestion")
    public String createDescriptiveQuestion(@PathVariable Long courseId, @PathVariable Long examId,
                                            @ModelAttribute DescriptiveQuestionDto descriptiveQuestionDto,
                                            RedirectAttributes redirectAttributes) {
        questionService.createDescriptiveQuestion(courseId, examId, descriptiveQuestionDto);
        redirectAttributes.addAttribute("examId", examId);
        redirectAttributes.addAttribute("courseId", courseId);
        redirectAttributes.addAttribute("successCreate", true);
        return "redirect:/question/{courseId}/{examId}/questionsOfExam";
    }

    @GetMapping("/{courseId}/multipleQuestionForBank")
    public String showCreateMCQFormForBank(@PathVariable Long courseId, Model model) {
        Course course = courseService.findById(courseId);

        MultipleChoiceQuestionDto multipleChoiceQuestionDto = new MultipleChoiceQuestionDto();
        multipleChoiceQuestionDto.setOptions(new ArrayList<>());

        model.addAttribute("course", course);
        model.addAttribute("multipleChoiceQuestionDto", multipleChoiceQuestionDto);

        return "question/createMCQFormForBank";
    }

    @PostMapping("/{courseId}/multipleQuestionForBank")
    public String createMCQForBank(@PathVariable Long courseId, @ModelAttribute MultipleChoiceQuestionDto multipleChoiceQuestionDto,
                                   RedirectAttributes redirectAttributes) {
        questionService.createMultipleChoiceQuestionForBank(courseId, multipleChoiceQuestionDto);
        redirectAttributes.addAttribute("courseId", courseId);
        redirectAttributes.addAttribute("successCreate", true);
        return "redirect:/question/{courseId}/questionBankForDeleteOrUpdate";
    }

    @GetMapping("/{courseId}/descriptiveQuestionForBank")
    public String showCreateDescriptiveQuestionFormForBank(@PathVariable Long courseId, Model model) {
        Course course = courseService.findById(courseId);

        DescriptiveQuestionDto descriptiveQuestionDto = new DescriptiveQuestionDto();

        model.addAttribute("course", course);
        model.addAttribute("descriptiveQuestionDto", descriptiveQuestionDto);

        return "question/descriptiveQuestionFormForBank";
    }

    @PostMapping("/{courseId}/descriptiveQuestionForBank")
    public String createDescriptiveQuestionForBank(@PathVariable Long courseId, @ModelAttribute DescriptiveQuestionDto descriptiveQuestionDto,
                                                   RedirectAttributes redirectAttributes) {
        questionService.createDescriptiveQuestionForBank(courseId, descriptiveQuestionDto);
        redirectAttributes.addAttribute("courseId", courseId);
        redirectAttributes.addAttribute("successCreate", true);
        return "redirect:/question/{courseId}/questionBankForDeleteOrUpdate";
    }

    @GetMapping("/deletedQuestions")
    public String deletedQuestions(Model model) {
        List<Question> questions = questionService.deletedQuestions();
        model.addAttribute("questions", questions);
        return "question/deletedQuestions";
    }
    @PostMapping("/{questionId}/deleteDeletedQuestion")
    public String deleteDeletedQuestion(@PathVariable Long questionId) {
        questionService.deleteDeletedQuestionFromBank(questionId);
        return "redirect:/question/deletedQuestions?successDeleted";
    }

    @PostMapping("/{questionId}/{courseId}/addDeletedQuestionToBank")
    public String addDeletedQuestionToBank(@PathVariable Long questionId , @PathVariable Long courseId) {
        questionService.addDeletedQuestionFromBankToBank(questionId , courseId);
        return "redirect:/question/deletedQuestions?successAdded";
    }

    @GetMapping("/coursesForAddQuestion")
    public String masterCoursesForAddQuestion(Model model, Principal principal) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        List<Course> courses = masterService.findMasterCourses(master.getId());

        model.addAttribute("courses", courses);
        return "question/coursesForAddQuestion";
    }

    @GetMapping("/{questionId}/coursesForAddDeletedQuestionToBank")
    public String coursesForAddDeletedQuestions(@PathVariable Long questionId , Model model, Principal principal) {
        String username = principal.getName();
        Master master = masterService.findByUsername(username);
        List<Course> courses = masterService.findMasterCourses(master.getId());

        model.addAttribute("questionId" , questionId);
        model.addAttribute("courses", courses);
        return "question/coursesForAddDeletedQuestions";
    }


}
