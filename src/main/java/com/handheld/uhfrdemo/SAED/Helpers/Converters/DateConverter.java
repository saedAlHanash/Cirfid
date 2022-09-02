package com.handheld.uhfrdemo.SAED.Helpers.Converters;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DateConverter {

    /**
     * convert any date for type {@link Date} to string
     *
     * @param date an date that you need to convert it
     * @return String type date with english format dd-MM-yyyy HH:mm
     */
    public static String dateToString(Date date) {
        DateFormat dateFormat;

        dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        return dateFormat.format(date);

    }

    /**
     * @param sDate string date with any format
     * @return Date type java Date
     * @deprecated if string date with this format { mm-dd-yyyy } will  get wrong date <br>
     * because It considers days as months and converts every 12 days to a year<br>
     * If you use it, make sure that the date format is correct { dd-mm-yyy }
     * <p>
     * <p>
     * convert any string date with any format to java Date
     */
    public static Date stringToDate(String sDate) {
        Date date = new Date();
        sDate = sDate.replace("T", " ");
        try {
            date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).parse(sDate);
        } catch (Exception e) {
            Log.e("DATE", "stringToDate: ", e);
        }

        return date;
    }

    /**
     * Find the time period between two dates
     *
     * @param dateStart start date
     * @param dateEnd   end date
     * @return period with format { difference_In_Days "Day "  difference_In_Hours "Hour }
     */
    public static String findDifference(Date dateStart, Date dateEnd) {
        // SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        long difference_In_Time = dateEnd.getTime() - dateStart.getTime();
/*
                long difference_In_Seconds = (difference_In_Time / 1000) % 60;
                long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;


*/
        long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
        long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
        return "" + difference_In_Days + "Day " + difference_In_Hours + "Hour";
    }

    /**
     * get current system time
     *
     * @return string date with format dd-MM-yyyy HH:mm
     */
    public static String now() {
        return dateToString(Calendar.getInstance().getTime());
    }

    /**
     * @param date any string Date
     * @return string date with format dd-MM-yyyy HH:mm
     * @deprecated if string date with this format { mm-dd-yyy } will  get wrong date <br>
     * because It considers days as months and converts every 12 days to a year<br>
     * If you use it, make sure that the date format is correct { dd-mm-yyy }
     * giv string date with any format and get string date with this format dd-MM-yyyy HH:mm
     */
    public static String FormatDate(String date) {

        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        Date dd = DateConverter.stringToDate(date);
        String d = dateFormat.format(dd);
        return d;
    }


    /**
     * It calculates the time difference between the start date and the end date and returns the value in minutes
     * @param start_date started date
     * @param end_date end date
     * @return Difference time in Minutes
     */
    public static long findDifferenceMinutes(String start_date, String end_date) {

        long difference_In_Seconds = 0;
        long diffInSeconds = 0;
        long diffInMinutes = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);

        try {

            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            long difference_In_Time = Objects.requireNonNull(d1).getTime() - Objects.requireNonNull(d2).getTime();

            diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(difference_In_Time);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diffInSeconds;

    }


}
