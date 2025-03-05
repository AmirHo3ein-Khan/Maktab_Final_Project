package ir.maktabsharif.online_exam.model.dto.questiondto;

import ir.maktabsharif.online_exam.model.Option;
import lombok.*;

import java.util.List;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MultipleChoiceQuestionDto {
    private String title;
    private String questionText;
    private Double defaultScore;
    private List<Option> options;
}
