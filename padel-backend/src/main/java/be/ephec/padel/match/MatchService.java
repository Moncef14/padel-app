package be.ephec.padel.match;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> getAll() {
        return matchRepository.findAll();
    }

    public Match getById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match non trouvé avec l'id : " + id));
    }

    public List<Match> getByStatut(StatutMatch statut) {
        return matchRepository.findByStatut(statut);
    }

    public List<Match> getByType(TypeMatch type) {
        return matchRepository.findByType(type);
    }

    public List<Match> getPublics() {
        return matchRepository.findByType(TypeMatch.PUBLIC);
    }

    public Match create(Match match) {
        return matchRepository.save(match);
    }

    public Match update(Long id, Match match) {
        Match existing = getById(id);
        existing.setTerrain(match.getTerrain());
        existing.setDateHeure(match.getDateHeure());
        existing.setType(match.getType());
        existing.setOrganisateur(match.getOrganisateur());
        existing.setStatut(match.getStatut());
        existing.setMontantTotal(match.getMontantTotal());
        existing.setDevenuPublicLe(match.getDevenuPublicLe());
        return matchRepository.save(existing);
    }

    public void delete(Long id) {
        matchRepository.deleteById(id);
    }
}