package ir.maktabsharif.online_exam.model;

import javax.persistence.*;
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
@SuperBuilder
//@PrimaryKeyJoinColumn(name="user_id", referencedColumnName = "id")
@DiscriminatorValue("STUDENT")
public class Student extends User {
    @ManyToMany(mappedBy = "students")
    private List<Course> courses = new ArrayList<>();
    @OneToMany(mappedBy = "student")
    private List<StudentExam> studentExams = new ArrayList<>();
}
