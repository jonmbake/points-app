package com.jonbake.pointsapp.controllers;

import com.jonbake.pointsapp.TransactionsStore;
import com.jonbake.pointsapp.dto.Points;
import com.jonbake.pointsapp.dto.TimestampedTransaction;
import com.jonbake.pointsapp.dto.Transaction;
import com.jonbake.pointsapp.exceptions.NegativePayerPointsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for /points endpoints.
 */
@RestController
public class PointsController extends BaseController {
    /**
     * Get points balance.
     *
     * @param userId user id path param
     * @return points balance per payer
     */
    @GetMapping("points/balance")
    public Map<String, Integer> getPointsBalance (@PathVariable Integer userId) {
        return TransactionsStore.INSTANCE.getPointBalancePerPayer(userId);
    }

    /**
     * Spend points balance.
     *
     * @param userId - user id path param
     * @param points number of points to spend
     * @return debit transactions that were applied by the spend
     */
    @PostMapping("points/spend")
    public List<Transaction> postPointsSpend (@PathVariable Integer userId, @RequestBody Points points) {
        List<TimestampedTransaction> orderedTransactions = TransactionsStore.INSTANCE.getOrderedTransactions(userId);
        List<TimestampedTransaction> creditTransactions = orderedTransactions.stream().filter(t -> t.getPoints() > 0).collect(Collectors.toList());
        // Number of points that have already been applied or used
        Integer appliedPoints = -orderedTransactions.stream().filter(t -> t.getPoints() < 0).map(TimestampedTransaction::getPoints).reduce(0, Integer::sum);
        Integer curPoints = points.getPoints();
        List<TimestampedTransaction> newSpendTransaction = new ArrayList<>();
        for (TimestampedTransaction t : creditTransactions) {
            if (appliedPoints > 0) {
                if (appliedPoints < t.getPoints()) {
                    int partialPoints =  t.getPoints() - appliedPoints;
                    newSpendTransaction.add(new TimestampedTransaction(t.getPayer(), -partialPoints, Instant.now()));
                    curPoints -= partialPoints;
                    appliedPoints = 0;
                } else {
                    appliedPoints -= t.getPoints();
                }
            } else {
                if (curPoints > t.getPoints()) {
                    newSpendTransaction.add(new TimestampedTransaction(t.getPayer(), -t.getPoints(), Instant.now()));
                    curPoints -= t.getPoints();
                } else {
                    newSpendTransaction.add(new TimestampedTransaction(t.getPayer(), -curPoints, Instant.now()));
                    break;
                }
            }
        }
        try {
            TransactionsStore.INSTANCE.addAll(userId, newSpendTransaction);
        } catch (NegativePayerPointsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot have negative payer points.");
        }
        return newSpendTransaction.stream().map(t -> new Transaction(t.getPayer(), t.getPoints())).collect(Collectors.toList());
    }
}
