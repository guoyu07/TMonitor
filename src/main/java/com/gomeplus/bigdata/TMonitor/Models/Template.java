package com.gomeplus.bigdata.TMonitor.Models;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {
    public static final Pattern dateToken = Pattern.compile(
            "\\$\\{date:\\s*([\\+\\-]\\d+[d])\\s*,\\s*(.*?)\\s*\\}");

    public static String replaceDateToken(String origin) {
        if (origin == null)
            return origin;

        Matcher matcher = dateToken.matcher(origin);
        StringBuilder sb = new StringBuilder();
        int e = 0;
        while (matcher.find()) {
            String delta = matcher.group(1);
            String format = matcher.group(2);
            Date date = new Date();

            char flag = delta.charAt(0);
            char unit = delta.charAt(delta.length() - 1);
            int amount = Integer.valueOf(delta.substring(1, delta.length() - 1));
            long currentTime = System.currentTimeMillis();

            if (flag == '+' && unit == 'd')
                date.setTime(currentTime + 86400000 * amount);
            else if (flag == '-' && unit == 'd')
                date.setTime(currentTime - 86400000 * amount);

            sb.append(origin.substring(e, matcher.start()));
            sb.append(String.format(format, date));
            e = matcher.end();
        }

        sb.append(origin.substring(e, origin.length()));
        return sb.toString();
    }

    public static String replace(String origin) {
        if (origin == null)
            return origin;
        return replaceDateToken(origin);
    }
}
