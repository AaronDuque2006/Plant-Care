package ad.PlantTrack.service;

import ad.PlantTrack.entity.Plant;
import ad.PlantTrack.entity.Species;
import ad.PlantTrack.payload.response.PlantResponse;
import ad.PlantTrack.service.impl.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class) // Habilita Mockito
public class PlantServiceTest {

    @InjectMocks // Inyecta los mocks dentro del servicio real
    private PlantService plantService;

    // --- TEST 1: Verificar status OK (Verde) ---
    @Test
    public void testWateringStatus_ShouldBeOK() {
        // 1. Preparar datos falsos (GIVEN)
        Species species = new Species();
        species.setCommonName("Cactus");
        species.setWateringFrequencyDays(7); // Se riega cada 7 días

        Plant plant = new Plant();
        plant.setId(1L);
        plant.setNickname("Cactilio");
        plant.setSpecies(species);
        plant.setLastWateringDate(LocalDate.now()); // Se regó HOY

        // 2. Ejecutar la lógica (WHEN)
        PlantResponse response = plantService.convertToResponse(plant);

        // 3. Verificar resultados (THEN)
        Assertions.assertEquals("OK", response.getWateringStatus());
        Assertions.assertEquals(7, response.getDaysUntilWatering());
        System.out.println("Test OK: El estado es " + response.getWateringStatus());
    }

    // --- TEST 2: Verificar status CRITICAL (Rojo) ---
    @Test
    public void testWateringStatus_ShouldBeCritical() {
        // 1. GIVEN
        Species species = new Species();
        species.setWateringFrequencyDays(5); // Frecuencia 5 días

        Plant plant = new Plant();
        plant.setSpecies(species);
        // Se regó hace 10 días (Ya pasó el doble de tiempo)
        plant.setLastWateringDate(LocalDate.now().minusDays(10));

        // 2. WHEN
        PlantResponse response = plantService.convertToResponse(plant);

        // 3. THEN
        Assertions.assertEquals("CRITICAL", response.getWateringStatus());
        // Debería decir que faltan -5 días (5 días de atraso)
        Assertions.assertEquals(-5, response.getDaysUntilWatering());
        System.out.println("Test CRITICAL: Atraso de " + response.getDaysUntilWatering() + " días");
    }

    // --- TEST 3: Verificar status WARNING (Amarillo/Hoy) ---
    @Test
    public void testWateringStatus_ShouldBeWarning() {
        // 1. GIVEN
        Species species = new Species();
        species.setWateringFrequencyDays(3);

        Plant plant = new Plant();
        plant.setSpecies(species);
        // Se regó hace exactamente 3 días -> Toca hoy
        plant.setLastWateringDate(LocalDate.now().minusDays(3));

        // 2. WHEN
        PlantResponse response = plantService.convertToResponse(plant);

        // 3. THEN
        Assertions.assertEquals("WARNING", response.getWateringStatus());
        Assertions.assertEquals(0, response.getDaysUntilWatering());
    }
}
