package ir.maktabsharif.online_exam.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("DESCRIPTIVE_ANSWER")
public class DescriptiveAnswer extends Answer {
    private String answerText;
}
