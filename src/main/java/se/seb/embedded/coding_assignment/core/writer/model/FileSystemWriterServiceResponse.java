/* (C)2024 */
package se.seb.embedded.coding_assignment.core.writer.model;

import java.util.List;
import se.seb.embedded.coding_assignment.core.service.model.Status;

public record FileSystemWriterServiceResponse(Status status, String errorMessage, List<String> fileNames) {}
