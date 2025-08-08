package ir.maktabsharif.online_exam.model.dto.response;

import ir.maktabsharif.online_exam.model.enums.RegisterState;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private LocalDate dob;
    private RegisterState registerState;
}
