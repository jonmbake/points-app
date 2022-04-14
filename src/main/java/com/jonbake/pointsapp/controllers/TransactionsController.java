package com.jonbake.pointsapp.controllers;

import com.jonbake.pointsapp.TransactionsStore;
import com.jonbake.pointsapp.dto.Points;
import com.jonbake.pointsapp.dto.TimestampedTransaction;
import com.jonbake.pointsapp.exceptions.NegativePayerPointsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Controller for /transactions endpoints.
 */
@RestController
public class TransactionsController extends BaseController {
    /**
     * Get user transactions.
     *
     * @param userId user id path param
     * @return user transactions
     */
    @GetMapping("transactions")
    public List<TimestampedTransaction> getTransactions(@PathVariable Integer userId) {
        return TransactionsStore.INSTANCE.get(userId);
    }

    /**
     * Apply a user transaction.
     *
     * @param userId user id path param
     * @param transaction transaction to apply
     * @return points balance after applying the transaction
     */
    @PostMapping("transactions")
    public Points postTransactions(@PathVariable Integer userId, @RequestBody TimestampedTransaction transaction) {
        try {
            TransactionsStore.INSTANCE.add(userId, transaction);
        } catch (NegativePayerPointsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot have negative payer points.");
        }
        Integer balance = TransactionsStore.INSTANCE.get(userId).stream().map(TimestampedTransaction::getPoints).reduce(0, Integer::sum);
        return new Points(balance);
    }
}
