package ir.maktabsharif.online_exam.model.dto.response;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDto {
    private String title;

    private String questionText;

    private Double defaultScore;

}
