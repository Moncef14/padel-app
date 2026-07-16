package be.ephec.padel.dto;

import be.ephec.padel.models.enums.TypeMatch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchCreateRequest {
    private Long terrainId;
    private Long organisateurId;
    private LocalDateTime dateHeure;
    private TypeMatch type;
}