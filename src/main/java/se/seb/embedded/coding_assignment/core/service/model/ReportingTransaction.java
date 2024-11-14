/* (C)2024 */
package se.seb.embedded.coding_assignment.core.service.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReportingTransaction(
        BigDecimal amount,
        String accountNumber,
        TransactionType transType,
        LocalDate dateOfTransaction,
        String currency) {}
