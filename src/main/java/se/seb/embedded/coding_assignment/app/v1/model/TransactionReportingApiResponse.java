/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class TransactionReportingApiResponse {

    @Schema(
            example = "10",
            description = "Total number of files that were created based in the passed intput transactions.")
    private int totalNumberOfFiles;

    @Schema(
            example = "[1234_dg14-3561nd-81252e]",
            description = "Name of files that were created based in the passed intput transactions.")
    private List<String> fileNames;
}
