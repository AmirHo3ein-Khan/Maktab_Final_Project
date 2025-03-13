package ir.maktabsharif.online_exam.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exceptionError", "Not found! Message: " + ex.getMessage());
        return "redirect:/exceptionPage";
    }

    @ExceptionHandler(QuestionAlreadyExistsInExamException.class)
    public String handleQuestionAlreadyExistsInExamException(QuestionAlreadyExistsInExamException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exceptionError", "Error! Message: " + ex.getMessage());
        return "redirect:/exceptionPage";
    }

    @ExceptionHandler(ScoreOfAnswerException.class)
    public String handleScoreOfAnswerException(ScoreOfAnswerException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exceptionError", "Error! Message: " + ex.getMessage());
        return "redirect:/exceptionPage";
    }

    @ExceptionHandler(AnswerTypeException.class)
    public String handleAnswerTypeException(AnswerTypeException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exceptionError", "Error! Message: " + ex.getMessage());
        return "redirect:/exceptionPage";
    }

    @ExceptionHandler(StudentCompletedTheExamException.class)
    public String handleStudentCompletedTheExamException(StudentCompletedTheExamException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exceptionError", "Error! Message: " + ex.getMessage());
        return "redirect:/exceptionPage";
    }

    @ExceptionHandler(StudentSubmittedTheExamException.class)
    public String handleStudentSubmittedTheExamException(StudentSubmittedTheExamException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exceptionError", "Error! Message: " + ex.getMessage());
        return "redirect:/exceptionPage";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exceptionError", "Something went wrong! Message: " + ex.getMessage());
        return "redirect:/exceptionPage";
    }
}
