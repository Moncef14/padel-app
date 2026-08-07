package be.ephec.padel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
// active la lecture des @PreAuthorize posés sur les controllers (ex: AdministrateurController, StatsController) ;
// sans cette annotation, Spring Security ignore ces vérifications de rôle au niveau méthode
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF protège les sessions basées sur cookies ; ici l'auth est stateless via un token porté dans le header, donc sans objet
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // aucune HttpSession créée/utilisée : chaque requête s'authentifie seule via son JWT (cf. JwtFilter), pas de state serveur
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // login/register doivent rester accessibles sans token : c'est justement là qu'on l'obtient
                .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                // doc Swagger ouverte pour pouvoir tester l'API sans authentification préalable (cf. OpenApiConfig)
                .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                // liste des sites actifs nécessaire dès l'écran de login/inscription (choix du site), avant qu'un token existe
                .requestMatchers(HttpMethod.GET, "/api/sites/actifs").permitAll()
                // tout le reste de l'API exige un token valide ; les restrictions plus fines par rôle sont posées via @PreAuthorize dans les controllers
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
                .anyRequest().authenticated()
            )
            // JwtFilter doit s'exécuter avant le filtre d'auth standard de Spring, pour que le SecurityContext soit déjà peuplé
            // quand authorizeHttpRequests évalue authenticated()/hasRole()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // le frontend Angular (autre port, ex: 4200) et le backend Spring (ex: 8080) sont deux origines distinctes pour le navigateur ;
    // sans cette config CORS, le navigateur bloquerait les appels fetch/HttpClient du frontend vers l'API (same-origin policy)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}