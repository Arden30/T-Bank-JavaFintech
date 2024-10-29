package arden.java.kudago.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class PriceParser {
    public Double parseEventPrice(String event) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(event);

        return matcher.find() ? Double.parseDouble(matcher.group()) : 0D;
    }
}
