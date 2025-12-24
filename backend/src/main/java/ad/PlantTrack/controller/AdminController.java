package ad.PlantTrack.controller;

import ad.PlantTrack.entity.User;
import ad.PlantTrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/PlantCare/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')") // 🔒 ¡SOLO ADMINS PUEDEN ENTRAR AQUÍ!
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    // Endpoint para ver todos los usuarios registrados
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Aquí podrías agregar más endpoints como: /stats, /logs, etc.
}
