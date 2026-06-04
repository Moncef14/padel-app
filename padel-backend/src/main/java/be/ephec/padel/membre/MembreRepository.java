package be.ephec.padel.membre;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembreRepository extends JpaRepository<Membre, Long> {

    List<Membre> findByType(TypeMembre type);

    List<Membre> findBySiteId(Long siteId);

    Optional<Membre> findByMatricule(String matricule);
}