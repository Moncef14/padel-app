package be.ephec.padel.terrain;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terrains")
public class TerrainController {

    private final TerrainService terrainService;

    public TerrainController(TerrainService terrainService) {
        this.terrainService = terrainService;
    }

    @GetMapping
    public List<Terrain> getAll() {
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