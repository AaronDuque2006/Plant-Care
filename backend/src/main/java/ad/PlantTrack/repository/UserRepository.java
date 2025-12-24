package ad.PlantTrack.repository;

import ad.PlantTrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // Para validar si ya existe al registrarse
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
