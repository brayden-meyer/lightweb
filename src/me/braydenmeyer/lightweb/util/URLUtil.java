package me.braydenmeyer.lightweb.util;

import java.util.regex.Pattern;

public final class URLUtil {

    private static Pattern validURL = Pattern.compile("^https?://.*");

    private static boolean isValid(String URL) {
        return validURL.matcher(URL).matches();
    }

    public static String parse(String URL) {
        return isValid(URL) ? URL : "http://" + URL;
    }
}
