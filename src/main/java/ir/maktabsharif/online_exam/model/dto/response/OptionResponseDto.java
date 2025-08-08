package ir.maktabsharif.online_exam.model.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponseDto {
    private String optionText;
    private boolean isCorrect;
}
