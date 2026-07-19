package be.ephec.padel.controllers;

import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.security.SecurityConfig;
import be.ephec.padel.services.AdministrateurService;
import be.ephec.padel.services.MatchService;
import be.ephec.padel.services.MembreService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchController.class)
@Import(SecurityConfig.class)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchService matchService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AdministrateurService administrateurService;

    @MockBean
    private MembreService membreService;

    @Test
    @WithAnonymousUser
    void getPublics_shouldReturn403_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/matchs/publics"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void create_shouldReturn403_whenNoToken() throws Exception {
        mockMvc.perform(post("/api/matchs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"terrainId\":1,\"organisateurId\":1,\"dateHeure\":\"2026-09-01T10:00:00\",\"type\":\"PRIVE\"}"))
                .andExpect(status().isForbidden());
    }
}