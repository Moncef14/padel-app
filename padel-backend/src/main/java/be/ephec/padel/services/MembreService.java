package be.ephec.padel.services;
import be.ephec.padel.dto.MembreCreateRequest;
import be.ephec.padel.dto.MembreResponse;
import be.ephec.padel.models.Membre;
import be.ephec.padel.models.Site;
import be.ephec.padel.repositories.MembreRepository;
import be.ephec.padel.models.enums.TypeMembre;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembreService {

    private final MembreRepository membreRepository;

    public MembreService(MembreRepository membreRepository) {
        this.membreRepository = membreRepository;
    }

    public List<Membre> getAll() {
        return membreRepository.findAll();
    }

    public Membre getById(Long id) {
        return membreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membre non trouvé avec l'id : " + id));
    }

    public List<Membre> getByType(TypeMembre type) {
        return membreRepository.findByType(type);
    }

    public List<Membre> getBySiteId(Long siteId) {
        return membreRepository.findBySiteId(siteId);
    }

    public Membre create(Membre membre) {
        return membreRepository.save(membre);
    }

    public Membre update(Long id, Membre membre) {
        Membre existing = getById(id);
        existing.setMatricule(membre.getMatricule());
        existing.setNom(membre.getNom());
        existing.setPrenom(membre.getPrenom());
        existing.setEmail(membre.getEmail());
        existing.setType(membre.getType());
        existing.setSite(membre.getSite());
        existing.setSoldeDu(membre.getSoldeDu());
        existing.setPenaliteJusquAu(membre.getPenaliteJusquAu());
        return membreRepository.save(existing);
    }

    public void delete(Long id) {
        membreRepository.deleteById(id);
    }

    public MembreResponse toResponse(Membre m) {
        return MembreResponse.builder()
                .id(m.getId())
                .matricule(m.getMatricule())
                .nom(m.getNom())
                .prenom(m.getPrenom())
                .email(m.getEmail())
                .type(m.getType())
                .siteId(m.getSite() != null ? m.getSite().getId() : null)
                .nomSite(m.getSite() != null ? m.getSite().getNom() : null)
                .soldeDu(m.getSoldeDu())
                .penaliteJusquAu(m.getPenaliteJusquAu())
                .build();
    }

    public Membre toEntity(MembreCreateRequest dto) {
        Membre membre = Membre.builder()
                .matricule(dto.getMatricule())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .email(dto.getEmail())
                .type(dto.getType())
                .soldeDu(dto.getSoldeDu() != null ? dto.getSoldeDu() : BigDecimal.ZERO)
                .build();
        if (dto.getSiteId() != null) {
            Site site = new Site();
            site.setId(dto.getSiteId());
            membre.setSite(site);
        }
        return membre;
    }

    public List<MembreResponse> getAllAsResponse() {
        return membreRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<MembreResponse> getBySiteIdAsResponse(Long siteId) {
        return membreRepository.findBySiteId(siteId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public MembreResponse getByIdAsResponse(Long id) {
        return toResponse(getById(id));
    }

    public MembreResponse createFromRequest(MembreCreateRequest dto) {
        return toResponse(create(toEntity(dto)));
    }
}