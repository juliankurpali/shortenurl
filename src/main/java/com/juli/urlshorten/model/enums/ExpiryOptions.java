package com.juli.urlshorten.model.enums;

import java.time.Duration;

public enum ExpiryOptions {
    ONE_MINUTE(0, 0, 1), FIVE_MINUTES(0, 0, 5), TEN_MINUTES(0, 0, 10), THIRTY_MINUTES(0, 0, 30), ONE_HOUR(0, 1, 0), ONE_DAY(1, 0, 0), ONE_WEEK(7, 0, 0);
    private final int days;
    private final int hours;
    private final int minutes;

    ExpiryOptions(int days, int hours, int minutes) {
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public Duration getDuration() {
        return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes);
    }
}




