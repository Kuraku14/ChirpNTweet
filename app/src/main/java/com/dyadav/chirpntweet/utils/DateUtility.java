package com.dyadav.chirpntweet.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtility {
    /**
     * Source - https://gist.github.com/nesquena/f786232f5ef72f6e10a7.
     */
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();

            long timeDiff = (System.currentTimeMillis() - dateMillis)/1000;

            if (timeDiff < 5)
                relativeDate = "Just now";
            else if (timeDiff < 60)
                relativeDate = String.format(Locale.ENGLISH, "%d sec",timeDiff);
            else if (timeDiff < 60 * 60)
                relativeDate = String.format(Locale.ENGLISH, "%d min", timeDiff / 60);
            else if (timeDiff < 60 * 60 * 24)
                relativeDate = String.format(Locale.ENGLISH, "%d hour", timeDiff / (60 * 60));
            else {
                Date date = new Date(dateMillis);
                DateFormat formatter = new SimpleDateFormat("dd MMM yy");
                relativeDate = formatter.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String detailedViewFormatDate(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            Date date = new Date(dateMillis);
            DateFormat formatter = new SimpleDateFormat("dd MMM yy");
            relativeDate = formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
