package be.ephec.padel.controllers;
import be.ephec.padel.models.Administrateur;
import be.ephec.padel.services.AdministrateurService;
import be.ephec.padel.models.enums.RoleAdmin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdministrateurController {

    private final AdministrateurService administrateurService;

    public AdministrateurController(AdministrateurService administrateurService) {
        this.administrateurService = administrateurService;
    }

    @GetMapping
    public List<Administrateur> getAll() {
        return administrateurService.getAll();
    }

    @GetMapping("/role/{role}")
    public List<Administrateur> getByRole(@PathVariable RoleAdmin role) {
        return administrateurService.getByRole(role);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Administrateur> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(administrateurService.getByEmail(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrateur> getById(@PathVariable Long id) {
        return ResponseEntity.ok(administrateurService.getById(id));
    }

    @PostMapping
    public ResponseEntity<Administrateur> create(@RequestBody Administrateur administrateur) {
        return ResponseEntity.status(HttpStatus.CREATED).body(administrateurService.create(administrateur));
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