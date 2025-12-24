package ad.PlantTrack.controller;

import ad.PlantTrack.entity.Species;
import ad.PlantTrack.payload.request.SpeciesRequest;
import ad.PlantTrack.payload.response.MessageResponse;
import ad.PlantTrack.service.impl.SpeciesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/PlantCare/species")
public class SpeciesController {

    @Autowired
    SpeciesService speciesService;

    // GET: Ver todas las especies (Disponible para USER y ADMIN)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Species>> getAllSpecies() {
        return ResponseEntity.ok(speciesService.getAllSpecies());
    }

    // GET: Ver detalle de una especie
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Species> getSpeciesById(@PathVariable Long id) {
        return ResponseEntity.ok(speciesService.getSpeciesById(id));
    }

    // POST: Crear nueva especie (SOLO ADMIN)
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createSpecies(@Valid @RequestBody SpeciesRequest request) {
        speciesService.createSpecies(request);
        return ResponseEntity.ok(new MessageResponse("Especie creada exitosamente"));
    }
}
