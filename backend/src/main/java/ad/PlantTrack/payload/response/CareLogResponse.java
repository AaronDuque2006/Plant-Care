package ad.PlantTrack.payload.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CareLogResponse {
    private Long id;
    private LocalDateTime actionDate;
    private String actionType;
    private String notes;
    private String photoUrl;

    // Constructor para convertir fácilmente
    public CareLogResponse(Long id, LocalDateTime actionDate, String actionType, String notes, String photoUrl) {
        this.id = id;
        this.actionDate = actionDate;
        this.actionType = actionType;
        this.notes = notes;
        this.photoUrl = photoUrl;
    }
}
