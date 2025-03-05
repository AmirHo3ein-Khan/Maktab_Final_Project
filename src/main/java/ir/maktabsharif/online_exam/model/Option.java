package ir.maktabsharif.online_exam.model;

import ir.maktabsharif.online_exam.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Table(name = "options")
public class Option extends BaseEntity<Long> {
    @Column(nullable = false)
    private String optionText;

    @Column(nullable = false)
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private MultipleChoiceQuestion question;
}
