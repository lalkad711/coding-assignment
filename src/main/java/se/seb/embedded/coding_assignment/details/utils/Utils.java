/* (C)2024 */
package se.seb.embedded.coding_assignment.details.utils;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import se.seb.embedded.coding_assignment.core.service.model.ReportingTransaction;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.AmountAndCurrency;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.Document;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.Header;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.ObjectFactory;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.Summary;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.TransactionDetail;
import se.seb.embedded.coding_assignment.details.xml.v1.generated.TransactionSummary;

/**
 * Helper methods for the xjb declarations. These methods are used by the jaxb plugin to map xsd object type to java
 * types.
 */
public class Utils {

    private static final ObjectFactory XML_OBJECT_FACTORY = new ObjectFactory();

    public static LocalDate parseDateForXml(String date) {
        return LocalDate.parse(date);
    }

    public static String printDateForXml(LocalDate date) {
        return date.toString();
    }

    /**
     * Prepare the file name by appending date and trace id which keeps it unique/request
     *
     * @param date execution date for the transactions in the xml file
     * @param traceId traceId from the current request context
     */
    public static String getFileName(String date, String traceId) {
        StringJoiner joiner = new StringJoiner("_");
        joiner.add(date);
        joiner.add(traceId);
        return joiner.toString();
    }

    /**
     * Builds fully qualified path to the write location
     *
     * @param fileName name of the xml file
     * @return {@link Path} fully qualified path for the xml file
     */
    public static Path buildPathToWriteLocation(String fileName, String outputDir) {
        return Paths.get(outputDir).resolve(fileName + ".xml");
    }

    /**
     * Segregates the transactions, both credit and debit, and creates a {@link Document} per execution date.
     *
     * @param initTransactions List of transactions to be reported.
     * @return Document per execution date. &lt;{@link Map}&lt;{@link LocalDate}, {@link Document}&gt;&gt;
     */
    public static Map<LocalDate, Document> getDocumentsByDate(List<ReportingTransaction> initTransactions) {
        return initTransactions.parallelStream()
                .collect(groupingBy(
                        ReportingTransaction::dateOfTransaction, collectingAndThen(toList(), transactions -> {
                            var document = XML_OBJECT_FACTORY.createDocument();

                            // We can safely do this since date field comes from Transaction object and we
                            // have grouped them by
                            // date so same for all in list
                            var header = createDocumentHeader(
                                    transactions.getFirst().dateOfTransaction(), transactions.size());
                            TransactionsSummaryStatistics transStats =
                                    transactions.parallelStream().collect(TransactionsSummaryStatistics.statistics());

                            var transSummary = createHeaderTransactionSummary(transStats);
                            header.setTransactionSummary(transSummary);

                            document.setHeader(header);
                            document.getTransactions().addAll(createDocumentTransactionSummary(transStats));

                            return document;
                        })));
    }

    /**
     * Create the header section for the document.
     *
     * @param date Execution date for the transactions
     * @param noOfTransaction Total number o transactions for supplied execution date
     * @return header object for the xml document
     */
    public static Header createDocumentHeader(LocalDate date, int noOfTransaction) {
        Header header = XML_OBJECT_FACTORY.createHeader();
        header.setDate(date);
        header.setFileId(UUID.randomUUID().toString());
        header.setNumberOfTransactions(noOfTransaction);
        return header;
    }

    // TODO improve this check: using mapstruct
    public static List<TransactionDetail> createDocumentTransactionSummary(TransactionsSummaryStatistics transStats) {
        return transStats.getTransactionByAccountNumber().entrySet().parallelStream()
                .map(e -> {
                    TransactionDetail detail = XML_OBJECT_FACTORY.createTransactionDetail();
                    detail.setAccountNumber(e.getKey());

                    AmountAndCurrency credit = XML_OBJECT_FACTORY.createAmountAndCurrency();
                    credit.setCurrency("SEK");
                    credit.setValue(e.getValue().getCreditAmount());
                    detail.setCredit(credit);

                    AmountAndCurrency debit = XML_OBJECT_FACTORY.createAmountAndCurrency();
                    debit.setCurrency("SEK");
                    debit.setValue(e.getValue().getDebitAmount());
                    detail.setDebit(debit);
                    return detail;
                })
                .toList();
    }

    // TODO Improve this: check using mapstruct
    public static TransactionSummary createHeaderTransactionSummary(TransactionsSummaryStatistics transStats) {

        TransactionSummary transSummary = XML_OBJECT_FACTORY.createTransactionSummary();

        Summary summaryDebit = new Summary();
        AmountAndCurrency amtAndCurrDebit = XML_OBJECT_FACTORY.createAmountAndCurrency();

        Summary summaryCredit = new Summary();
        AmountAndCurrency amtAndCurrCredit = XML_OBJECT_FACTORY.createAmountAndCurrency();

        summaryDebit.setNumberOfTransactions(transStats.getNumberOfDebitTransaction());
        amtAndCurrDebit.setCurrency("SEK");
        amtAndCurrDebit.setValue(transStats.getDebitAmount());
        summaryDebit.setTotalAmount(amtAndCurrDebit);

        summaryCredit.setNumberOfTransactions(transStats.getNumberOfCreditTransaction());
        amtAndCurrCredit.setCurrency("SEK");
        amtAndCurrCredit.setValue(transStats.getCreditAmount());
        summaryCredit.setTotalAmount(amtAndCurrCredit);

        transSummary.setCreditSummary(summaryCredit);
        transSummary.setDebitSummary(summaryDebit);

        return transSummary;
    }
}
