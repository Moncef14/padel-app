package be.ephec.padel.controllers;

import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.security.SecurityConfig;
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

@WebMvcTest(MembreController.class)
@Import(SecurityConfig.class)
class MembreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MembreService membreService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithAnonymousUser
    void getAll_shouldReturn403_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/membres"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void create_shouldReturn403_whenNoToken() throws Exception {
        mockMvc.perform(post("/api/membres")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"matricule\":\"G0011\",\"nom\":\"Test\",\"prenom\":\"User\",\"email\":\"test@test.be\",\"type\":\"GLOBAL\"}"))
                .andExpect(status().isForbidden());
    }
}