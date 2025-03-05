package ir.maktabsharif.online_exam.model.dto;

import lombok.*;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequestDto {
    @NotBlank(message = "Course title cannot be empty")
    @Size(min = 3, max = 30, message = "Title must be between 3 and 30 characters")
    private String title;

    @NotNull(message = "Unit cannot be empty")
    @Min(value = 1, message = "Unit must be at least 1")
    private Integer unit;

    @NotNull(message = "Course start date cannot be empty")
    @FutureOrPresent(message = "Course start date must be today or in the future")
    private LocalDate courseStartedDate;

    @NotNull(message = "Course end date cannot be empty")
    @Future(message = "Course end date must be in the future")
    private LocalDate courseFinishedDate;
}

