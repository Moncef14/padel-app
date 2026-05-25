package be.ephec.padel.site;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<Site> create(@RequestBody Site site) {
        return ResponseEntity.status(HttpStatus.CREATED).body(siteService.create(site));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Site> update(@PathVariable Long id, @RequestBody Site site) {
        return ResponseEntity.ok(siteService.update(id, site));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        siteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
