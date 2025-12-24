package ad.PlantTrack.controller;

import ad.PlantTrack.payload.request.CareLogRequest;
import ad.PlantTrack.payload.response.CareLogResponse;
import ad.PlantTrack.payload.response.MessageResponse;
import ad.PlantTrack.service.impl.CareLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/PlantCare/logs")
public class CareLogController {

    @Autowired
    CareLogService careLogService;

    // POST: Registrar un cuidado (Riego, Abono, etc)
    @PostMapping
    public ResponseEntity<?> createLog(@Valid @RequestBody CareLogRequest request) {
        careLogService.createLog(request);
        return ResponseEntity.ok(new MessageResponse("¡Cuidado registrado exitosamente!"));
    }

    @GetMapping("/plant/{plantId}")
    public ResponseEntity<List<CareLogResponse>> getLogsByPlant(@PathVariable Long plantId) { // <--- Cambio aquí
        return ResponseEntity.ok(careLogService.getLogsByPlant(plantId));
    }
}
