package be.ephec.padel.inscription;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionMatchController {

    private final InscriptionMatchService inscriptionMatchService;

    public InscriptionMatchController(InscriptionMatchService inscriptionMatchService) {
        this.inscriptionMatchService = inscriptionMatchService;
    }

    @GetMapping
    public List<InscriptionMatch> getAll() {
        return inscriptionMatchService.getAll();
    }

    @GetMapping("/match/{matchId}")
    public List<InscriptionMatch> getByMatchId(@PathVariable Long matchId) {
        return inscriptionMatchService.getByMatchId(matchId);
    }

    @GetMapping("/match/{matchId}/payes")
    public List<InscriptionMatch> getJoueursPayes(@PathVariable Long matchId) {
        return inscriptionMatchService.getJoueursPayes(matchId);
    }

    @GetMapping("/membre/{membreId}")
    public List<InscriptionMatch> getByMembreId(@PathVariable Long membreId) {
        return inscriptionMatchService.getByMembreId(membreId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InscriptionMatch> getById(@PathVariable Long id) {
        return ResponseEntity.ok(inscriptionMatchService.getById(id));
    }

    @PostMapping
    public ResponseEntity<InscriptionMatch> create(@RequestBody InscriptionMatch inscription) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inscriptionMatchService.create(inscription));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InscriptionMatch> update(@PathVariable Long id, @RequestBody InscriptionMatch inscription) {
        return ResponseEntity.ok(inscriptionMatchService.update(id, inscription));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inscriptionMatchService.delete(id);
        return ResponseEntity.noContent().build();
    }
}