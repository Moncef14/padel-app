package be.ephec.padel.models;

import be.ephec.padel.models.Site;
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