package be.ephec.padel.repositories;
import be.ephec.padel.models.Terrain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TerrainRepository extends JpaRepository<Terrain, Long> {

    List<Terrain> findBySiteId(Long siteId);

    // verifie la contrainte d'unicite (numero, site) avant creation, pour renvoyer une erreur metier claire plutot qu'une violation SQL
    boolean existsByNumeroAndSiteId(Integer numero, Long siteId);
}