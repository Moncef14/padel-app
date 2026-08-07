package be.ephec.padel.controllers;
import be.ephec.padel.models.Site;
import be.ephec.padel.services.SiteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping
    public List<Site> getAll() {
        return siteService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Site> getById(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.getById(id));
    }

    @GetMapping("/actifs")
    public List<Site> getActifs() {
        return siteService.getActifs();
    }

    // création/modification/suppression de site réservées à ADMIN_GLOBAL : un ADMIN_SITE gère son site mais n'en crée pas de nouveaux
    @PostMapping
    @PreAuthorize("hasRole('ADMIN_GLOBAL')")
    public ResponseEntity<Site> create(@RequestBody Site site) {
        return ResponseEntity.status(HttpStatus.CREATED).body(siteService.create(site));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_GLOBAL')")
    public ResponseEntity<Site> update(@PathVariable Long id, @RequestBody Site site) {
        return ResponseEntity.ok(siteService.update(id, site));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_GLOBAL')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        siteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
