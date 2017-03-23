package com.dyadav.chirpntweet.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            //relativeDate = android.text.format.DateUtils.getRelativeTimeSpanString(dateMillis,
            //        System.currentTimeMillis(), android.text.format.DateUtils.SECOND_IN_MILLIS).toString();
            long timeDiff = (System.currentTimeMillis() - dateMillis)/1000;

            if (timeDiff < 5)
                relativeDate = "Just now";
            else if (timeDiff < 60)
                relativeDate = String.format(Locale.ENGLISH, "%d sec",timeDiff);
            else if (timeDiff < 60 * 60)
                relativeDate = String.format(Locale.ENGLISH, "%d min", timeDiff / 60);
            else if (timeDiff < 60 * 60 * 24)
                relativeDate = String.format(Locale.ENGLISH, "%d hour", timeDiff / (60 * 60));
            else
                //Show time stamp
                relativeDate = "";
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
