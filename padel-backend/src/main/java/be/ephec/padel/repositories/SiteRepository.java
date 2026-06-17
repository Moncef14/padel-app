package be.ephec.padel.repositories;
import be.ephec.padel.models.Site;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {

    List<Site> findByActifTrue();
}