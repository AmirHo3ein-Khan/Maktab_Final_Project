package ir.maktabsharif.online_exam.model.dto.response;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDto {
    private String title;
    private int unit;
    private LocalDate courseStartedDate;
    private LocalDate courseFinishedDate;
}
