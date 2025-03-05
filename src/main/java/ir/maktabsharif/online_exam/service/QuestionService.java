package ir.maktabsharif.online_exam.service;


import ir.maktabsharif.online_exam.model.DescriptiveQuestion;
import ir.maktabsharif.online_exam.model.MultipleChoiceQuestion;
import ir.maktabsharif.online_exam.model.Question;
import ir.maktabsharif.online_exam.model.dto.questiondto.*;

import java.util.List;

public interface QuestionService {
    void createMultipleChoiceQuestion(Long courseId , Long examId , MultipleChoiceQuestionDto multipleChoiceQuestionDto);
    void createDescriptiveQuestion(Long courseId , Long examId , DescriptiveQuestionDto dto);
    void addQuestionToExam(AddQuestionToExamDto dto);
    void deleteQuestionFromExam(DeleteQuestionFromExamDto dto);
    void createMultipleChoiceQuestionForBank(Long courseId,MultipleChoiceQuestionDto multipleChoiceQuestionDto);
    void createDescriptiveQuestionForBank(Long courseId,DescriptiveQuestionDto dto);
    void updateMultipleChoiceQuestion(UpdateMCQDto dto);
    void updateDescriptiveQuestion(UpdateDQBankDto dto);
    MultipleChoiceQuestion findMCQById(Long questionId);
    DescriptiveQuestion findDQById(Long questionId);
    List<Question> deletedQuestions();
    void deleteDeletedQuestionFromBank(Long questionId);
    void addDeletedQuestionFromBankToBank(Long questionId , Long courseId);
}
