/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.seb.embedded.coding_assignment.app.v1.error.model.BadRequestApiError;
import se.seb.embedded.coding_assignment.app.v1.error.model.InternalServerApiError;
import se.seb.embedded.coding_assignment.app.v1.model.TransactionReportingApiRequest;
import se.seb.embedded.coding_assignment.app.v1.model.TransactionReportingApiResponse;

@Tag(name = "TransactionReportingV1", description = "Use this API to report multiple transactions into xml files.")
public interface TransactionReportingV1ApiSpec {

    public String REPORT_TRANSACTIONS_PATH = "/payment/reporting/transaction/v1/report";

    @Operation(
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content =
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = TransactionReportingApiRequest.class))),
            operationId = "reportPayments",
            summary = "Report multiple transaction and aggregate them into an xml file.",
            description =
                    "Post multiple transactions and then aggregate them per account number and per execution date and create xml file report for each execution date.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Successfully created.",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TransactionReportingApiResponse.class))
                        }),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad Request.",
                        content = {
                            @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(implementation = BadRequestApiError.class))
                        }),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error.",
                        content = {
                            @Content(
                                    mediaType = "application/problem+json",
                                    schema = @Schema(implementation = InternalServerApiError.class))
                        })
            })
    @PostMapping(
            path = REPORT_TRANSACTIONS_PATH,
            produces = {
                "application/json",
                "application/problem+json",
            },
            consumes = {"application/json"})
    public TransactionReportingApiResponse processTransactionsV1(
            @RequestBody @Valid TransactionReportingApiRequest reportRequest, final HttpServletResponse response);
}
