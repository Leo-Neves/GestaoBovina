package br.agr.terras.materialdroid.utils.calendar;

import java.util.Calendar;

/**
 * Created by leo on 15/06/16.
 */
public interface CalendarEvent {

    Object getId();

    void setId(Object mId);

    Calendar getStartTime();

    void setStartTime(Calendar mStartTime);

    Calendar getEndTime();

    void setEndTime(Calendar mEndTime);

    String getTitle();

    void setTitle(String mTitle);

    Calendar getInstanceDay();

    void setInstanceDay(Calendar mInstanceDay);

    DayItem getDayReference();

    void setDayReference(DayItem mDayReference);

    WeekItem getWeekReference();

    void setWeekReference(WeekItem mWeekReference);

    CalendarEvent copy();
}