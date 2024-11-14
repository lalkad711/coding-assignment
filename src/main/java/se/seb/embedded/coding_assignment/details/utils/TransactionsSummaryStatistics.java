/* (C)2024 */
package se.seb.embedded.coding_assignment.details.utils;

import static java.util.stream.Collectors.toMap;
import static se.seb.embedded.coding_assignment.core.service.model.TransactionType.CREDIT;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.ToString;
import se.seb.embedded.coding_assignment.core.service.model.ReportingTransaction;

/**
 * Consumer class that helps in calculating various statistics about the {@link ReportingTransaction} when used in
 * reduce operation on a stream.
 */
@Getter
@ToString
public class TransactionsSummaryStatistics implements Consumer<ReportingTransaction> {

    public static Collector<ReportingTransaction, ?, TransactionsSummaryStatistics> statistics() {
        return Collector.of(
                TransactionsSummaryStatistics::new,
                TransactionsSummaryStatistics::accept,
                TransactionsSummaryStatistics::merge);
    }

    private int totalNumberOfTranaction, numberOfCreditTransaction, numberOfDebitTransaction;
    private BigDecimal creditAmount = BigDecimal.ZERO;
    private BigDecimal debitAmount = BigDecimal.ZERO;
    private Map<String, TransactionSummary> transactionByAccountNumber = new HashMap<>();

    @Override
    public void accept(ReportingTransaction t) {
        Objects.requireNonNull(t);

        totalNumberOfTranaction++;

        TransactionSummary transactionSummary;
        if (t.transType() == CREDIT) {
            numberOfCreditTransaction++;
            creditAmount = creditAmount.add(t.amount());
            transactionSummary = new TransactionSummary(BigDecimal.ZERO, t.amount());

        } else {
            numberOfDebitTransaction++;
            debitAmount = debitAmount.add(t.amount());
            transactionSummary = new TransactionSummary(t.amount(), BigDecimal.ZERO);
        }
        transactionByAccountNumber.merge(t.accountNumber(), transactionSummary, (ts1, ts2) -> ts1.merge(ts2));
    }

    public TransactionsSummaryStatistics merge(TransactionsSummaryStatistics s) {
        Objects.requireNonNull(s);

        totalNumberOfTranaction += s.totalNumberOfTranaction;
        numberOfCreditTransaction += s.numberOfCreditTransaction;
        numberOfDebitTransaction += s.numberOfDebitTransaction;
        creditAmount = creditAmount.add(s.creditAmount);
        debitAmount = debitAmount.add(s.debitAmount);

        transactionByAccountNumber = Stream.concat(
                        transactionByAccountNumber.entrySet().stream(),
                        s.transactionByAccountNumber.entrySet().stream())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> value1.merge(value2)));

        return this;
    }
}
