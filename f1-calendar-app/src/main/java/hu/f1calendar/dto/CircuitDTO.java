package hu.f1calendar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CircuitDTO {

    private Long id;

    @NotBlank(message = "A pálya neve nem lehet üres.")
    @Size(min = 3, max = 100, message = "A pálya nevének 3 és 100 karakter között kell lennie.")
    private String name;

    @NotBlank(message = "Az ország nem lehet üres.")
    @Size(min = 2, max = 50, message = "Az országnak 2 és 50 karakter között kell lennie.")
    private String country;

    @NotBlank(message = "A város nem lehet üres.")
    @Size(min = 2, max = 50, message = "A városnak 2 és 50 karakter között kell lennie.")
    private String city;

    private Double trackLengthKm;

}

