package org.iit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testTransactionProperties() {
        Transaction transaction = new Transaction("B001", "ITEM1", 10.0, 15.0, 2, 0.0, 0);

        assertEquals("B001", transaction.getBillNumber());
        assertEquals("ITEM1", transaction.getItemCode());
        assertEquals(10.0, transaction.getInternalPrice());
        assertEquals(15.0, transaction.getSalePrice());
        assertEquals(2, transaction.getQuantity());
        assertEquals(0.0, transaction.getDiscount());
        assertEquals(0, transaction.getChecksum());
        assertEquals(0.0, transaction.getProfit());
        assertEquals("Not validated", transaction.getStatus());
    }

    @Test
    void testSetters() {
        Transaction transaction = new Transaction("", "", 0, 0, 0, 0, 0);

        transaction.setBillNumber("B002");
        transaction.setItemCode("ITEM2");
        transaction.setInternalPrice(20.0);
        transaction.setSalePrice(25.0);
        transaction.setQuantity(3);
        transaction.setDiscount(5.0);
        transaction.setChecksum(100);
        transaction.setProfit(10.0);
        transaction.setStatus("Valid");

        assertEquals("B002", transaction.getBillNumber());
        assertEquals("ITEM2", transaction.getItemCode());
        assertEquals(20.0, transaction.getInternalPrice());
        assertEquals(25.0, transaction.getSalePrice());
        assertEquals(3, transaction.getQuantity());
        assertEquals(5.0, transaction.getDiscount());
        assertEquals(100, transaction.getChecksum());
        assertEquals(10.0, transaction.getProfit());
        assertEquals("Valid", transaction.getStatus());
    }
}