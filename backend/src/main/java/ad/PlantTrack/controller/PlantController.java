package ad.PlantTrack.controller;

import ad.PlantTrack.payload.request.PlantRequest;
import ad.PlantTrack.payload.response.MessageResponse;
import ad.PlantTrack.payload.response.PlantResponse;
import ad.PlantTrack.service.impl.PlantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/PlantCare/plants")
public class PlantController {

    @Autowired
    PlantService plantService;

    // GET: Ver MIS plantas (Con cálculos de riego)
    @GetMapping
    public ResponseEntity<List<PlantResponse>> getMyPlants() {
        return ResponseEntity.ok(plantService.getMyPlants());
    }

    // POST: Crear una planta nueva
    @PostMapping
    public ResponseEntity<PlantResponse> createPlant(@Valid @RequestBody PlantRequest request) {
        ad.PlantTrack.entity.Plant createdPlant = plantService.createPlant(request);
        return ResponseEntity.ok(plantService.convertToResponse(createdPlant));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantResponse> getPlantById(@PathVariable Long id) {
        return ResponseEntity.ok(plantService.getPlantById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlant(@PathVariable Long id) {
        try {
            plantService.deletePlant(id);
            return ResponseEntity.ok(new MessageResponse("¡Planta eliminada correctamente!"));
        } catch (RuntimeException e) {
            // Si intenta borrar una planta ajena o que no existe, devolvemos error 400 o 403
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<PlantResponse> uploadPlantImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(plantService.uploadImage(id, file));
    }
}
