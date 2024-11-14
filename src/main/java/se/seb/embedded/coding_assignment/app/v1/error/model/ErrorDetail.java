/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.error.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(NON_NULL)
public class ErrorDetail {

    @Schema(
            description = "A short error code. Could also be an ISO error code in case of payments.",
            example = "NotEmpty")
    private String code;

    @Schema(description = "Field associated with the error in context.", example = "transactions[0].amount")
    private String field;

    @Schema(
            description = "Message associated with the error giving more info about it.",
            example = "Amount value should be a positive value.")
    private String message;
}
