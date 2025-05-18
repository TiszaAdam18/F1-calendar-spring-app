package hu.f1calendar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A versenyző keresztneve nem lehet üres.")
    @Size(min = 2, max = 50, message = "A keresztnévnek 2 és 50 karakter között kell lennie.")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "A versenyző vezetékneve nem lehet üres.")
    @Size(min = 2, max = 50, message = "A vezetéknévnek 2 és 50 karakter között kell lennie.")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "A nemzetiség nem lehet üres.")
    @Size(min = 2, max = 50, message = "A nemzetiségnek 2 és 50 karakter között kell lennie.")
    private String nationality;

    @NotNull(message = "A születési dátum nem lehet üres.")
    @Past(message = "A születési dátumnak a múltban kell lennie.")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Positive(message = "Az állandó rajtszámnak pozitívnak kell lennie.")
    @Column(name = "permanent_number", unique = true)
    private Integer permanentNumber;


    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

}
