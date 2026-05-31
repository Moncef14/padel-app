package be.ephec.padel.membre;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembreRepository extends JpaRepository<Membre, Long> {

    List<Membre> findByType(TypeMembre type);

    List<Membre> findBySiteId(Long siteId);
}