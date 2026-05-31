package be.ephec.padel.fermeture;

import be.ephec.padel.site.Site;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
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

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
}