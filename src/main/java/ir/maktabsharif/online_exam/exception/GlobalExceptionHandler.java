package ir.maktabsharif.online_exam.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(QuestionAlreadyExistsInExamException.class)
    public ResponseEntity<ExceptionResponse> handleQuestionAlreadyExistsInExamException(QuestionAlreadyExistsInExamException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ScoreOfAnswerException.class)
    public ResponseEntity<ExceptionResponse> handleScoreOfAnswerException(ScoreOfAnswerException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AnswerTypeException.class)
    public ResponseEntity<ExceptionResponse> handleAnswerTypeException(AnswerTypeException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(StudentCompletedTheExamException.class)
    public ResponseEntity<ExceptionResponse> handleStudentCompletedTheExamException(StudentCompletedTheExamException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(QuestionNotFoundInExamException.class)
    public ResponseEntity<ExceptionResponse> handleQuestionNotFoundInExamException(QuestionNotFoundInExamException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(NotCompletedExamException.class)
    public ResponseEntity<ExceptionResponse> handleNotCompletedExamException(NotCompletedExamException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StudentSubmittedTheExamException.class)
    public ResponseEntity<ExceptionResponse> handleStudentSubmittedTheExamException(StudentSubmittedTheExamException ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        return new ResponseEntity<>(new ExceptionResponse(ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}
