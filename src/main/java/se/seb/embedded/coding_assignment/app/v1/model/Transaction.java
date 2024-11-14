/* (C)2024 */
package se.seb.embedded.coding_assignment.app.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import se.seb.embedded.coding_assignment.core.service.model.TransactionType;

@Data
public class Transaction {

    // Good to check for the upper limit and adjust. BigDecimals are heavier in
    // terms of creation and processing.
    @Schema(example = "65489.124", description = "Transaction amount for the current transaction in context.")
    @Positive(message = "Amount value should be a positive value.") @NotNull private BigDecimal amount;

    // TODO What account number is this? BBAN or IBAN? Update constraints accordingly.
    // IBAN is a standard convention. SE IBANs are 26 chars.
    @Schema(
            example = "8327947851255892",
            description = "BBAN or Basic bank account number from which the money should be debited or credited to.")
    @NotEmpty
    private String accountNumber;

    @Schema(
            description =
                    "Defines the the type of transaction. When DEBIT, money is deducted from account and when CREDIT, money is credited or added to the account.")
    @NotNull private TransactionType transType;

    // TODO Check if we need time and zone information to unambiguously interpret date.
    @Schema(example = "2007-12-03", description = "A date without a time-zone in the ISO-8601 calendar system.")
    @NotNull private LocalDate dateOfTransaction;

    @Schema(example = "SEK", description = "Currency code denoting the currency of the transaction bein carried out.")
    @Pattern(regexp = "SEK", message = "Only SEK allowed currently.")
    private String currency;
}
