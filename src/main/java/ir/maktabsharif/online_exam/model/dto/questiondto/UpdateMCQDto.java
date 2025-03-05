package ir.maktabsharif.online_exam.model.dto.questiondto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMCQDto {
    private Long questionId;
    private String title;
    private String questionText;
    private Double defaultScore;
    private List<OptionDto> options;
}
