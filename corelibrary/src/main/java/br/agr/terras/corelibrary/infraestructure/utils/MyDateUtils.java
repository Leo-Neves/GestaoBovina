package br.agr.terras.corelibrary.infraestructure.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyDateUtils {

    public static Locale getLocaleBR() {
        return new Locale("pt", "BR");
    }

    public static Date convertStringDateToDate(String stringDate) {
        if (stringDate == null || stringDate.trim().isEmpty())
            return null;
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", getLocaleBR());
        try {
            return formatter.parse(stringDate);
        } catch (ParseException e) {
            Log.e(MyDateUtils.class.getSimpleName(), String.format("Cannot parse date %s", stringDate));
        }
        return null;
    }

    public static String convertDateToISOString(Date date) {
        if (date == null)
            return null;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", getLocaleBR());
        return formatter.format(date);
    }

    public static String getDataEHoraEmString(Date date) {
        if (date == null)
            return null;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", getLocaleBR());
        return df.format(date);
    }

    public static String convertDateToString(Date date) {
        if (date == null)
            return null;
        return new SimpleDateFormat("dd/MM/yyyy", getLocaleBR()).format(date);
    }

    public static Date getDateYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static String convertDateToHourString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", getLocaleBR());
        return simpleDateFormat.format(date);
    }

    public static Date convertStringToDate(String data) {
        if (data == null)
            return null;
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z", getLocaleBR());
        try {
            return formatter.parse(data);
        } catch (ParseException e) {
            formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS z", getLocaleBR());
            try {
                return formatter.parse(data);
            } catch (ParseException f) {
                formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", getLocaleBR());
                try {
                    return formatter.parse(data);
                } catch (ParseException g) {
                    formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", getLocaleBR());
                    try {
                        return formatter.parse(data);
                    } catch (ParseException h) {
                        formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", getLocaleBR());
                        try {
                            return formatter.parse(data);
                        } catch (ParseException i) {
                            Log.e(MyDateUtils.class.getSimpleName(), String.format("Cannot parse date %s", data));
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String convertDateToStringDots(Date data) {
        if (data == null)
            return null;
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z", getLocaleBR());
        return formatter.format(data);
    }

    public static boolean isDayBeforeToday(Date date) {
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", getLocaleBR());

            String dateTodayText = sdf.format(new Date());
            String dateOtherText = sdf.format(date);

            Date dateToday = sdf.parse(dateTodayText);
            Date dateOther = sdf.parse(dateOtherText);


            if (dateOther.before(dateToday)) {
                return true;
            }

            if (dateOther.equals(dateToday)) {
                return false;
            }

        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static boolean isDayToday(Date date) {
        if (!isDayBeforeToday(date)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", getLocaleBR());
                String dateTodayText = sdf.format(new Date());
                String dateOtherText = sdf.format(date);
                Date dateToday = sdf.parse(dateTodayText);
                Date dateOther = sdf.parse(dateOtherText);
                if (dateOther.equals(dateToday)) {
                    return true;
                }

            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isDayThisWeek(Date date) {
        if (!isDayBeforeToday(date)) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK) + 7);
                Date inSevenDays = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", getLocaleBR());
                String dateInsevenDaysText = sdf.format(inSevenDays);
                String dateOtherText = sdf.format(date);
                Date dateInSevenDays = sdf.parse(dateInsevenDaysText);
                Date dateOther = sdf.parse(dateOtherText);
                if (dateOther.before(dateInSevenDays)) {
                    return true;
                }

            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static boolean isSameYear(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static String getMonthName(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getLocaleBR());
    }

    public static int getDaysBetweenDates(Date date1, Date date2) {
        Calendar day1 = Calendar.getInstance();
        Calendar day2 = Calendar.getInstance();
        day1.setTime(date1);
        day2.setTime(date2);
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
        }
    }

    public static Date getDataToday() {
        Calendar hoje = Calendar.getInstance();
        hoje.set(Calendar.DAY_OF_MONTH, hoje.get(Calendar.DAY_OF_MONTH));
        hoje.set(Calendar.HOUR_OF_DAY, 12);
        hoje.set(Calendar.MINUTE, 0);
        hoje.set(Calendar.SECOND, 0);
        return hoje.getTime();
    }

    public static Date getDataTomorrow() {
        Calendar amanha = Calendar.getInstance();
        amanha.set(Calendar.DAY_OF_MONTH, amanha.get(Calendar.DAY_OF_MONTH) + 1);
        amanha.set(Calendar.HOUR_OF_DAY, 0);
        amanha.set(Calendar.MINUTE, 0);
        amanha.set(Calendar.SECOND, 0);
        return amanha.getTime();
    }

    public static Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance(getLocaleBR());
        calendar.setTime(date);
        return calendar;
    }

    public static int getUltimoDiaDoMes(int mes, int ano){
        if (mes<1 || mes>12)
            return Integer.MIN_VALUE;
        if (mes == 2)
            return (ano % 4 == 0 && ano % 100 != 0) || ( ano % 400 == 0 )?29:28;
        if (mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12)
            return 31;
        return 30;
    }
}