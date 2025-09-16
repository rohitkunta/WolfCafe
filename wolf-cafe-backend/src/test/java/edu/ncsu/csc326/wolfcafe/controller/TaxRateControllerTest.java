package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import edu.ncsu.csc326.wolfcafe.service.TaxRateService;

/**
 * Unit tests for the TaxRateController class, which handles updating the tax rate.
 * Uses MockMvc to simulate HTTP requests and validate responses.
 * @author Riana Victoria
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TaxRateControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TaxRateService taxRateService;

    private static final String API_PATH = "/api/taxRate/change";
    private static final String ENCODING = "utf-8";

    /**
     * Simulates a successful tax rate update request by an admin user.
     * Expects a 200 OK status and a success message.
     *
     * @throws Exception if the request fails
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testChangeTaxRateSuccess() throws Exception {
        double newTaxRate = 0.08;
        String responseMessage = "Tax rate successfully updated to 8.0%.";

        Mockito.when(taxRateService.changeTaxRate(newTaxRate)).thenReturn(responseMessage);

        mvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(ENCODING)
                .content(String.valueOf(newTaxRate))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(responseMessage));
    }

    /**
     * Simulates a tax rate update request with an invalid (negative) tax rate by an admin user.
     * Expects a 400 Bad Request status and an error message.
     *
     * @throws Exception if the request fails
     */
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testChangeTaxRateInvalid() throws Exception {
        double newTaxRate = -0.05;
        String errorMessage = "Tax rate cannot be negative.";

        Mockito.when(taxRateService.changeTaxRate(newTaxRate)).thenThrow(new IllegalArgumentException(errorMessage));

        mvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(ENCODING)
                .content(String.valueOf(newTaxRate))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(errorMessage));
    }

    /**
     * Simulates an unauthorized tax rate update request without a logged-in user.
     * Expects a 401 Unauthorized status.
     *
     * @throws Exception if the request fails
     */
    @Test
    public void testChangeTaxRateUnauthorized() throws Exception {
        double newTaxRate = 0.08;

        mvc.perform(post(API_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(ENCODING)
                .content(String.valueOf(newTaxRate))
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }
}
