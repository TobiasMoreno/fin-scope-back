package tobias.moreno.fin.scope.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tobias.moreno.fin.scope.dto.investments.CreateInvestmentDTO;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class InvestmentControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Test
    @WithMockUser(authorities = {"READ_BASIC"})
    void testGetDashboard() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(get("/api/investments/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.sections").isArray());
    }

    @Test
    @WithMockUser(authorities = {"READ_BASIC"})
    void testGetAllInvestments() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(get("/api/investments")
                        .param("page", "0")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @WithMockUser(authorities = {"WRITE_BASIC"})
    void testCreateInvestment() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        CreateInvestmentDTO createDto = CreateInvestmentDTO.builder()
                .symbol("TSLA")
                .name("TESLA INC")
                .quantity(new BigDecimal("5.0"))
                .lastPrice(new BigDecimal("250.00"))
                .build();

        mockMvc.perform(post("/api/investments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol").value("TSLA"))
                .andExpect(jsonPath("$.name").value("TESLA INC"))
                .andExpect(jsonPath("$.type").value("CEDEAR"));
    }

    @Test
    @WithMockUser(authorities = {"READ_BASIC"})
    void testGetSummary() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        mockMvc.perform(get("/api/investments/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.totalInvested").exists())
                .andExpect(jsonPath("$.totalCurrent").exists())
                .andExpect(jsonPath("$.totalGain").exists());
    }
}
