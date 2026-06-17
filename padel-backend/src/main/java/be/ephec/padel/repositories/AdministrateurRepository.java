package be.ephec.padel.repositories;
import be.ephec.padel.models.Administrateur;
import be.ephec.padel.models.enums.RoleAdmin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdministrateurRepository extends JpaRepository<Administrateur, Long> {

    Optional<Administrateur> findByEmail(String email);

    List<Administrateur> findByRole(RoleAdmin role);

    List<Administrateur> findBySiteId(Long siteId);
}