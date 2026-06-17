package be.ephec.padel.models;

import be.ephec.padel.models.Site;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "terrains")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Terrain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numero;

    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;
}