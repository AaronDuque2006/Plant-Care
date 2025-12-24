package ad.PlantTrack.entity;

import ad.PlantTrack.enums.ActionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_logs")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CareLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Plant plant;

    private LocalDateTime actionDate;

    @Enumerated(EnumType.STRING)
    private ActionType actionType; // WATER, FERTILIZE, etc.

    @Column(columnDefinition = "TEXT")
    private String notes;

    private String photoUrl; // URL de la foto de progreso

    // Método helper para asignar fecha actual automáticamente si no viene
    @PrePersist
    public void prePersist() {
        if (this.actionDate == null) {
            this.actionDate = LocalDateTime.now();
        }
    }
}
