/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.error.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonInclude(NON_NULL)
public class ApiError {

    @Schema(
            description = "Url path on which the error was observed.",
            example = "/payment/reporting/transaction/v1/report")
    private String path;

    @Schema(description = "Title of the error observed", example = "BAD REQUEST")
    private String title;

    @Schema(
            description =
                    "Unique alphanumeric 26 digit refId for identification fo the error being associated to a specific request.",
            example = "pj0OjOkKWklP3zUuLULPTFvHFo")
    private String refid;

    @Schema(description = "Denoted http status code", example = "500")
    private int status;

    @Schema(description = "ISO standard time when the error happened.", example = "2024-11-13T10:49:01.5921235")
    private LocalDateTime timestamp;
}
