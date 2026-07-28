package be.ephec.padel.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Déclare le schéma d'authentification JWT pour Swagger UI, afin que le
// bouton "Authorize" apparaisse et permette de tester les routes protégées
// directement depuis la doc, sans passer par Postman.
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI padelOpenAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}