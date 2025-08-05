package ir.maktabsharif.online_exam.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
//@PrimaryKeyJoinColumn(name="user_id", referencedColumnName = "id")
@DiscriminatorValue("MASTER")
public class Master extends User {
    @OneToMany(mappedBy = "master")
    private List<Course> courses = new ArrayList<>();
    @OneToMany(mappedBy = "master")
    private List<Exam> exams = new ArrayList<>();
}
