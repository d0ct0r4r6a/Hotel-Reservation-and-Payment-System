package com.hotel.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Handles Date Formatting
 * @author  Tan Jun Xiang
 * @version 1.0
 */
public class DateManager {
    /**
     * Get total days by end - start
     * @param start Start Date
     * @param end End Date
     * @return total days
     */
    public static int getTotalDays(Date start, Date end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            start = sdf.parse(sdf.format(start));
            end = sdf.parse(sdf.format(end));

            long diff = end.getTime() - start.getTime();
            return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * similar to getTotalDays but only returns number of Weekdays
     * @param start Start Date
     * @param end End Date
     * @return total weekdays
     */
    public static int getTotalWeekdays(Date start, Date end) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);

        int workDays = 0;

        do {
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
                ++workDays;
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        } while (startCal.getTimeInMillis() < endCal.getTimeInMillis());

        return workDays;
    }
}
