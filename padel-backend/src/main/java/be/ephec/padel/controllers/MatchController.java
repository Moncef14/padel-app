package be.ephec.padel.controllers;
import be.ephec.padel.dto.MatchCreateRequest;
import be.ephec.padel.dto.MatchResponse;
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
}