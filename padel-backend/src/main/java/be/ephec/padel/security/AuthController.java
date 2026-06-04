package be.ephec.padel.security;

import be.ephec.padel.admin.Administrateur;
import be.ephec.padel.admin.AdministrateurService;
import be.ephec.padel.membre.Membre;
import be.ephec.padel.membre.MembreRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdministrateurService administrateurService;
    private final MembreRepository membreRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AdministrateurService administrateurService,
                          MembreRepository membreRepository,
                          JwtUtil jwtUtil) {
        this.administrateurService = administrateurService;
        this.membreRepository = membreRepository;
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