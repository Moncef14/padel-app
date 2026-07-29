package be.ephec.padel.controllers;
import be.ephec.padel.dto.MatchCreateRequest;
import be.ephec.padel.dto.MatchResponse;
import be.ephec.padel.models.Match;
import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.services.AdministrateurService;
import be.ephec.padel.services.MatchService;
import be.ephec.padel.services.MembreService;
import be.ephec.padel.models.enums.StatutMatch;
import be.ephec.padel.models.enums.TypeMatch;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matchs")
public class MatchController {

    private final MatchService matchService;
    private final JwtUtil jwtUtil;
    private final AdministrateurService administrateurService;
    private final MembreService membreService;

    public MatchController(MatchService matchService, JwtUtil jwtUtil, AdministrateurService administrateurService, MembreService membreService) {
        this.matchService = matchService;
        this.jwtUtil = jwtUtil;
        this.administrateurService = administrateurService;
        this.membreService = membreService;
    }

    @GetMapping
    public List<MatchResponse> getAll(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");

        if ("ADMIN_SITE".equals(role)) {
            String authHeader = request.getHeader("Authorization");
            String token = authHeader.substring(7);
            Long siteId = jwtUtil.extractSiteId(token);
            return matchService.getBySiteIdAsResponse(siteId);
        }
        return matchService.getAllAsResponse();
    }

    @GetMapping("/publics")
    public List<MatchResponse> getPublics() {
        return matchService.getPublics().stream()
                .map(matchService::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/membre/{membreId}")
    public List<MatchResponse> getByMembre(@PathVariable Long membreId) {
        return matchService.getByMembreParticipantAsResponse(membreId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getByIdAsResponse(id));
    }

    @GetMapping("/statut/{statut}")
    public List<Match> getByStatut(@PathVariable StatutMatch statut) {
        return matchService.getByStatut(statut);
    }

    @GetMapping("/type/{type}")
    public List<Match> getByType(@PathVariable TypeMatch type) {
        return matchService.getByType(type);
    }

    @PostMapping
    public ResponseEntity<MatchResponse> create(@RequestBody MatchCreateRequest match) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.createFromRequest(match));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> update(@PathVariable Long id, @RequestBody Match match) {
        return ResponseEntity.ok(matchService.update(id, match));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<MatchResponse> annuler(@PathVariable Long id, HttpServletRequest request) {
        // Extraction du demandeur depuis le token JWT
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        // Le token porte soit un email d'admin, soit un matricule de membre
        // On tente d'abord l'admin, puis on retombe sur le membre
        Long demandeurId;
        if (role != null) {
            demandeurId = administrateurService.getByEmail(username).getId();
        } else {
            demandeurId = membreService.getByMatricule(username).getId();
        }

        return ResponseEntity.ok(matchService.annuler(id, demandeurId, role));
    }
}