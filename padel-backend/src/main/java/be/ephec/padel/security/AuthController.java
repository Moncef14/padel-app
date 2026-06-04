package be.ephec.padel.security;

import be.ephec.padel.admin.Administrateur;
import be.ephec.padel.admin.AdministrateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdministrateurService administrateurService;
    private final JwtUtil jwtUtil;

    public AuthController(AdministrateurService administrateurService, JwtUtil jwtUtil) {
        this.administrateurService = administrateurService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            Administrateur admin = administrateurService.getByEmail(request.getEmail());

            if (!admin.getMotDePasse().equals(request.getPassword())) {
                return ResponseEntity.status(401).build();
            }

            String token = jwtUtil.generateToken(admin.getEmail());
            String nom = admin.getPrenom() + " " + admin.getNom();
            return ResponseEntity.ok(new LoginResponse(token, admin.getRole().name(), nom));

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
}