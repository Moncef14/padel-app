package be.ephec.padel.services;
import be.ephec.padel.models.Terrain;
import be.ephec.padel.repositories.TerrainRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TerrainService {

    private final TerrainRepository terrainRepository;

    public TerrainService(TerrainRepository terrainRepository) {
        this.terrainRepository = terrainRepository;
    }

    public List<Terrain> getAll() {
        return terrainRepository.findAll();
    }

    public Terrain getById(Long id) {
        return terrainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terrain non trouvé avec l'id : " + id));
    }

    public List<Terrain> getBySiteId(Long siteId) {
        return terrainRepository.findBySiteId(siteId);
    }

    public Terrain create(Terrain terrain) {
        // numérotation unique par site seulement (le terrain 1 peut exister sur plusieurs sites) — cf. contrainte d'unicité de l'entité
        if (terrainRepository.existsByNumeroAndSiteId(terrain.getNumero(), terrain.getSite().getId())) {
            throw new RuntimeException("Le terrain numéro " + terrain.getNumero() +
                " existe déjà pour ce site");
        }
        return terrainRepository.save(terrain);
    }

    public Terrain update(Long id, Terrain terrain) {
        Terrain existing = getById(id);

        // ne revérifie l'unicité que si numéro ou site changent, sinon le terrain se "collisionnerait" avec lui-même
        boolean changement = !existing.getNumero().equals(terrain.getNumero())
                || !existing.getSite().getId().equals(terrain.getSite().getId());

        if (changement && terrainRepository.existsByNumeroAndSiteId(terrain.getNumero(), terrain.getSite().getId())) {
            throw new RuntimeException("Le terrain numéro " + terrain.getNumero() +
                " existe déjà pour ce site");
        }

        existing.setNumero(terrain.getNumero());
        existing.setSite(terrain.getSite());
        return terrainRepository.save(existing);
    }

    public void delete(Long id) {
        terrainRepository.deleteById(id);
    }
}