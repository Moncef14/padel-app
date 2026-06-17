package be.ephec.padel.services;
import be.ephec.padel.repositories.MembreRepository;
import be.ephec.padel.services.MembreService;
import be.ephec.padel.models.Membre;
import be.ephec.padel.models.enums.TypeMembre;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembreServiceTest {

    @Mock
    private MembreRepository membreRepository;

    @InjectMocks
    private MembreService membreService;

    private Membre buildMembre(Long id, String matricule, TypeMembre type) {
        return Membre.builder()
                .id(id)
                .matricule(matricule)
                .nom("Dupont")
                .prenom("Alice")
                .email("alice@test.be")
                .type(type)
                .soldeDu(BigDecimal.ZERO)
                .build();
    }

    @Test
    void getAll_shouldReturnAllMembres() {
        Membre m1 = buildMembre(1L, "M001", TypeMembre.GLOBAL);
        Membre m2 = buildMembre(2L, "M002", TypeMembre.SITE);
        when(membreRepository.findAll()).thenReturn(List.of(m1, m2));

        List<Membre> result = membreService.getAll();

        assertThat(result).hasSize(2).containsExactly(m1, m2);
    }

    @Test
    void getById_shouldReturnMembre_whenExists() {
        Membre membre = buildMembre(1L, "M001", TypeMembre.GLOBAL);
        when(membreRepository.findById(1L)).thenReturn(Optional.of(membre));

        Membre result = membreService.getById(1L);

        assertThat(result).isEqualTo(membre);
    }

    @Test
    void getByType_shouldReturnMembresOfType() {
        Membre m1 = buildMembre(1L, "M001", TypeMembre.LIBRE);
        Membre m2 = buildMembre(2L, "M002", TypeMembre.LIBRE);
        when(membreRepository.findByType(TypeMembre.LIBRE)).thenReturn(List.of(m1, m2));

        List<Membre> result = membreService.getByType(TypeMembre.LIBRE);

        assertThat(result).hasSize(2).allMatch(m -> m.getType() == TypeMembre.LIBRE);
    }

    @Test
    void create_shouldSaveAndReturnMembre() {
        Membre membre = buildMembre(null, "M003", TypeMembre.SITE);
        Membre saved = buildMembre(3L, "M003", TypeMembre.SITE);
        when(membreRepository.save(membre)).thenReturn(saved);

        Membre result = membreService.create(membre);

        assertThat(result.getId()).isEqualTo(3L);
        verify(membreRepository).save(membre);
    }
}