package be.ephec.padel.models;
import be.ephec.padel.models.enums.TypeMatch;
import be.ephec.padel.models.enums.StatutMatch;

import be.ephec.padel.models.Membre;
import be.ephec.padel.models.Terrain;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "matchs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "terrain_id", nullable = false)
    private Terrain terrain;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMatch type;

    @ManyToOne
    @JoinColumn(name = "organisateur_id", nullable = false)
    private Membre organisateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMatch statut;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal montantTotal = new BigDecimal("60");

    private LocalDateTime devenuPublicLe;
}