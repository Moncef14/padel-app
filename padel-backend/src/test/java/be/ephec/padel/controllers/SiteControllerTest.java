package be.ephec.padel.controllers;

import be.ephec.padel.security.JwtUtil;
import be.ephec.padel.security.SecurityConfig;
import be.ephec.padel.services.SiteService;

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

@WebMvcTest(SiteController.class)
@Import(SecurityConfig.class)
class SiteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SiteService siteService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithAnonymousUser
    void getAll_shouldReturn403_whenNoToken() throws Exception {
        mockMvc.perform(get("/api/sites"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void create_shouldReturn403_whenNoToken() throws Exception {
        mockMvc.perform(post("/api/sites")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Test Site\"}"))
                .andExpect(status().isForbidden());
    }
}