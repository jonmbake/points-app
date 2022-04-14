package com.jonbake.pointsapp.dto;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a debit or credit points transaction.
 */
public class TimestampedTransaction extends Transaction {
    private Instant timestamp;

    public TimestampedTransaction(String payer, Integer points, Instant timestamp) {
        super(payer, points);
        this.timestamp = timestamp;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimestampedTransaction that = (TimestampedTransaction) o;
        return payer.equals(that.payer) && points.equals(that.points) && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payer, points, timestamp);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "payer='" + payer + '\'' +
                ", points=" + points +
                ", timestamp=" + timestamp +
                '}';
    }
}