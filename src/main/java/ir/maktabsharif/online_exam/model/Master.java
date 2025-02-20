package ir.maktabsharif.online_exam.model;

import ir.maktabsharif.online_exam.model.enums.RegisterState;
import ir.maktabsharif.online_exam.model.enums.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name="user_id", referencedColumnName = "id")
public class Master extends User {
    @OneToMany(mappedBy = "master")
    private List<Course> courses = new ArrayList<>();
}
