package ftms.svc.accounts.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ftms.svc.accounts.api.constants.FtmsAccountType;
import ftms.svc.accounts.api.controller.FtmsAccountsController;
import ftms.svc.accounts.api.domain.FtmsAccountResult;
import ftms.svc.accounts.api.domain.FtmsAccountStatus;
import ftms.svc.accounts.api.domain.FtmsCreateAccount;
import ftms.svc.accounts.api.domain.service.FtmsAccountService;
import ftms.svc.accounts.api.infrastructure.exception.BusinessRuleValidationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FtmsAccountsController.class)
class FtmsAccountsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    FtmsAccountService ftmsAccountService;



    /**
     * ✅ 201 CREATED : Happy path
     */
    @Test
    void shouldCreateAccountAndReturn201() throws Exception {

        FtmsCreateAccount request = new FtmsCreateAccount(
                "550e8400-e29b-41d4-a716-446655440002",
                FtmsAccountType.SAVINGS,
                "USD"
        );

        FtmsAccountResult mockResponse = FtmsAccountResult.builder()
                .uuid("bb02fb66-a82e-48fb-903a-632ddc664a5a")
                .customerId("550e8400-e29b-41d4-a716-446655440002")
                .accountType(FtmsAccountType.SAVINGS)
                .currency("USD")
                .balance(BigDecimal.ZERO)
                .status(FtmsAccountStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(ftmsAccountService.createFtmsAccount(any()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/accounts"))
                .andExpect(jsonPath("$.uuid").value(mockResponse.getUuid()))
                .andExpect(jsonPath("$.customerId").value(request.getCustomerId()))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(ftmsAccountService).createFtmsAccount(any());
    }


    /**
     * ❌ 400 BAD REQUEST (Validation failure)
     */
    @Test
    void shouldReturn400WhenRequestInvalid() throws Exception {

        String invalidJson = """
                {
                  "customerId": "",
                  "accountType": "",
                  "currency": ""
                }
                """;

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(ftmsAccountService);
    }


    /**
     * ❌ Business exception (customer not found)
     */
    @Test
    void shouldReturnErrorWhenCustomerNotFound() throws Exception {

        FtmsCreateAccount request = new FtmsCreateAccount(
                "550e8400-e29b-41d4-a716-446655440002",
                FtmsAccountType.SAVINGS,
                "USD"
        );

        when(ftmsAccountService.createFtmsAccount(any()))
                .thenThrow(new BusinessRuleValidationException("Customer not found", "ERR_3001"));

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }


    /**
     * ✅ GET /api/v1/accounts/health
     */
    @Test
    void shouldReturnHealthStatus() throws Exception {
        mockMvc.perform(get("/api/v1/accounts/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("FTMS Accounts Service"))
                .andExpect(jsonPath("$.version").value("v1"));
    }
}
