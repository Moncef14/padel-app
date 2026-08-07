package be.ephec.padel.models;
import be.ephec.padel.models.enums.TypeMembre;

import be.ephec.padel.models.Site;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "membres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String matricule;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String email;

    // nullable : les membres crees via import/migration n'ont pas encore defini de mot de passe
    @Column(nullable = true)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeMembre type;

    // pas de site pour GLOBAL (acces tous sites) ni LIBRE (pas encore affilie) ; obligatoire seulement pour SITE
    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    // cumul des impayes ; sert de base au calcul de penalite plutot que de bloquer l'inscription immediatement
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal soldeDu = BigDecimal.ZERO;

    // date de fin de suspension suite a un impaye ; null = pas de penalite en cours
    private LocalDate penaliteJusquAu;
}