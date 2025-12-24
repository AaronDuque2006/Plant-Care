package ad.PlantTrack.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CareLogRequest {
    @NotNull(message = "Debes especificar a qué planta le hiciste el cuidado")
    private Long plantId;

    @NotNull(message = "Debes especificar qué acción realizaste (WATER, FERTILIZE, PRUNE, etc)")
    private String actionType; // Lo recibimos como String y lo convertimos a Enum después

    private String notes; // "Le puse un poco más de agua porque hacía calor"
    private String photoUrl; // "http://imgur.com/..." (Opcional por ahora)
}
