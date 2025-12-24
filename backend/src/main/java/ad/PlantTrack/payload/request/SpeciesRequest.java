package ad.PlantTrack.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SpeciesRequest {
    @NotBlank(message = "El nombre común es obligatorio")
    private String commonName; // Ej: Monstera

    private String scientificName;

    @Min(value = 1, message = "La frecuencia de riego debe ser al menos 1 día")
    private int wateringFrequencyDays;

    private int fertilizationFrequencyDays;

    @NotBlank
    private String sunlightNeeds; // DIRECT, INDIRECT, SHADE

    private String careTips;
}
