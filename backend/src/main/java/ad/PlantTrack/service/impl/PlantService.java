package ad.PlantTrack.service.impl;

import ad.PlantTrack.entity.Plant;
import ad.PlantTrack.entity.Species;
import ad.PlantTrack.entity.User;
import ad.PlantTrack.enums.PlantStatus;
import ad.PlantTrack.payload.request.PlantRequest;
import ad.PlantTrack.payload.response.PlantResponse;
import ad.PlantTrack.repository.PlantRepository;
import ad.PlantTrack.repository.SpeciesRepository;
import ad.PlantTrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class PlantService {

    @Autowired
    PlantRepository plantRepository;

    @Autowired
    SpeciesRepository speciesRepository;

    @Autowired
    UserRepository userRepository;

    // Método privado para obtener al usuario logueado actualmente
    private User getAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public PlantResponse uploadImage(Long plantId, MultipartFile file) throws IOException {
        User currentUser = getAuthenticatedUser();
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Planta no encontrada"));

        // Seguridad: Verificar que sea del usuario
        if (!plant.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Error: No tienes permiso para editar esta planta");
        }

        // 1. Validar nombre y limpiar
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // 2. Definir dónde guardar (Carpeta "uploads" en la raíz del proyecto)
        String uploadDir = "uploads/";
        Path uploadPath = Paths.get(uploadDir);

        // DEBUG: Imprimir ruta absoluta
        System.out.println("DEBUG: Upload Path Absolute: " + uploadPath.toAbsolutePath().toString());

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 3. Guardar el archivo
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Actualizar la Entidad con la URL pública
            String fileUrl = "http://localhost:8080/uploads/" + fileName;
            
            // DEBUG: Imprimir URL generada
            System.out.println("DEBUG: Generated File URL: " + fileUrl);
            
            plant.setImageUrl(fileUrl);
            plantRepository.save(plant);

        } catch (IOException ioe) {
            throw new IOException("No se pudo guardar la imagen: " + fileName, ioe);
        }

        return convertToResponse(plant);
    }

    public Plant createPlant(PlantRequest request) {
        User currentUser = getAuthenticatedUser();

        Species species = speciesRepository.findById(request.getSpeciesId())
                .orElseThrow(() -> new RuntimeException("Especie no encontrada"));

        Plant plant = new Plant();
        plant.setNickname(request.getNickname());
        plant.setSpecies(species);
        plant.setUser(currentUser); // ¡Asignación automática!
        plant.setStatus(PlantStatus.ALIVE);

        // Si no envía fechas, asumimos "Hoy"
        plant.setAcquisitionDate(request.getAcquisitionDate() != null ? request.getAcquisitionDate() : LocalDate.now());
        plant.setLastWateringDate(request.getLastWateringDate() != null ? request.getLastWateringDate() : LocalDate.now());

        return plantRepository.save(plant);
    }

    public List<PlantResponse> getMyPlants() {
        User currentUser = getAuthenticatedUser();
        List<Plant> plants = plantRepository.findByUserId(currentUser.getId());

        // Convertimos cada planta usando el método mágico de arriba
        return plants.stream()
                .map(this::convertToResponse)
                .toList();
    }

    public void deletePlant(Long plantId) {
        // 1. Obtener al usuario actual (el que está intentando borrar)
        User currentUser = getAuthenticatedUser();

        // 2. Buscar la planta en la BD
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Error: Planta no encontrada"));

        // 3. VALIDACIÓN DE SEGURIDAD: ¿Es esta planta de este usuario?
        if (!plant.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Error: No tienes permiso para eliminar esta planta");
        }

        // 4. Si pasa la validación, la borramos
        plantRepository.delete(plant);
    }

    public PlantResponse convertToResponse(Plant plant) {
        PlantResponse response = new PlantResponse();
        response.setId(plant.getId());
        response.setNickname(plant.getNickname());
        response.setSpeciesName(plant.getSpecies().getCommonName());
        response.setLastWateringDate(plant.getLastWateringDate());

        // 1. Calcular la siguiente fecha de riego
        // Fórmula: Último riego + Frecuencia de la especie
        int freq = plant.getSpecies().getWateringFrequencyDays();
        LocalDate nextWatering = plant.getLastWateringDate().plusDays(freq);
        response.setNextWateringDate(nextWatering);

        // 2. Calcular días restantes (Diferencia entre HOY y la fecha siguiente)
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), nextWatering);
        response.setDaysUntilWatering(daysUntil);

        // 3. Determinar el estado (Semáforo)
        if (daysUntil < 0) {
            response.setWateringStatus("CRITICAL"); // ¡Ya se pasó! (Rojo)
        } else if (daysUntil == 0) {
            response.setWateringStatus("WARNING"); // Toca hoy (Amarillo)
        } else {
            response.setWateringStatus("OK"); // Aún falta (Verde)
        }

        // Incluir la URL pública de la imagen si existe
        response.setImageUrl(plant.getImageUrl());

        return response;
    }

    public PlantResponse getPlantById(Long id) {
        User currentUser = getAuthenticatedUser();

        // 1. Buscar planta
        Plant plant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Planta no encontrada")); // Esto lanza el error 400 que queremos

        // 2. Seguridad: Verificar que sea del usuario
        if (!plant.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Error: No tienes permiso para ver esta planta");
        }

        // 3. Convertir a DTO (con cálculos de riego)
        return convertToResponse(plant);
    }
}