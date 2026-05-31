package be.ephec.padel.match;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matchs")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public List<Match> getAll() {
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