package be.ephec.padel.repositories;

import be.ephec.padel.models.enums.StatutPaiement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class InscriptionMatchRepositoryTest {

    @Autowired
    private InscriptionMatchRepository inscriptionMatchRepository;

    @Test
    void existsByMatchIdAndMembreId_shouldReturnFalse_whenNotInscrit() {
        boolean result = inscriptionMatchRepository
                .existsByMatchIdAndMembreId(99L, 99L);
        assertThat(result).isFalse();
    }

    @Test
    void countByMatchIdAndStatutPaiement_shouldReturnZero_whenNoInscriptions() {
        int count = inscriptionMatchRepository
                .countByMatchIdAndStatutPaiement(99L, StatutPaiement.PAYE);
        assertThat(count).isEqualTo(0);
    }
}