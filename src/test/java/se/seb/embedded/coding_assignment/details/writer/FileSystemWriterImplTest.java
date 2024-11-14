/* (C)2024 */
package se.seb.embedded.coding_assignment.details.writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.seb.embedded.coding_assignment.core.service.model.Status.FAILURE;
import static se.seb.embedded.coding_assignment.core.service.model.Status.SUCCESS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.seb.embedded.coding_assignment.core.service.model.ReportingTransaction;
import se.seb.embedded.coding_assignment.core.writer.model.FileSystemWriterServiceResponse;
import se.seb.embedded.coding_assignment.details.writer.helper.XmlWriterHelper;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.Document;

@Tag("unit")
@ExtendWith(SpringExtension.class)
public class FileSystemWriterImplTest {

    @Mock
    private Tracer tracer;

    @Mock
    private XmlWriterHelper xmlWriterHelper;

    @Mock
    private Span span;

    @Mock
    private TraceContext traceContext;

    @InjectMocks
    private FileSystemWriterImpl fileSystemWriterImpl;

    private ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @DisplayName("Junit: Details: Positive flow test. : Happy flow")
    @SneakyThrows
    void testWriterImplReturnsValidResponse() {
        List<ReportingTransaction> reportTransactions = getJsonContentFromFile("json/valid_reporting_transaction.json");

        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("test_trace");

        doNothing().when(xmlWriterHelper).writeXmlToFileSystem(anyString(), any(Document.class));

        FileSystemWriterServiceResponse response =
                fileSystemWriterImpl.processAndWriteXmlFilesToFileSystem(reportTransactions);

        assertEquals(SUCCESS, response.status(), "Ststus does not match");
        assertEquals(SUCCESS.name(), response.errorMessage(), "Message does not match");
        assertEquals(4, response.fileNames().size());

        verify(tracer).currentSpan();
        verify(span).context();
        verify(traceContext).traceId();
    }

    @Test
    @DisplayName("Junit: Details: Negative flow test. : Failure response flow")
    @SneakyThrows
    void testWriterImplReturnsFailureResponse() {
        List<ReportingTransaction> reportTransactions = getJsonContentFromFile("json/valid_reporting_transaction.json");

        when(tracer.currentSpan()).thenReturn(span);
        when(span.context()).thenReturn(traceContext);
        when(traceContext.traceId()).thenReturn("test_trace");

        doThrow(new RuntimeException("Exception from testWriterImplReturnsFailureResponse."))
                .when(xmlWriterHelper)
                .writeXmlToFileSystem(anyString(), any(Document.class));

        FileSystemWriterServiceResponse response =
                fileSystemWriterImpl.processAndWriteXmlFilesToFileSystem(reportTransactions);

        assertEquals(FAILURE, response.status(), "Ststus does not match");
        assertEquals(
                "Exception from testWriterImplReturnsFailureResponse.",
                response.errorMessage(),
                "Message does not match");
        assertEquals(0, response.fileNames().size());

        verify(tracer).currentSpan();
        verify(span).context();
        verify(traceContext).traceId();
    }

    @SneakyThrows
    private List<ReportingTransaction> getJsonContentFromFile(String fileName) {
        String inputData = new ClassPathResource(fileName).getContentAsString(StandardCharsets.UTF_8);
        return mapper.readValue(inputData, new TypeReference<List<ReportingTransaction>>() {});
    }
}
