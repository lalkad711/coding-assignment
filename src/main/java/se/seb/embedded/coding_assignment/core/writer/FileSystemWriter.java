/* (C)2024 */
package se.seb.embedded.coding_assignment.core.writer;

import java.util.List;
import se.seb.embedded.coding_assignment.core.service.model.ReportingTransaction;
import se.seb.embedded.coding_assignment.core.writer.model.FileSystemWriterServiceResponse;

/**
 * API for the File System writer. This defines the contract to be followed as per the core or domain. The
 * implementations can be found in the details package.
 */
public interface FileSystemWriter {

    public FileSystemWriterServiceResponse processAndWriteXmlFilesToFileSystem(
            List<ReportingTransaction> initTransactions);
}
