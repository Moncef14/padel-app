package be.ephec.padel.controllers;
import be.ephec.padel.dto.AdministrateurCreateRequest;
import be.ephec.padel.dto.AdministrateurResponse;
import be.ephec.padel.models.Administrateur;
import be.ephec.padel.services.AdministrateurService;
import be.ephec.padel.models.enums.RoleAdmin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@PreAuthorize("hasRole('ADMIN_GLOBAL')")
public class AdministrateurController {

    private final AdministrateurService administrateurService;

    public AdministrateurController(AdministrateurService administrateurService) {
        this.administrateurService = administrateurService;
    }

    @GetMapping
    public List<AdministrateurResponse> getAll() {
        return administrateurService.getAllAsResponse();
    }

    @GetMapping("/role/{role}")
    public List<AdministrateurResponse> getByRole(@PathVariable RoleAdmin role) {
        return administrateurService.getByRole(role).stream()
                .map(administrateurService::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<AdministrateurResponse> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(
                administrateurService.toResponse(
                        administrateurService.getByEmail(email)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministrateurResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(administrateurService.getByIdAsResponse(id));
    }

    @PostMapping
    public ResponseEntity<AdministrateurResponse> create(@RequestBody AdministrateurCreateRequest administrateur) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administrateurService.createFromRequest(administrateur));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrateur> update(@PathVariable Long id, @RequestBody Administrateur administrateur) {
        return ResponseEntity.ok(administrateurService.update(id, administrateur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        administrateurService.delete(id);
        return ResponseEntity.noContent().build();
    }
}