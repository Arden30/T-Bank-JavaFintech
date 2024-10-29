package arden.java.kudago.utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

@UtilityClass
public class DateParser {
    public LocalDate toLocalDate(Long date) {
        return Instant.ofEpochSecond(date).atZone(ZoneOffset.UTC).toLocalDate();
    }

    public long toEpochSeconds(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    }
}
