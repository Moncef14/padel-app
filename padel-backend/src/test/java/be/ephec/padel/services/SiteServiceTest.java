package be.ephec.padel.services;
import be.ephec.padel.repositories.SiteRepository;
import be.ephec.padel.services.SiteService;
import be.ephec.padel.models.Site;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SiteServiceTest {

    @Mock
    private SiteRepository siteRepository;

    @InjectMocks
    private SiteService siteService;

    private Site buildSite(Long id, String nom) {
        return Site.builder()
                .id(id)
                .nom(nom)
                .heureOuverture(LocalTime.of(8, 0))
                .heureFermeture(LocalTime.of(22, 0))
                .annee(2025)
                .actif(true)
                .build();
    }

    @Test
    void getAll_shouldReturnAllSites() {
        Site s1 = buildSite(1L, "Bruxelles");
        Site s2 = buildSite(2L, "Liège");
        when(siteRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Site> result = siteService.getAll();

        assertThat(result).hasSize(2).containsExactly(s1, s2);
    }

    @Test
    void getById_shouldReturnSite_whenExists() {
        Site site = buildSite(1L, "Bruxelles");
        when(siteRepository.findById(1L)).thenReturn(Optional.of(site));

        Site result = siteService.getById(1L);

        assertThat(result).isEqualTo(site);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(siteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> siteService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_shouldSaveAndReturnSite() {
        Site site = buildSite(null, "Namur");
        Site saved = buildSite(3L, "Namur");
        when(siteRepository.save(site)).thenReturn(saved);

        Site result = siteService.create(site);

        assertThat(result.getId()).isEqualTo(3L);
        verify(siteRepository).save(site);
    }

    @Test
    void delete_shouldCallDeleteById() {
        siteService.delete(1L);

        verify(siteRepository).deleteById(1L);
    }
}