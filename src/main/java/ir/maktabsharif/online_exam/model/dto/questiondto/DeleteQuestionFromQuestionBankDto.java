package ir.maktabsharif.online_exam.model.dto.questiondto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteQuestionFromQuestionBankDto {
    private Long courseId;
    private Long questionId;
}
