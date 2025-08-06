package ir.maktabsharif.online_exam.model.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExamDetailsDto {
    private String examTitle;
    private String examDescription;
    private LocalDate examDate;
    private Integer examTime;
    private Integer numberOfQuestions;
    private String courseName;

}
