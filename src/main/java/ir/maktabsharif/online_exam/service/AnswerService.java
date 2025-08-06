package ir.maktabsharif.online_exam.service;

import ir.maktabsharif.online_exam.model.Answer;
import ir.maktabsharif.online_exam.model.dto.answerdto.DescriptiveAnswerDto;
import ir.maktabsharif.online_exam.model.dto.answerdto.MultipleChoiceAnswerDto;

import java.util.Map;

public interface AnswerService {
    void saveMultipleChoiceAnswer(MultipleChoiceAnswerDto dto);
    void saveDescriptiveAnswer(DescriptiveAnswerDto dto);
    Map<Long, Object> getStudentAnswers(Long studentId, Long examId);
    Answer findAnswer(Long answerId);

}
