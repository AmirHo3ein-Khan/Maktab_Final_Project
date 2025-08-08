package ir.maktabsharif.online_exam.model.dto.questiondto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddQuestionToExamDto {
    private Long examId;
    private Long questionId;
    private Double score;
}
