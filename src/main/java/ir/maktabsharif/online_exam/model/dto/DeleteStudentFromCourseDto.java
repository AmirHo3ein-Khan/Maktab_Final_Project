package ir.maktabsharif.online_exam.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteStudentFromCourseDto {
    private Long courseId;
    private Long studentId;
}
