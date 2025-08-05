package ir.maktabsharif.online_exam.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
@DiscriminatorValue("MANAGER")
public class Manager extends User {
}
