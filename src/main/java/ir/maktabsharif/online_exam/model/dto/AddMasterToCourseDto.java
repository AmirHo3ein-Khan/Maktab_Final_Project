package ir.maktabsharif.online_exam.model.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddMasterToCourseDto {
    private Long courseId;
    private Long masterId;
}
