package ad.PlantTrack.repository;

import ad.PlantTrack.entity.Role;
import ad.PlantTrack.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
