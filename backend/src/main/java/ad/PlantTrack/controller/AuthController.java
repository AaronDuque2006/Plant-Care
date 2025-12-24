package ad.PlantTrack.controller;

import ad.PlantTrack.entity.Role;
import ad.PlantTrack.entity.User;
import ad.PlantTrack.enums.RoleName;
import ad.PlantTrack.payload.request.LoginRequest;
import ad.PlantTrack.payload.request.SignupRequest;
import ad.PlantTrack.payload.response.JwtResponse;
import ad.PlantTrack.payload.response.MessageResponse;
import ad.PlantTrack.repository.RoleRepository;
import ad.PlantTrack.repository.UserRepository;
import ad.PlantTrack.security.jwt.JwtUtils;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600) // Permite peticiones desde Angular/React/Vue
@RestController
@RequestMapping("/PlantCare/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // Endpoint de Login
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. Autenticar {usuario, password} usando el Manager de Spring
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 2. Si pasa, guardamos la autenticación en el contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generamos el Token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. Obtenemos los detalles del usuario para devolverlos en la respuesta
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                // Ojo: aquí casteamos a UserDetailsImpl si usaste una clase personalizada,
                // pero si usaste el User de Spring directo en tu servicio, no tienes acceso al ID directamente desde userDetails.
                // Truco rápido: buscar el usuario por nombre para sacar el ID (o mejorar el UserDetailsServiceImpl).
                // Por ahora, para que compile rápido sin cambiar tu servicio, buscamos el ID:
                userRepository.findByUsername(userDetails.getUsername()).get().getId(),
                userDetails.getUsername(),
                userRepository.findByUsername(userDetails.getUsername()).get().getEmail(),
                roles));
    }

    // Endpoint de Registro
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // 1. Validar duplicados
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: ¡El Username ya está en uso!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: ¡El Email ya está en uso!"));
        }

        // 2. Crear la cuenta del usuario (Con password ENCRIPTADA)
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        // 3. Asignar Roles
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            // Si no manda roles, por defecto es USER
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado. (¿Ejecutaste el SQL inicial?)"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("¡Usuario registrado exitosamente!"));
    }
}
