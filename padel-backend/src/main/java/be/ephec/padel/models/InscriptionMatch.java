package be.ephec.padel.models;
import be.ephec.padel.models.enums.StatutPaiement;

import be.ephec.padel.models.Match;
import be.ephec.padel.models.Membre;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscription_matchs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"match_id", "membre_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscriptionMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statutPaiement;

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal montantPaye = BigDecimal.ZERO;

    private LocalDateTime datePaiement;
}