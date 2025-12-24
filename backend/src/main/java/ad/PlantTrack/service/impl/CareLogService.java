package ad.PlantTrack.service.impl;

import ad.PlantTrack.entity.CareLog;
import ad.PlantTrack.entity.Plant;
import ad.PlantTrack.entity.User;
import ad.PlantTrack.enums.ActionType;
import ad.PlantTrack.payload.request.CareLogRequest;
import ad.PlantTrack.payload.response.CareLogResponse;
import ad.PlantTrack.repository.CareLogRepository;
import ad.PlantTrack.repository.PlantRepository;
import ad.PlantTrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CareLogService {

    @Autowired
    CareLogRepository careLogRepository;

    @Autowired
    PlantRepository plantRepository;

    @Autowired
    UserRepository userRepository;

    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // @Transactional asegura que si falla el guardado del log, no se actualice la planta (todo o nada)
    @Transactional
    public CareLog createLog(CareLogRequest request) {
        User currentUser = getAuthenticatedUser();

        // 1. Buscar la planta y verificar dueño
        Plant plant = plantRepository.findById(request.getPlantId())
                .orElseThrow(() -> new RuntimeException("Planta no encontrada"));

        if (!plant.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para registrar cuidados en esta planta");
        }

        // 2. Convertir el String a Enum
        ActionType type;
        try {
            type = ActionType.valueOf(request.getActionType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Tipo de acción inválida. Usa: WATER, FERTILIZE, PRUNE, PHOTO_UPDATE");
        }

        // 3. Lógica de Actualización de la Planta (Efecto Secundario)
        if (type == ActionType.WATER) {
            plant.setLastWateringDate(LocalDate.now());
            plantRepository.save(plant);
        } else if (type == ActionType.FERTILIZE) {
            plant.setLastFertilizerDate(LocalDate.now());
            plantRepository.save(plant);
        }

        // 4. Crear y guardar el Log
        CareLog log = new CareLog();
        log.setPlant(plant);
        log.setActionType(type);
        log.setNotes(request.getNotes());
        log.setPhotoUrl(request.getPhotoUrl());
        log.setActionDate(LocalDateTime.now());

        return careLogRepository.save(log);
    }

    public List<CareLogResponse> getLogsByPlant(Long plantId) {
        User currentUser = getAuthenticatedUser();
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Planta no encontrada"));

        if (!plant.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para ver esta bitácora");
        }

        List<CareLog> logs = careLogRepository.findByPlantIdOrderByActionDateDesc(plantId);

        // Convertimos la lista de Entidades a lista de DTOs usando Stream
        return logs.stream()
                .map(log -> new CareLogResponse(
                        log.getId(),
                        log.getActionDate(),
                        log.getActionType().name(),
                        log.getNotes(),
                        log.getPhotoUrl()))
                .toList();
    }
}