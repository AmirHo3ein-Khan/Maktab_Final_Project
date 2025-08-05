package ir.maktabsharif.online_exam.model;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("MULTIPLE_CHOICE_ANSWER")
public class MultipleChoiceAnswer extends Answer {
    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;
}
