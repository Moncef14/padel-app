package be.ephec.padel.controllers;
import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.dto.LoginRequest;
import be.ephec.padel.dto.LoginResponse;
import be.ephec.padel.dto.MembreLoginRequest;
import be.ephec.padel.dto.MembreLoginResponse;
import be.ephec.padel.dto.RegisterRequest;
import be.ephec.padel.dto.RegisterResponse;

import be.ephec.padel.models.Administrateur;
import be.ephec.padel.services.AdministrateurService;
import be.ephec.padel.services.MembreService;
import be.ephec.padel.models.Membre;
import be.ephec.padel.repositories.MembreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdministrateurService administrateurService;
    private final MembreRepository membreRepository;
    private final MembreService membreService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(AdministrateurService administrateurService,
                          MembreRepository membreRepository,
                          MembreService membreService,
                          JwtUtil jwtUtil,
                          BCryptPasswordEncoder passwordEncoder) {
        this.administrateurService = administrateurService;
        this.membreRepository = membreRepository;
        this.membreService = membreService;
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

            Long siteId = admin.getSite() != null ? admin.getSite().getId() : null;
            String token = jwtUtil.generateToken(admin.getEmail(), admin.getRole().name(), siteId);
            String nom = admin.getPrenom() + " " + admin.getNom();
            return ResponseEntity.ok(new LoginResponse(token, admin.getRole().name(), nom));

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/membre")
    public ResponseEntity<MembreLoginResponse> loginMembre(
            @RequestBody MembreLoginRequest request) {
        // motDePasse null = membre importé sans mot de passe défini (cf. Membre.motDePasse) : connexion autorisée sur matricule seul
        return membreRepository.findByMatricule(request.getMatricule())
                .filter(m -> m.getMotDePasse() == null ||
                        passwordEncoder.matches(request.getMotDePasse() != null ?
                                request.getMotDePasse() : "",
                                m.getMotDePasse() != null ? m.getMotDePasse() : ""))
                .map(membre -> {
                    String token = jwtUtil.generateToken(membre.getMatricule());
                    return ResponseEntity.ok(new MembreLoginResponse(
                            membre.getId(),
                            token,
                            membre.getMatricule(),
                            membre.getType().name(),
                            membre.getNom(),
                            membre.getPrenom(),
                            membre.getSite() != null ? membre.getSite().getId() : null
                    ));
                })
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        try {
            Membre membre = membreService.inscrire(request);
            String token = jwtUtil.generateToken(membre.getMatricule());
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new RegisterResponse(
                            membre.getId(),
                            token,
                            membre.getMatricule(),
                            membre.getNom(),
                            membre.getPrenom(),
                            membre.getType(),
                            membre.getSite() != null ? membre.getSite().getId() : null
                    )
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).build();
        }
    }
}