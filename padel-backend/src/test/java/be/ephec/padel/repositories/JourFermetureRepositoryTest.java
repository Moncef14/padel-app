package be.ephec.padel.repositories;

import be.ephec.padel.models.JourFermeture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class JourFermetureRepositoryTest {

    @Autowired
    private JourFermetureRepository jourFermetureRepository;

    @Test
    void existsBySiteIsNullAndDate_shouldReturnTrue_whenGlobalFermeture() {
        JourFermeture jf = JourFermeture.builder()
                .date(LocalDate.of(2026, 7, 21))
                .site(null)
                .build();
        jourFermetureRepository.save(jf);
        boolean result = jourFermetureRepository
                .existsBySiteIsNullAndDate(LocalDate.of(2026, 7, 21));
        assertThat(result).isTrue();
    }

    @Test
    void existsBySiteIsNullAndDate_shouldReturnFalse_whenNoFermeture() {
        boolean result = jourFermetureRepository
                .existsBySiteIsNullAndDate(LocalDate.of(2026, 12, 25));
        assertThat(result).isFalse();
    }
}