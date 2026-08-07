package be.ephec.padel.controllers;
import be.ephec.padel.dto.DashboardStatsResponse;
import be.ephec.padel.dto.StatsParSiteResponse;
import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.services.StatsService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
// stats réservées aux administrateurs, jamais aux membres — les deux rôles y accèdent mais avec un périmètre différent (cf. getStats)
@PreAuthorize("hasAnyRole('ADMIN_GLOBAL', 'ADMIN_SITE')")
public class StatsController {

    private final StatsService statsService;
    private final JwtUtil jwtUtil;

    public StatsController(StatsService statsService, JwtUtil jwtUtil) {
        this.statsService = statsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/dashboard")
    public DashboardStatsResponse getStats(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        String role = jwtUtil.extractRole(token);

        // Un ADMIN_SITE ne voit que les stats de son propre site
        Long siteId = "ADMIN_SITE".equals(role) ? jwtUtil.extractSiteId(token) : null;
        return statsService.getStats(siteId);
    }

    // comparaison entre sites : n'a de sens que pour ADMIN_GLOBAL, qui a une vue sur l'ensemble
    @GetMapping("/par-site")
    @PreAuthorize("hasRole('ADMIN_GLOBAL')")
    public List<StatsParSiteResponse> getStatsParSite() {
        return statsService.getStatsParSite();
    }
}
