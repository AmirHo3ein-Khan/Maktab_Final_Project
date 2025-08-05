package ir.maktabsharif.online_exam.model;

import ir.maktabsharif.online_exam.model.base.BaseEntity;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "question")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
public class Question extends BaseEntity<Long> {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, name = "qustion_text")
    private String questionText;

    private Double defaultScore;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<QuestionExam> questionExams = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "COURSE_ID")
    private Course course;

}
