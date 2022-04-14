package com.jonbake.pointsapp.controllers;

import com.jonbake.pointsapp.TransactionsStore;
import com.jonbake.pointsapp.dto.TimestampedTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionsControllerTest {
    private TransactionsController transactionsController = new TransactionsController();

    @BeforeEach
    public void clearTransactionStore () {
        TransactionsStore.INSTANCE.clear(1);
    }

    @Test
    public void postShouldPersistTransactions () {
        TimestampedTransaction transaction = new TimestampedTransaction("DANNON", 1000, Instant.parse("2020-11-02T14:00:00Z"));
        transactionsController.postTransactions(1, transaction);
        assertEquals(List.of(transaction), transactionsController.getTransactions(1));
    }

    @Test
    public void postShouldReturnBadRequestOnNegativePayerBalance () {
        TimestampedTransaction transaction = new TimestampedTransaction("DANNON", 100, Instant.parse("2020-11-02T14:00:00Z"));
        transactionsController.postTransactions(1, transaction);
        assertThrows(ResponseStatusException.class, () -> {
           transactionsController.postTransactions(1, new TimestampedTransaction("DANNON", -200, Instant.parse("2020-11-03T14:00:00Z")));
        });
        assertEquals(List.of(transaction), transactionsController.getTransactions(1));
    }
}