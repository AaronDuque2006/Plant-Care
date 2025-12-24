package ad.PlantTrack.payload.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PlantResponse {
    private Long id;
    private String nickname;
    private String speciesName; // Mostramos el nombre, no todo el objeto especie
    private LocalDate lastWateringDate;

    // --- Campos Calculados (La magia) ---
    private LocalDate nextWateringDate;
    private long daysUntilWatering; // Puede ser negativo si está atrasado
    private String wateringStatus; // "OK", "WARNING" (hoy), "CRITICAL" (atrasado)
    private String imageUrl; // URL pública a la foto (ej: http://localhost:8080/uploads/archivo.jpg)

    // Constructor vacío
    public PlantResponse() {}
}
