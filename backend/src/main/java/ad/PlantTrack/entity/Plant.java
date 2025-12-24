package ad.PlantTrack.entity;

import ad.PlantTrack.enums.PlantStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "plants")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación Muchos a Uno: Muchas plantas pueden ser de una Especie
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "species_id", nullable = false)
    private Species species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Importante: Evita un bucle infinito al convertir a JSON
    private User user;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareLog> careLogs;

    @Column(nullable = false)
    private String nickname; // Apodo de la planta

    private LocalDate acquisitionDate;
    private LocalDate lastWateringDate;
    private LocalDate lastFertilizerDate;

    // Enum para evitar errores de texto
    @Enumerated(EnumType.STRING)
    private PlantStatus status;

    private String imageUrl;
}
