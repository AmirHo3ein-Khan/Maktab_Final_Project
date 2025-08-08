package ir.maktabsharif.online_exam.model.dto.response;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamResponseDto {
    private String examTitle;
    private String examDescription;
    private LocalDate examDate;
    private Integer examTime;
    private Integer numberOfQuestions;
    private String courseName;
}
