package ir.maktabsharif.online_exam.model;
import ir.maktabsharif.online_exam.model.base.BaseEntity;
import ir.maktabsharif.online_exam.model.enums.RegisterState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name =  "users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User extends BaseEntity<Long> {
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String username;
    private String password;
    private String email;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    @Enumerated(EnumType.STRING)
    private RegisterState registerState;
    @ManyToOne
    private Role role;
}