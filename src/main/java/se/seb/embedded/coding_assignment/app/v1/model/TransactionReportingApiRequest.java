/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class TransactionReportingApiRequest {

    // TODO Some restrictions to be put here for allowable number of transaction.
    // APIs are not usually good at batch operations. Could lead to resource exploitation.
    @NotEmpty
    @Schema(description = "List of transactions that will be submitted for processing.")
    List<@Valid Transaction> transactions;
}
