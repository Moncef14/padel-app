package be.ephec.padel.controllers;
import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.dto.LoginRequest;
import be.ephec.padel.dto.LoginResponse;
import be.ephec.padel.dto.MembreLoginRequest;
import be.ephec.padel.dto.MembreLoginResponse;

import be.ephec.padel.models.Administrateur;
import be.ephec.padel.services.AdministrateurService;
import be.ephec.padel.models.Membre;
import be.ephec.padel.repositories.MembreRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdministrateurService administrateurService;
    private final MembreRepository membreRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(AdministrateurService administrateurService,
                          MembreRepository membreRepository,
                          JwtUtil jwtUtil,
                          BCryptPasswordEncoder passwordEncoder) {
        this.administrateurService = administrateurService;
        this.membreRepository = membreRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            Administrateur admin = administrateurService.getByEmail(request.getEmail());

            if (!passwordEncoder.matches(request.getPassword(), admin.getMotDePasse())) {
                return ResponseEntity.status(401).build();
            }

            String token = jwtUtil.generateToken(admin.getEmail());
            String nom = admin.getPrenom() + " " + admin.getNom();
            return ResponseEntity.ok(new LoginResponse(token, admin.getRole().name(), nom));

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/membre")
    public ResponseEntity<MembreLoginResponse> loginMembre(@RequestBody MembreLoginRequest request) {
        return membreRepository.findByMatricule(request.getMatricule())
                .map(membre -> {
                    String token = jwtUtil.generateToken(membre.getMatricule());
                    return ResponseEntity.ok(new MembreLoginResponse(
                            token,
                            membre.getMatricule(),
                            membre.getType().name(),
                            membre.getNom(),
                            membre.getPrenom()
                    ));
                })
                .orElse(ResponseEntity.status(401).build());
    }
}