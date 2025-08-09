package ir.maktabsharif.online_exam.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitAnswersDto {
    private Long examId;
    private Long studentId;
    private int questionId;
    private Long selectedOptionId;
    private String answer;
}
