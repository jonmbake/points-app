package com.jonbake.pointsapp.controllers;

import com.jonbake.pointsapp.TransactionsStore;
import com.jonbake.pointsapp.dto.Points;
import com.jonbake.pointsapp.dto.TimestampedTransaction;
import com.jonbake.pointsapp.dto.Transaction;
import com.jonbake.pointsapp.exceptions.NegativePayerPointsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PointsControllerTest {
    private PointsController transactionController = new PointsController();

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
    public void getPointsBalanceShouldReturnPointBalanceByPayer () {
        Map<String, Integer> pointsBalance = transactionController.getPointsBalance(1);
        assertEquals(
                Map.of(
                        "UNILEVER", 200,
                        "MILLER COORS", 10_000,
                        "DANNON", 1100
                ),
                pointsBalance
        );
    }

    @Test
    public void postPointsSpendShouldSpendOldestPointsFirst() {
        List<Transaction> spendTransactions = transactionController.postPointsSpend(1, new Points(5000));
        assertEquals(
            List.of(
                    new Transaction("DANNON", -100),
                    new Transaction("UNILEVER", -200),
                    new Transaction("MILLER COORS", -4700)
            ),
            spendTransactions
        );
        assertEquals(
                Map.of(
                        "UNILEVER", 0,
                        "MILLER COORS", 5300,
                        "DANNON", 1000
                ),
                transactionController.getPointsBalance(1)
        );

    }
}