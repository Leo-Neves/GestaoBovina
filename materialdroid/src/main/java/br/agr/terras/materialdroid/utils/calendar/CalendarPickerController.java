package br.agr.terras.materialdroid.utils.calendar;

import java.util.Calendar;

/**
 * Created by leo on 15/06/16.
 */
public interface CalendarPickerController {
    void onDaySelected(DayItem dayItem);

    void onEventSelected(CalendarEvent event);

    void onScrollToDate(Calendar calendar);
}
