package com.jonbake.pointsapp.dto;

import java.util.Objects;

public class Transaction {
    protected String payer;
    protected Integer points;

    public Transaction() {
    }

    public Transaction(String payer, Integer points) {
        this.payer = payer;
        this.points = points;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return payer.equals(that.payer) && points.equals(that.points);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payer, points);
    }
}
