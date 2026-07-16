package be.ephec.padel.controllers;
import be.ephec.padel.dto.MembreCreateRequest;
import be.ephec.padel.dto.MembreResponse;
import be.ephec.padel.models.Membre;
import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.services.MembreService;
import be.ephec.padel.models.enums.TypeMembre;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membres")
public class MembreController {

    private final MembreService membreService;
    private final JwtUtil jwtUtil;

    public MembreController(MembreService membreService, JwtUtil jwtUtil) {
        this.membreService = membreService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<MembreResponse> getAll(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");

        if ("ADMIN_SITE".equals(role)) {
            String authHeader = request.getHeader("Authorization");
            String token = authHeader.substring(7);
            Long siteId = jwtUtil.extractSiteId(token);
            return membreService.getBySiteIdAsResponse(siteId);
        }
        return membreService.getAllAsResponse();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembreResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(membreService.getByIdAsResponse(id));
    }

    @GetMapping("/type/{type}")
    public List<Membre> getByType(@PathVariable TypeMembre type) {
        return membreService.getByType(type);
    }

    @GetMapping("/site/{siteId}")
    public List<Membre> getBySiteId(@PathVariable Long siteId) {
        return membreService.getBySiteId(siteId);
    }

    @PostMapping
    public ResponseEntity<MembreResponse> create(@RequestBody MembreCreateRequest membre) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membreService.createFromRequest(membre));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Membre> update(@PathVariable Long id, @RequestBody Membre membre) {
        return ResponseEntity.ok(membreService.update(id, membre));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        membreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}