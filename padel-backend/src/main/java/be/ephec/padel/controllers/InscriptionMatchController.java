package be.ephec.padel.controllers;
import be.ephec.padel.dto.InscriptionMatchResponse;
import be.ephec.padel.models.InscriptionMatch;
import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.services.InscriptionMatchService;
import be.ephec.padel.services.MembreService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionMatchController {

    private final InscriptionMatchService inscriptionMatchService;
    private final JwtUtil jwtUtil;
    private final MembreService membreService;

    public InscriptionMatchController(InscriptionMatchService inscriptionMatchService, JwtUtil jwtUtil, MembreService membreService) {
        this.inscriptionMatchService = inscriptionMatchService;
        this.jwtUtil = jwtUtil;
        this.membreService = membreService;
    }

    @GetMapping
    public List<InscriptionMatch> getAll() {
        return inscriptionMatchService.getAll();
    }

    @GetMapping("/match/{matchId}")
    public List<InscriptionMatchResponse> getByMatchId(@PathVariable Long matchId) {
        return inscriptionMatchService.getByMatchIdAsResponse(matchId);
    }

    @GetMapping("/match/{matchId}/payes")
    public List<InscriptionMatchResponse> getJoueursPayes(@PathVariable Long matchId) {
        return inscriptionMatchService.getJoueursPayes(matchId).stream()
                .map(inscriptionMatchService::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/membre/{membreId}")
    public List<InscriptionMatchResponse> getByMembreId(@PathVariable Long membreId) {
        return inscriptionMatchService.getByMembreIdAsResponse(membreId);
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

    @PostMapping("/inscrire")
    public ResponseEntity<InscriptionMatchResponse> inscrireJoueur(
            @RequestBody InscrireJoueurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                inscriptionMatchService.toResponse(
                        inscriptionMatchService.inscrireJoueur(
                                request.matchId(), request.membreId(),
                                request.organisateurId())));
    }

    @PostMapping("/payer/{inscriptionId}")
    public ResponseEntity<InscriptionMatchResponse> payerPlace(
            @PathVariable Long inscriptionId, @RequestParam Long membreId) {
        return ResponseEntity.ok(
                inscriptionMatchService.toResponse(
                        inscriptionMatchService.payerPlace(inscriptionId, membreId)));
    }

    @PostMapping("/public/{matchId}")
    public ResponseEntity<InscriptionMatchResponse> inscrireEtPayer(
            @PathVariable Long matchId, @RequestParam Long membreId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                inscriptionMatchService.toResponse(
                        inscriptionMatchService.inscrireEtPayer(matchId, membreId)));
    }

    @DeleteMapping("/{id}/quitter")
    public ResponseEntity<Void> quitterMatch(@PathVariable Long id, HttpServletRequest request) {
        // le membreId n'est pas passé en paramètre : on le déduit du token pour empêcher qu'un membre libère la place d'un autre
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        String matricule = jwtUtil.extractUsername(token);

        Long membreId = membreService.getByMatricule(matricule).getId();

        inscriptionMatchService.quitterMatch(id, membreId);
        return ResponseEntity.noContent().build();
    }

    public record InscrireJoueurRequest(Long matchId, Long membreId, Long organisateurId) {}
}