package ir.maktabsharif.online_exam.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCourseToStudentDto {
    private Long studentId;
    private Long courseId;
}
