package com.jonbake.pointsapp;

import com.jonbake.pointsapp.dto.TimestampedTransaction;
import com.jonbake.pointsapp.exceptions.NegativePayerPointsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class TransactionsStoreTest {

    @BeforeEach
    public void beforeAll () throws NegativePayerPointsException {
        TransactionsStore.INSTANCE.clear(1);
        List<TimestampedTransaction> transactions = List.of(
                new TimestampedTransaction("DANNON", 1000, Instant.parse("2020-11-02T14:00:00Z")),
                new TimestampedTransaction("UNILEVER", 200, Instant.parse("2020-10-31T11:00:00Z")),
                new TimestampedTransaction("DANNON", -200, Instant.parse("2020-10-31T15:00:00Z")),
                new TimestampedTransaction("MILLER COORS", 10000, Instant.parse("2020-11-01T14:00:00Z")),
                new TimestampedTransaction("DANNON", 300, Instant.parse("2020-10-31T10:00:00Z"))
        );
        TransactionsStore.INSTANCE.addAll(1, transactions);
    }

    @Test
    public void addShouldThrowsNegativePayerPointsException () {
       assertThrowsExactly(NegativePayerPointsException.class, () -> {
           TransactionsStore.INSTANCE.add(1, new TimestampedTransaction("UNILEVER", -300, Instant.parse("2020-10-31T11:00:00Z")));
       });
    }

    @Test
    public void getOrderedTransactionsShouldOrderTransactionsByTimestamp () {
        assertEquals(
                List.of(
                        new TimestampedTransaction("DANNON", 300, Instant.parse("2020-10-31T10:00:00Z")),
                        new TimestampedTransaction("UNILEVER", 200, Instant.parse("2020-10-31T11:00:00Z")),
                        new TimestampedTransaction("DANNON", -200, Instant.parse("2020-10-31T15:00:00Z")),
                        new TimestampedTransaction("MILLER COORS", 10000, Instant.parse("2020-11-01T14:00:00Z")),
                        new TimestampedTransaction("DANNON", 1000, Instant.parse("2020-11-02T14:00:00Z"))
                ),
                TransactionsStore.INSTANCE.getOrderedTransactions(1)
        );
    }
}