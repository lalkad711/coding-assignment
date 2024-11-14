/* (C)2024 */
package se.seb.embedded.coding_assignment.core.service.model;

import java.util.List;

public record TransReportingServiceResponse(Status status, String errorMessage, List<String> fileNames) {}
