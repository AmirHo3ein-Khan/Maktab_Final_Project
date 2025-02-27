package ir.maktabsharif.online_exam.model;

import ir.maktabsharif.online_exam.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Exam extends BaseEntity<Long> {
    private String title;
    private String description;
    @Column(name = "exam_time")
    private Integer examTime;
    @Column(name = "exam_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate examDate;
    @ManyToOne
    @JoinColumn(name = "COURSE_ID")
    private Course course;
}
