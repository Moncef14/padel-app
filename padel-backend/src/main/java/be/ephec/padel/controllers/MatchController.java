package be.ephec.padel.controllers;
import be.ephec.padel.models.Match;
import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.services.MatchService;
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

    public MatchController(MatchService matchService, JwtUtil jwtUtil) {
        this.matchService = matchService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Match> getAll(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");

        if ("ADMIN_SITE".equals(role)) {
            String authHeader = request.getHeader("Authorization");
            String token = authHeader.substring(7);
            Long siteId = jwtUtil.extractSiteId(token);
            return matchService.getBySiteId(siteId);
        }
        return matchService.getAll();
    }

    @GetMapping("/publics")
    public List<Match> getPublics() {
        return matchService.getPublics();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getById(id));
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
    public ResponseEntity<Match> create(@RequestBody Match match) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.create(match));
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
}