package ir.maktabsharif.online_exam.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddStudentToCourseDto {
    private Long courseId;
    private Long studentId;
}
