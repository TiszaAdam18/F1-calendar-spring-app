package hu.f1calendar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {

    private Long id;

    @NotBlank(message = "A versenyző keresztneve nem lehet üres.")
    @Size(min = 2, max = 50, message = "A keresztnévnek 2 és 50 karakter között kell lennie.")
    private String firstName;

    @NotBlank(message = "A versenyző vezetékneve nem lehet üres.")
    @Size(min = 2, max = 50, message = "A vezetéknévnek 2 és 50 karakter között kell lennie.")
    private String lastName;

    @NotBlank(message = "A nemzetiség nem lehet üres.")
    @Size(min = 2, max = 50, message = "A nemzetiségnek 2 és 50 karakter között kell lennie.")
    private String nationality;

    @NotNull(message = "A születési dátum nem lehet üres.")
    @Past(message = "A születési dátumnak a múltban kell lennie.")
    private LocalDate dateOfBirth;

    @Positive(message = "Az állandó rajtszámnak pozitívnak kell lennie.")
    private Integer permanentNumber;

    private Long teamId;
    private String teamName;

}

