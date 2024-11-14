/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.error.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public class BadRequestApiError extends ApiError {

    private List<ErrorDetail> errors;
}
