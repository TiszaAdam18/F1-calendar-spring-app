package hu.f1calendar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceDTO {

    private Long id;

    @NotBlank(message = "A verseny neve nem lehet üres.")
    private String name;

    @NotNull(message = "A verseny dátuma nem lehet üres.")
    @FutureOrPresent(message = "A verseny dátuma nem lehet a múltban.")
    private LocalDate date;

    @NotNull(message = "A verseny kezdési időpontja nem lehet üres.")
    private LocalTime startTime;

    @Positive(message = "A körök száma pozitív kell legyen.")
    private Integer laps;

    @NotNull(message = "A versenypálya azonosítója nem lehet üres.")
    private Long circuitId;
    private String circuitName;


}

