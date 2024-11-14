/* (C)2024 */
package se.seb.embedded.coding_assignment.details.writer;

import static se.seb.embedded.coding_assignment.core.service.model.Status.FAILURE;
import static se.seb.embedded.coding_assignment.core.service.model.Status.SUCCESS;

import io.micrometer.tracing.Tracer;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import se.seb.embedded.coding_assignment.core.service.model.ReportingTransaction;
import se.seb.embedded.coding_assignment.core.writer.FileSystemWriter;
import se.seb.embedded.coding_assignment.core.writer.model.FileSystemWriterServiceResponse;
import se.seb.embedded.coding_assignment.details.utils.Utils;
import se.seb.embedded.coding_assignment.details.writer.helper.XmlWriterHelper;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.Document;

@Log
@Service
@RequiredArgsConstructor
public class FileSystemWriterImpl implements FileSystemWriter {

    private final Tracer tracer;
    private final XmlWriterHelper xmlWriterHelper;
    /**
     * Implementation of the contract defined by core {@link FileSystemWriter}. Groups, aggregates and then writes the
     * supplied list of transactions to a valid xml file defined by the xsd
     */
    @Override
    public FileSystemWriterServiceResponse processAndWriteXmlFilesToFileSystem(
            List<ReportingTransaction> reportTransactions) {

        // Segregate the transactions per date
        var documentsByDate = Utils.getDocumentsByDate(reportTransactions);

        try {
            List<String> fileNames = writeXmlFilesToFileSystem(documentsByDate);
            return new FileSystemWriterServiceResponse(SUCCESS, SUCCESS.name(), fileNames);
        } catch (RuntimeException e) {
            // In case of any exception return failure response
            log.log(Level.SEVERE, e, () -> "Something went wrong.");
            return new FileSystemWriterServiceResponse(FAILURE, e.getMessage(), Collections.emptyList());
        }
    }

    private List<String> writeXmlFilesToFileSystem(Map<LocalDate, Document> documentsByDate) {

        // Using the trace id from every request to trace files created from it.
        // Trace id will be included in the name of the xml files being written to file
        // system.
        String traceId = tracer.currentSpan().context().traceId();

        return documentsByDate.entrySet().parallelStream()
                .map(entry -> {
                    String name = Utils.getFileName(entry.getKey().toString(), traceId);
                    xmlWriterHelper.writeXmlToFileSystem(name, entry.getValue());
                    return name;
                })
                .toList();
    }
}
