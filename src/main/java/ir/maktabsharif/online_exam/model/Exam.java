package ir.maktabsharif.online_exam.model;

import ir.maktabsharif.online_exam.model.base.BaseEntity;
import ir.maktabsharif.online_exam.model.enums.ExamState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private Double totalScore;

    @Enumerated(EnumType.STRING)
    private ExamState examState;

    @ManyToOne
    @JoinColumn(name = "COURSE_ID")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "MASTER_ID")
    private Master master;

    @OneToMany(mappedBy = "exam")
    private List<QuestionExam> questionExams = new ArrayList<>();

    @OneToMany(mappedBy = "exam")
    private List<StudentExam> studentExams = new ArrayList<>();
}
