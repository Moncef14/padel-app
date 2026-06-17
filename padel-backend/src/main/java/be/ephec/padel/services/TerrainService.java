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
        return terrainRepository.save(terrain);
    }

    public Terrain update(Long id, Terrain terrain) {
        Terrain existing = getById(id);
        existing.setNumero(terrain.getNumero());
        existing.setSite(terrain.getSite());
        return terrainRepository.save(existing);
    }

    public void delete(Long id) {
        terrainRepository.deleteById(id);
    }
}