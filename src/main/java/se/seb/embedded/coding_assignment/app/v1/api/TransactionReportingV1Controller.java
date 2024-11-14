/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.api;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static se.seb.embedded.coding_assignment.core.service.model.Status.SUCCESS;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import se.seb.embedded.coding_assignment.app.v1.model.TransactionReportingApiRequest;
import se.seb.embedded.coding_assignment.app.v1.model.TransactionReportingApiResponse;
import se.seb.embedded.coding_assignment.core.service.TransactionReportingService;
import se.seb.embedded.coding_assignment.core.service.model.ReportingTransaction;
import se.seb.embedded.coding_assignment.core.service.model.TransReportingServiceResponse;

@Log
@RestController
@RequiredArgsConstructor
public class TransactionReportingV1Controller implements TransactionReportingV1ApiSpec {

    private final ConversionService conversionService;
    private final TransactionReportingService transactionReportingService;

    @Override
    public TransactionReportingApiResponse processTransactionsV1(
            TransactionReportingApiRequest reportingRequest, final HttpServletResponse response) {
        log.info("Reporting request received");

        var serviceReportingReq = reportingRequest.getTransactions().stream()
                .map(trans -> new ReportingTransaction(
                        trans.getAmount(),
                        trans.getAccountNumber(),
                        trans.getTransType(),
                        trans.getDateOfTransaction(),
                        trans.getCurrency()))
                .toList();

        var serviceResponse = transactionReportingService.processTransactionReporting(serviceReportingReq);

        var apiResponse = handleServiceResponse(serviceResponse);

        log.info("Reporting request processed successfully.");
        return apiResponse;
    }

    private TransactionReportingApiResponse handleServiceResponse(TransReportingServiceResponse serviceResponse) {
        if (serviceResponse.status() == SUCCESS) {
            return conversionService.convert(serviceResponse, TransactionReportingApiResponse.class);
        } else {
            log.info("Reporting request processing failed.");
            throw new HttpServerErrorException(INTERNAL_SERVER_ERROR);
        }
    }
}
