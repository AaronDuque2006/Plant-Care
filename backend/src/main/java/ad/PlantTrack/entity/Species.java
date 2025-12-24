package ad.PlantTrack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "species")
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Species {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String commonName;

    @Column(length = 100)
    private String scientificName;

    private Integer wateringFrequencyDays; // Frecuencia de riego sugerida
    private Integer fertilizationFrequencyDays; // Frecuencia de abono sugerida

    @Column(length = 50)
    private String sunlightNeeds; // DIRECT, INDIRECT, SHADE

    @Column(columnDefinition = "TEXT")
    private String careTips;
}
