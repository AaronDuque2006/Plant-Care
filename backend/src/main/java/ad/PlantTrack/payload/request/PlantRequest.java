package ad.PlantTrack.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PlantRequest {
    @NotNull(message = "Debes seleccionar una especie")
    private Long speciesId; // El usuario elige "Monstera" (ID 5)

    @NotBlank(message = "Ponle un nombre a tu planta")
    private String nickname; // "La moustrosa"

    private LocalDate acquisitionDate; // Cuándo la compró
    private LocalDate lastWateringDate; // Cuándo la regó por última vez
}
