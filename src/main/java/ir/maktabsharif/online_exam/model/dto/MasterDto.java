package ir.maktabsharif.online_exam.model.dto;

import lombok.*;

import java.time.LocalDate;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private LocalDate dob;
}
