package ad.PlantTrack.repository;

import ad.PlantTrack.entity.CareLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareLogRepository extends JpaRepository<CareLog, Long> {
    // Buscar todos los logs de una planta ordenados por fecha (del más reciente al más antiguo)
    List<CareLog> findByPlantIdOrderByActionDateDesc(Long plantId);
}
