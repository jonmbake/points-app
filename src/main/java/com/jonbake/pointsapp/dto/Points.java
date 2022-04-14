package com.jonbake.pointsapp.dto;

/**
 * A DTO representing a user points balance.
 */
public class Points {
    private Integer points;

    public Points(Integer points) {
        this.points = points;
    }

    public Points() {
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}