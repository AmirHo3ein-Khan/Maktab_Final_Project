package ir.maktabsharif.online_exam.exception;

public class QuestionNotFoundInExamException extends RuntimeException{
    public QuestionNotFoundInExamException(String message) {
        super(message);
    }
}
