package be.ephec.padel.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "sites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private LocalTime heureOuverture;

    @Column(nullable = false)
    private LocalTime heureFermeture;

    // saison de validite du site (ex: 2026) : permet de clore une saison sans supprimer l'historique
    @Column(nullable = false)
    private Integer annee;

    // desactive un site plutot que le supprimer, pour garder l'integrite des matchs/terrains passes
    @Column(nullable = false)
    private boolean actif;
}