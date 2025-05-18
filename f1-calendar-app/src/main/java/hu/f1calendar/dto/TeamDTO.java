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
public class TeamDTO {

    private Long id;

    @NotBlank(message = "A csapat neve nem lehet üres.")
    @Size(min = 2, max = 100, message = "A csapat nevének 2 és 100 karakter között kell lennie.")
    private String name;

    @NotBlank(message = "A csapat székhelye nem lehet üres.")
    @Size(min = 3, max = 100, message = "A csapat székhelyének 3 és 100 karakter között kell lennie.")
    private String base;

    @Size(max = 100, message = "A csapatfőnök nevének maximum 100 karakternek kell lennie.")
    private String teamPrincipal;

    @Size(max = 50, message = "Az autó nevének maximum 50 karakternek kell lennie.")
    private String carName;

}

