package be.ephec.padel.controllers;

import be.ephec.padel.repositories.MembreRepository;
import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.security.SecurityConfig;
import be.ephec.padel.services.AdministrateurService;
import be.ephec.padel.services.MembreService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdministrateurService administrateurService;

    @MockBean
    private MembreRepository membreRepository;

    @MockBean
    private MembreService membreService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void login_shouldReturn401_whenBadCredentials() throws Exception {
        when(administrateurService.getByEmail(any()))
                .thenThrow(new RuntimeException("Non trouvé"));
        mockMvc.perform(post("/api/auth/login")
                .with(org.springframework.security.test.web.servlet.request
                    .SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"bad@test.be\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginMembre_shouldReturn401_whenMatriculeNotFound() throws Exception {
        when(membreRepository.findByMatricule(any())).thenReturn(Optional.empty());
        mockMvc.perform(post("/api/auth/membre")
                .with(org.springframework.security.test.web.servlet.request
                    .SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"matricule\":\"X9999\",\"motDePasse\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_shouldReturn400_whenEmailAlreadyExists() throws Exception {
        when(membreService.inscrire(any()))
                .thenThrow(new RuntimeException("Email déjà utilisé"));
        mockMvc.perform(post("/api/auth/register")
                .with(org.springframework.security.test.web.servlet.request
                    .SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Test\",\"prenom\":\"User\",\"email\":\"exists@test.be\",\"motDePasse\":\"pass\",\"type\":\"GLOBAL\"}"))
                .andExpect(status().isBadRequest());
    }
}