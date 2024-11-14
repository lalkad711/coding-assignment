/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static se.seb.embedded.coding_assignment.app.v1.api.TransactionReportingV1ApiSpec.REPORT_TRANSACTIONS_PATH;
import static se.seb.embedded.coding_assignment.core.service.model.Status.SUCCESS;

import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import se.seb.embedded.coding_assignment.app.v1.api.TransactionReportingV1Controller;
import se.seb.embedded.coding_assignment.app.v1.model.TransactionReportingApiResponse;
import se.seb.embedded.coding_assignment.core.service.TransactionReportingService;
import se.seb.embedded.coding_assignment.core.service.model.TransReportingServiceResponse;

@Tag("unit")
@ActiveProfiles("test")
@WebMvcTest(TransactionReportingV1Controller.class)
public class TransactionReportingV1ControllerTest {

    @Autowired
    MockMvc mockMvc;

    @SpyBean
    private ConversionService conversionService;

    @MockBean
    private TransactionReportingService transactionReportingService;

    @MockBean
    private HttpServletResponse response;

    @Test
    @DisplayName("Junit: Controller: Positive flow test. : Happy flow")
    @SneakyThrows
    void testPositiveFlow() {

        TransReportingServiceResponse succSerResp = mock(TransReportingServiceResponse.class);
        TransactionReportingApiResponse apiResp = new TransactionReportingApiResponse(4, List.of("1", "2", "3", "4"));

        when(transactionReportingService.processTransactionReporting(anyList())).thenReturn(succSerResp);
        when(succSerResp.status()).thenReturn(SUCCESS);
        when(conversionService.convert(succSerResp, TransactionReportingApiResponse.class))
                .thenReturn(apiResp);

        mockMvc.perform(post(REPORT_TRANSACTIONS_PATH)
                        .content(getJsonContentFromFile("json/valid_reporting_transaction_api_request.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.totalNumberOfFiles").value(4),
                        jsonPath("$.fileNames.length()").value(4),
                        jsonPath("$.fileNames.[0]").value("1"),
                        jsonPath("$.fileNames.[1]").value("2"),
                        jsonPath("$.fileNames.[2]").value("3"),
                        jsonPath("$.fileNames.[3]").value("4"));

        verify(transactionReportingService).processTransactionReporting(anyList());
    }

    @Test
    @DisplayName("Junit: Controller: Negative flow test. : Invalid request params 1")
    @SneakyThrows
    void testInvalidRequestParams1() {
        mockMvc.perform(post(REPORT_TRANSACTIONS_PATH)
                        .content(getJsonContentFromFile("json/invalid_request_params1.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.path").value(REPORT_TRANSACTIONS_PATH),
                        jsonPath("$.title").value("BAD_REQUEST"),
                        jsonPath("$.status").value(400),
                        jsonPath("$.timestamp").exists(),
                        jsonPath("$.errors.length()").value(4),
                        jsonPath("$.errors[*].code", hasItems("Positive", "NotEmpty", "NotNull", "Pattern")),
                        jsonPath(
                                "$.errors[*].field",
                                hasItems(
                                        "transactions[0].amount",
                                        "transactions[0].accountNumber",
                                        "transactions[0].dateOfTransaction",
                                        "transactions[0].currency")),
                        jsonPath(
                                "$.errors[*].message",
                                hasItems(
                                        "Amount value should be a positive value.",
                                        "must not be empty",
                                        "must not be null",
                                        "Only SEK allowed currently.")));
        verify(transactionReportingService, times(0)).processTransactionReporting(anyList());
    }

    @Test
    @DisplayName("Junit: Controller: Negative flow test. : Invalid request params 2")
    @SneakyThrows
    void testInvalidRequestParams2() {
        mockMvc.perform(post(REPORT_TRANSACTIONS_PATH)
                        .content(getJsonContentFromFile("json/invalid_request_params2.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.path").value(REPORT_TRANSACTIONS_PATH),
                        jsonPath("$.title").value("BAD_REQUEST"),
                        jsonPath("$.status").value(400),
                        jsonPath("$.timestamp").exists(),
                        jsonPath("$.errors.length()").value(3),
                        jsonPath("$.errors[*].code", hasItems("NotEmpty", "NotNull", "Pattern")),
                        jsonPath(
                                "$.errors[*].field",
                                hasItems(
                                        "transactions[0].amount",
                                        "transactions[0].accountNumber",
                                        "transactions[0].currency")),
                        jsonPath(
                                "$.errors[*].message",
                                hasItems("must not be empty", "must not be null", "Only SEK allowed currently.")));
        verify(transactionReportingService, times(0)).processTransactionReporting(anyList());
    }

    @Test
    @DisplayName("Junit: Controller: Negative flow test. : Internal server error")
    @SneakyThrows
    void testInternalServerError() {

        when(transactionReportingService.processTransactionReporting(anyList())).thenThrow(new RuntimeException());

        mockMvc.perform(post(REPORT_TRANSACTIONS_PATH)
                        .content(getJsonContentFromFile("json/valid_reporting_transaction_api_request.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().is5xxServerError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.path").value(REPORT_TRANSACTIONS_PATH),
                        jsonPath("$.title").value("INTERNAL_SERVER_ERROR"),
                        jsonPath("$.status").value(500),
                        jsonPath("$.timestamp").exists());
        verify(transactionReportingService).processTransactionReporting(anyList());
    }

    @SneakyThrows
    private String getJsonContentFromFile(String fileName) {
        return new ClassPathResource(fileName).getContentAsString(StandardCharsets.UTF_8);
    }
}
