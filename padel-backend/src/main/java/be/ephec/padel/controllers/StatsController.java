package be.ephec.padel.controllers;
import be.ephec.padel.dto.DashboardStatsResponse;
import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.services.StatsService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
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
}
