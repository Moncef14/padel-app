package be.ephec.padel.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// s'exécute une fois par requête, avant qu'elle n'atteigne un controller (branché dans SecurityConfig via addFilterBefore) :
// lit le header Authorization, valide le token et peuple le SecurityContext pour que @PreAuthorize/hasRole fonctionnent en aval
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // absent ou pas au format "Bearer <token>" : on ne peuple rien, la requête continue non authentifiée
        // et c'est SecurityConfig (authorizeHttpRequests) qui la rejettera en 401/403 si la route l'exige
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // token invalide (signature/expiration) : isTokenValid renvoie false, on ne peuple pas le contexte, même traitement que "absent"
            if (jwtUtil.isTokenValid(token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // pas de rôle dans le token (cas membre) : liste d'autorités vide → authentifié mais sans ROLE_*, donc bloqué par les hasRole()
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                if (role != null) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}