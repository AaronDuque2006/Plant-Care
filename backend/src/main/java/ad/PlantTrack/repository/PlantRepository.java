package ad.PlantTrack.repository;

import ad.PlantTrack.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    // Busca todas las plantas que pertenecen a un usuario específico
    List<Plant> findByUserId(Long userId);
}
