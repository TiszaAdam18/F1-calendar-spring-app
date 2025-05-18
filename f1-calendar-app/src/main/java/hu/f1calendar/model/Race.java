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
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A verseny neve nem lehet üres.")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "A verseny dátuma nem lehet üres.")
    @FutureOrPresent(message = "A verseny dátuma nem lehet a múltban.")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "A verseny kezdési időpontja nem lehet üres.")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // Helyi idő szerint

    @Positive(message = "A körök száma pozitív kell legyen.")
    private Integer laps;


    @NotNull(message = "A versenypálya nem lehet üres.")
    @ManyToOne
    @JoinColumn(name = "circuit_id", nullable = false)
    private Circuit circuit;


}

