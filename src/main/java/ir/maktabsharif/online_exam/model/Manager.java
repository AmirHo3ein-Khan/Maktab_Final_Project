package ir.maktabsharif.online_exam.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
@DiscriminatorValue("MANAGER")
public class Manager extends User {
}
