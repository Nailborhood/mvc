package com.nailshop.nailborhood.service.common;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeFormatter {
    public static String formatRelative(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();
        long years = ChronoUnit.YEARS.between(time, now);
        long months = ChronoUnit.MONTHS.between(time, now);
        long days = ChronoUnit.DAYS.between(time, now);
        long hours = ChronoUnit.HOURS.between(time, now);
        long minutes = ChronoUnit.MINUTES.between(time, now);

        if (years > 0) return years + "년 전";
        if (months > 0) return months + "달 전";
        if (days > 0) return days + "일 전";
        if (hours > 0) return hours + "시간 전";
        if (minutes > 0) return minutes + "분 전";
        return "방금 전";
    }
}
