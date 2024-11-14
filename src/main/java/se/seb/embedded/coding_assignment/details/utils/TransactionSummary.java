/* (C)2024 */
package se.seb.embedded.coding_assignment.details.utils;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionSummary {

    private BigDecimal debitAmount;
    private BigDecimal creditAmount;

    public TransactionSummary merge(TransactionSummary ts) {
        this.debitAmount = this.debitAmount.add(ts.getDebitAmount());
        this.creditAmount = this.creditAmount.add(ts.getCreditAmount());
        return this;
    }
}
