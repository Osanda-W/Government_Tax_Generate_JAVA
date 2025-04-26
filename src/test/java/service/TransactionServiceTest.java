package service;

import org.iit.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {
    private TransactionService transactionService;
    private List<Transaction> testTransactions;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
        testTransactions = new ArrayList<>(List.of(
                new Transaction("B001", "ITEM1", 10.0, 15.0, 2, 0.0, 62),
                new Transaction("B002", "ITEM2", 20.0, 25.0, 3, 5.0, 85),
                new Transaction("B003", "ITEM@3", 15.0, 20.0, 1, 0.0, 0) // Invalid checksum
        ));

        // Set statuses explicitly
        testTransactions.get(0).setStatus("Valid");
        testTransactions.get(1).setStatus("Valid");
        testTransactions.get(2).setStatus("Invalid: Checksum mismatch");
    }

    @Test
    void testImportTransactionsFromFile() throws IOException {
        // Create a temporary test file
        String testData = "B001,ITEM1,10.0,15.0,2,0.0,62\n" +
                "B002,ITEM2,20.0,25.0,3,5.0,85\n";
        Path tempFile = Files.createTempFile("transactions", ".csv");
        Files.write(tempFile, testData.getBytes());

        List<Transaction> imported = transactionService.importTransactionsFromFile(tempFile.toString());
        assertEquals(2, imported.size());
        assertEquals("B001", imported.get(0).getBillNumber());
        assertEquals(15.0, imported.get(0).getSalePrice());

        Files.delete(tempFile);
    }

    @Test
    void testValidateTransactions() {
        // Reset statuses for this test
        testTransactions.forEach(t -> t.setStatus("Not validated"));

        transactionService.validateTransactions(testTransactions);

        assertEquals("Valid", testTransactions.get(0).getStatus());
        assertTrue(testTransactions.get(1).getStatus().startsWith("Invalid"));
        assertTrue(testTransactions.get(2).getStatus().startsWith("Invalid"));
    }

    @Test
    void testCalculateChecksum() {
        Transaction transaction = testTransactions.get(0);
        int checksum = transactionService.calculateChecksum(transaction);
        assertEquals(62, checksum);
    }

    @Test
    void testCalculateProfits() {
        transactionService.calculateProfits(testTransactions);

        // Transaction 1: (15*2) - (10*2) = 30 - 20 = 10.0
        assertEquals(10.0, testTransactions.get(0).getProfit(), 0.001);

        // Transaction 2: (25*3*0.95) - (20*3) = 71.25 - 60 = 11.25
        assertEquals(11.25, testTransactions.get(1).getProfit(), 0.001);

        // Transaction 3: (20*1) - (15*1) = 5.0 (but invalid)
        assertEquals(5.0, testTransactions.get(2).getProfit(), 0.001);
    }

    @Test
    void testRemoveZeroProfitTransactions() {
        // Add a zero-profit transaction
        Transaction zeroProfit = new Transaction("B004", "ITEM4", 10.0, 10.0, 1, 0.0, 50);
        zeroProfit.setStatus("Valid");
        testTransactions.add(zeroProfit);

        transactionService.calculateProfits(testTransactions);
        int initialSize = testTransactions.size();
        transactionService.removeZeroProfitTransactions(testTransactions);

        assertEquals(initialSize - 1, testTransactions.size());
        assertFalse(testTransactions.contains(zeroProfit));
    }

    @Test
    void testCalculateTax() {
        // Calculate profits first
        transactionService.calculateProfits(testTransactions);

        // Only valid transactions should be considered for tax
        TransactionService.TaxCalculationResult result =
                transactionService.calculateTax(testTransactions, 10.0);

        // Valid transactions are at indices 0 and 1 with profits 10.0 and 11.25
        assertEquals(21.25, result.getTotalProfit(), 0.001);
        assertEquals(0.0, result.getTotalLoss(), 0.001);
        assertEquals(21.25, result.getNetProfit(), 0.001);
        assertEquals(2.125, result.getTax(), 0.001);
    }
}