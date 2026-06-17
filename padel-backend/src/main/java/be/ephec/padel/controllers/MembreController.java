package be.ephec.padel.controllers;
import be.ephec.padel.models.Membre;
import be.ephec.padel.services.MembreService;
import be.ephec.padel.models.enums.TypeMembre;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membres")
public class MembreController {

    private final MembreService membreService;

    public MembreController(MembreService membreService) {
        this.membreService = membreService;
    }

    @GetMapping
    public List<Membre> getAll() {
        return membreService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Membre> getById(@PathVariable Long id) {
        return ResponseEntity.ok(membreService.getById(id));
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
    public ResponseEntity<Membre> create(@RequestBody Membre membre) {
        return ResponseEntity.status(HttpStatus.CREATED).body(membreService.create(membre));
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