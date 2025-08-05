package ir.maktabsharif.online_exam.model;

import ir.maktabsharif.online_exam.model.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Course extends BaseEntity<Long> {
    @NotNull
    @NotEmpty(message = "Course should has title")
    private String title;
    private int unit;

    @Column(name = "START_COURSE_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate courseStartedDate;

    @Column(name = "FINISHED_COURSE_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate courseFinishedDate;

    @ManyToOne
    @JoinColumn(name = "MASTER_ID")
    private Master master;

    @ManyToMany
    @JoinTable(name = "STUDENT_COURSE")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Exam> exams = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<Question> questions = new ArrayList<>();

}
