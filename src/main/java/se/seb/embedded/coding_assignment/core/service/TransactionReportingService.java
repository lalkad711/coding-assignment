/* (C)2024 */
package se.seb.embedded.coding_assignment.core.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import se.seb.embedded.coding_assignment.core.service.model.ReportingTransaction;
import se.seb.embedded.coding_assignment.core.service.model.TransReportingServiceResponse;
import se.seb.embedded.coding_assignment.core.writer.FileSystemWriter;
import se.seb.embedded.coding_assignment.core.writer.model.FileSystemWriterServiceResponse;

/**
 * Service class in <code>se.seb.embedded.coding_assignment.core</code> package is the main entry point to the domain
 * logic. The idea is to orchestrate all the business logic through this class for the functionality exposed by the
 * associated controller exposing the resource.
 */
@Log
@Service
@RequiredArgsConstructor
public class TransactionReportingService {

    private final FileSystemWriter fileSystemWriter;
    private final ConversionService conversionService;

    /**
     * Orchestrates the logic to process and write the input transactions to a well defined valid xml file.
     *
     * @param reportTransactions List of transactions to be processed and written to an xml file
     * @return Service response object with detail defining the contract between core and other layers.
     *     {@link TransReportingServiceResponse}
     */
    public TransReportingServiceResponse processTransactionReporting(List<ReportingTransaction> reportTransactions) {
        log.info(() -> "Servicing request for number of transactions : " + reportTransactions.size());

        FileSystemWriterServiceResponse fileSystemWriterServiceResponse =
                fileSystemWriter.processAndWriteXmlFilesToFileSystem(reportTransactions);

        log.info(() -> "Servicing request finished writing xml files for transactions.");

        return conversionService.convert(fileSystemWriterServiceResponse, TransReportingServiceResponse.class);
    }
}
