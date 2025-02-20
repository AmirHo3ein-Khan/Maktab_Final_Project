package ir.maktabsharif.online_exam.model.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDto {
    private String title;
    private Integer unit;
    private LocalDate courseStartedDate;
    private LocalDate courseFinishedDate;
}
