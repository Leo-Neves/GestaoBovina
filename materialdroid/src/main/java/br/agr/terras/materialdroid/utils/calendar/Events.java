package br.agr.terras.materialdroid.utils.calendar;

import java.util.Calendar;

/**
 * Created by leo on 15/06/16.
 */
public class Events {

    public static class DayClickedEvent {

        public Calendar mCalendar;
        public DayItem mDayItem;

        public DayClickedEvent(DayItem dayItem) {
            this.mCalendar = Calendar.getInstance();
            this.mCalendar.setTime(dayItem.getDate());
            this.mDayItem = dayItem;
        }

        public Calendar getCalendar() {
            return mCalendar;
        }

        public DayItem getDay() {
            return mDayItem;
        }
    }

    public static class CalendarScrolledEvent {
    }

    public static class AgendaListViewTouchedEvent {
    }

    public static class EventsFetched {
    }

    public static class ForecastFetched {
    }
}
