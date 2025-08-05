package ir.maktabsharif.online_exam.model.base;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity<ID extends Serializable> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;
}
