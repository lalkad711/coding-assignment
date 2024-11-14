/* (C)2024 */
package se.seb.embedded.coding_assignment.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.seb.embedded.coding_assignment.core.service.model.Status.SUCCESS;

import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.seb.embedded.coding_assignment.core.service.model.ReportingTransaction;
import se.seb.embedded.coding_assignment.core.service.model.TransReportingServiceResponse;
import se.seb.embedded.coding_assignment.core.writer.FileSystemWriter;
import se.seb.embedded.coding_assignment.core.writer.model.FileSystemWriterServiceResponse;

@Tag("unit")
@ExtendWith(SpringExtension.class)
public class TransactionReportingServiceTest {

    @Mock
    private FileSystemWriter fileSystemWriter;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private TransactionReportingService transactionReportingService;

    @Test
    @DisplayName("Junit: Service: Positive flow test. : Happy flow")
    @SneakyThrows
    void testProcessTransactionSuccessFlow() {

        List<ReportingTransaction> reportTransactions = Collections.emptyList();
        FileSystemWriterServiceResponse fileSystemWriterServiceResponse = mock(FileSystemWriterServiceResponse.class);
        TransReportingServiceResponse responseExpected =
                new TransReportingServiceResponse(SUCCESS, "", Collections.emptyList());

        when(fileSystemWriter.processAndWriteXmlFilesToFileSystem(reportTransactions))
                .thenReturn(fileSystemWriterServiceResponse);
        when(conversionService.convert(fileSystemWriterServiceResponse, TransReportingServiceResponse.class))
                .thenReturn(responseExpected);

        TransReportingServiceResponse responseActual =
                transactionReportingService.processTransactionReporting(reportTransactions);

        assertEquals(responseExpected, responseActual);
        verify(fileSystemWriter).processAndWriteXmlFilesToFileSystem(reportTransactions);
        verify(conversionService).convert(fileSystemWriterServiceResponse, TransReportingServiceResponse.class);
    }

    @Test
    @DisplayName("Junit: Service: Negative flow test. : Throws exception")
    @SneakyThrows
    void testProcessTransactionThrowsException() {

        List<ReportingTransaction> reportTransactions = Collections.emptyList();
        FileSystemWriterServiceResponse fileSystemWriterServiceResponse = mock(FileSystemWriterServiceResponse.class);

        when(fileSystemWriter.processAndWriteXmlFilesToFileSystem(reportTransactions))
                .thenThrow(new RuntimeException("Exception from testProcessTransactionThrowsException."));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> transactionReportingService.processTransactionReporting(reportTransactions));

        assertEquals("Exception from testProcessTransactionThrowsException.", ex.getMessage());

        verify(fileSystemWriter).processAndWriteXmlFilesToFileSystem(reportTransactions);
        verify(conversionService, times(0))
                .convert(fileSystemWriterServiceResponse, TransReportingServiceResponse.class);
    }
}
