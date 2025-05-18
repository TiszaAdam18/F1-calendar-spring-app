package hu.f1calendar.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A csapat neve nem lehet üres.")
    @Size(min = 2, max = 100, message = "A csapat nevének 2 és 100 karakter között kell lennie.")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "A csapat székhelye nem lehet üres.")
    @Size(min = 3, max = 100, message = "A csapat székhelyének 3 és 100 karakter között kell lennie.")
    private String base; // Pl. "Brackley, UK"

    @Size(max = 100, message = "A csapatfőnök nevének maximum 100 karakternek kell lennie.")
    @Column(name = "team_principal")
    private String teamPrincipal;

    @Size(max = 50, message = "Az autó nevének maximum 50 karakternek kell lennie.")
    @Column(name = "car_name")
    private String carName; // Pl. "W15"


    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Driver> drivers;

}

