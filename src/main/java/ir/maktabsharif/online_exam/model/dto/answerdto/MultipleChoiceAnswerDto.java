package ir.maktabsharif.online_exam.model.dto.answerdto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultipleChoiceAnswerDto {
    private Long questionId;
    private Long examId;
    private Long studentId;
    private Long selectedOptionId;
}
