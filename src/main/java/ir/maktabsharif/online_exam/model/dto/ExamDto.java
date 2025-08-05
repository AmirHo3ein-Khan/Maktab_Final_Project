package ir.maktabsharif.online_exam.model.dto;

import lombok.*;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamDto {
    @NotBlank(message = "Title cannot be empty")
    @Size(min = 3, max = 20, message = "Title must be between 3 and 20 characters")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotNull(message = "Exam time cannot be empty")
    @Min(value = 1, message = "Exam time must be at least 30 minutes")
    private Integer examTime;

    @NotNull(message = "Exam date cannot be empty")
    @FutureOrPresent(message = "Exam date must be today or in the future")
    private LocalDate examDate;
}

