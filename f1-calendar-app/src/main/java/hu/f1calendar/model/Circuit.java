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
public class Circuit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A pálya neve nem lehet üres.")
    @Size(min = 3, max = 100, message = "A pálya nevének 3 és 100 karakter között kell lennie.")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Az ország nem lehet üres.")
    @Size(min = 2, max = 50, message = "Az országnak 2 és 50 karakter között kell lennie.")
    @Column(nullable = false)
    private String country;

    @NotBlank(message = "A város nem lehet üres.")
    @Size(min = 2, max = 50, message = "A városnak 2 és 50 karakter között kell lennie.")
    @Column(nullable = false)
    private String city;

    @Column(name = "track_length_km")
    private Double trackLengthKm; // Pálya hossza km-ben

    @OneToMany(mappedBy = "circuit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Race> races;

}
