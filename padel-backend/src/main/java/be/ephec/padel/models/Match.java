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

    // EN_ATTENTE/COMPLET/ANNULE/DEVENU_PUBLIC : un match PRIVE non complet a l'approche de la date bascule en DEVENU_PUBLIC
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMatch statut;

    // prix fixe d'un terrain reserve (4 joueurs) ; sert de base au partage des frais dans InscriptionMatch
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal montantTotal = new BigDecimal("60");

    // renseigne uniquement quand un match PRIVE devient DEVENU_PUBLIC faute de joueurs ; trace le delai de bascule
    private LocalDateTime devenuPublicLe;
}