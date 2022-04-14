package com.jonbake.pointsapp;

import com.jonbake.pointsapp.dto.TimestampedTransaction;
import com.jonbake.pointsapp.exceptions.NegativePayerPointsException;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * An in-memory data store for user transactions.
 */
public class TransactionsStore {
    public static final TransactionsStore INSTANCE = new TransactionsStore();

    private TransactionsStore() {}

    private Map<Integer, List<TimestampedTransaction>> data = new HashMap<>();

    public void add(Integer userId, TimestampedTransaction transaction) throws NegativePayerPointsException {
        if (transaction.getPoints() < 0) {
            if (getPointBalancePerPayer(userId).getOrDefault(transaction.getPayer(), 0) + transaction.getPoints() < 0) {
                throw new NegativePayerPointsException();
            }
        }
        data.putIfAbsent(userId, new ArrayList<>());
        data.get(userId).add(transaction);
    }

    public void addAll(Integer userId, List<TimestampedTransaction> transactions) throws NegativePayerPointsException {
        for (TimestampedTransaction transaction : transactions) {
            add(userId, transaction);
        }
    }

    public List<TimestampedTransaction> get(Integer userId) {
        return data.getOrDefault(userId, new ArrayList<>());
    }

    public void clear(Integer userId) {
        data.remove(userId);
    }

    public List<TimestampedTransaction> getOrderedTransactions (Integer userId) {
        return get(userId).stream().sorted(Comparator.comparing(TimestampedTransaction::getTimestamp)).collect(Collectors.toList());
    }

    public Map<String, Integer> getPointBalancePerPayer(Integer userId) {
        return INSTANCE.get(userId).stream()
                .collect(groupingBy(TimestampedTransaction::getPayer))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, mapEntry -> mapEntry.getValue().stream().map(TimestampedTransaction::getPoints).reduce(0, Integer::sum)));
    }
}
