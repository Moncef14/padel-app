package be.ephec.padel.models;
import be.ephec.padel.models.enums.RoleAdmin;

import be.ephec.padel.models.Site;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Administrateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleAdmin role;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
}