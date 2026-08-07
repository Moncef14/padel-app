package be.ephec.padel.models;

import be.ephec.padel.models.Site;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
// evite de dupliquer une meme fermeture pour un site donne
@Table(name = "jours_fermeture", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date", "site_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourFermeture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    // nullable : une fermeture sans site s'applique a tous les sites (ex: jour ferie national)
    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
}