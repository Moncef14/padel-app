package be.ephec.padel.controllers;
import be.ephec.padel.models.JourFermeture;
import be.ephec.padel.services.JourFermetureService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fermetures")
public class JourFermetureController {

    private final JourFermetureService jourFermetureService;

    public JourFermetureController(JourFermetureService jourFermetureService) {
        this.jourFermetureService = jourFermetureService;
    }

    @GetMapping
    public List<JourFermeture> getAll() {
        return jourFermetureService.getAll();
    }

    @GetMapping("/globaux")
    public List<JourFermeture> getGlobaux() {
        return jourFermetureService.getGlobaux();
    }

    @GetMapping("/site/{siteId}")
    public List<JourFermeture> getBySiteId(@PathVariable Long siteId) {
        return jourFermetureService.getBySiteId(siteId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JourFermeture> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jourFermetureService.getById(id));
    }

    @PostMapping
    public ResponseEntity<JourFermeture> create(@RequestBody JourFermeture jourFermeture) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jourFermetureService.create(jourFermeture));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jourFermetureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}