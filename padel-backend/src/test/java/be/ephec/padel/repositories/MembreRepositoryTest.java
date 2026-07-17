package be.ephec.padel.repositories;

import be.ephec.padel.models.Membre;
import be.ephec.padel.models.enums.TypeMembre;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MembreRepositoryTest {

    @Autowired
    private MembreRepository membreRepository;

    private Membre buildMembre(String matricule, String email, TypeMembre type) {
        return Membre.builder()
                .matricule(matricule)
                .nom("Dupont")
                .prenom("Jean")
                .email(email)
                .type(type)
                .soldeDu(BigDecimal.ZERO)
                .build();
    }

    @Test
    void findByMatricule_shouldReturnMembre() {
        membreRepository.save(buildMembre("G0001", "jean@test.be", TypeMembre.GLOBAL));
        Optional<Membre> result = membreRepository.findByMatricule("G0001");
        assertThat(result).isPresent();
        assertThat(result.get().getMatricule()).isEqualTo("G0001");
    }

    @Test
    void findByMatricule_shouldReturnEmpty_whenNotFound() {
        Optional<Membre> result = membreRepository.findByMatricule("X9999");
        assertThat(result).isEmpty();
    }

    @Test
    void findByEmail_shouldReturnMembre() {
        membreRepository.save(buildMembre("G0002", "marie@test.be", TypeMembre.GLOBAL));
        Optional<Membre> result = membreRepository.findByEmail("marie@test.be");
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("marie@test.be");
    }

    @Test
    void findTopByMatriculeStartingWithOrderByMatriculeDesc_shouldReturnLast() {
        membreRepository.save(buildMembre("G0001", "a@test.be", TypeMembre.GLOBAL));
        membreRepository.save(buildMembre("G0003", "b@test.be", TypeMembre.GLOBAL));
        membreRepository.save(buildMembre("G0002", "c@test.be", TypeMembre.GLOBAL));
        Optional<Membre> result = membreRepository
                .findTopByMatriculeStartingWithOrderByMatriculeDesc("G");
        assertThat(result).isPresent();
        assertThat(result.get().getMatricule()).isEqualTo("G0003");
    }

    @Test
    void findBySiteId_shouldReturnMembresForSite() {
        List<Membre> result = membreRepository.findBySiteId(99L);
        assertThat(result).isEmpty();
    }
}