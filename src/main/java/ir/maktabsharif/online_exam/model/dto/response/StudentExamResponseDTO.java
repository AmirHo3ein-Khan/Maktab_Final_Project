package ir.maktabsharif.online_exam.model.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentExamResponseDTO {
       private Long studentId;
       private Long examId;
       private LocalDateTime startAt;
       private LocalDateTime endAt;
       private Integer currentQuestion;

}