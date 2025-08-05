package ir.maktabsharif.online_exam.model;

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
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceQuestion extends Question {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();
}
