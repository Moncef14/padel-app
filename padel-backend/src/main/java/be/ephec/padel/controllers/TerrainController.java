package be.ephec.padel.controllers;
import be.ephec.padel.models.Terrain;
import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.services.TerrainService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terrains")
public class TerrainController {

    private final TerrainService terrainService;
    private final JwtUtil jwtUtil;

    public TerrainController(TerrainService terrainService, JwtUtil jwtUtil) {
        this.terrainService = terrainService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<Terrain> getAll(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");

        // un ADMIN_SITE ne gère que les terrains de son site, pas ceux des autres sites
        if ("ADMIN_SITE".equals(role)) {
            String authHeader = request.getHeader("Authorization");
            String token = authHeader.substring(7);
            Long siteId = jwtUtil.extractSiteId(token);
            return terrainService.getBySiteId(siteId);
        }
        return terrainService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Terrain> getById(@PathVariable Long id) {
        return ResponseEntity.ok(terrainService.getById(id));
    }

    @GetMapping("/site/{siteId}")
    public List<Terrain> getBySiteId(@PathVariable Long siteId) {
        return terrainService.getBySiteId(siteId);
    }

    @PostMapping
    public ResponseEntity<Terrain> create(@RequestBody Terrain terrain) {
        return ResponseEntity.status(HttpStatus.CREATED).body(terrainService.create(terrain));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Terrain> update(@PathVariable Long id, @RequestBody Terrain terrain) {
        return ResponseEntity.ok(terrainService.update(id, terrain));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        terrainService.delete(id);
        return ResponseEntity.noContent().build();
    }
}