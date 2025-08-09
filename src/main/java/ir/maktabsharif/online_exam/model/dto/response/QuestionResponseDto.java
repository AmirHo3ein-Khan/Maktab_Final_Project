package ir.maktabsharif.online_exam.model.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponseDto {
    private Long id;
    private String title;

    private String questionText;

    private Double defaultScore;

    private List<OptionResponseDto> options;

}
