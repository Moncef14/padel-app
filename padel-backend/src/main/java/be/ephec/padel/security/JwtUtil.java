package be.ephec.padel.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class
JwtUtil {

    // clé symétrique HMAC : même secret pour signer et vérifier (côté serveur uniquement, jamais envoyé au frontend)
    private static final String SECRET = "padel-ephec-super-secret-key-256bits-minimum-32chars!!";
    // validité 24h : le token expire seul, pas besoin de mécanisme de révocation côté serveur (stateless)
    private static final long EXPIRATION_MS = 24L * 60 * 60 * 1000;

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // token "membre" : pas de rôle ni de site dans les claims — un membre n'a pas de droits d'administration à porter dans le token
    public String generateToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    // token "administrateur" : le rôle (ADMIN_GLOBAL/ADMIN_SITE) et le siteId sont embarqués dans le token
    // pour que chaque requête suivante sache qui appelle sans re-consulter la base (cf. JwtFilter, filtrage par site dans les controllers)
    public String generateToken(String subject, String role, Long siteId) {
        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .claim("siteId", siteId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) parseClaims(token).get("role");
    }

    public Long extractSiteId(String token) {
        Object siteId = parseClaims(token).get("siteId");
        if (siteId == null) return null;
        return ((Number) siteId).longValue();
    }

    // valide = signature correcte (via parseClaims, qui lève si altéré) ET non expiré ; toute exception (token malformé, signature invalide) = invalide
    public boolean isTokenValid(String token) {
        try {
            return !parseClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // parseSignedClaims revérifie la signature avec la clé locale : un token modifié ou signé avec une autre clé est rejeté ici
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}