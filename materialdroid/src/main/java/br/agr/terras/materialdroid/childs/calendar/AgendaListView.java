package br.agr.terras.materialdroid.childs.calendar;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.List;

import br.agr.terras.materialdroid.parents.ListView;
import br.agr.terras.materialdroid.utils.calendar.CalendarEvent;
import br.agr.terras.materialdroid.utils.calendar.CalendarManager;
import br.agr.terras.materialdroid.utils.calendar.DateHelper;

/**
 * Created by leo on 15/06/16.
 */
public class AgendaListView extends ListView {

    // region Constructors

    public AgendaListView(Context context) {
        super(context);
    }

    public AgendaListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AgendaListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // endregion

    // region Public methods

    public void scrollToCurrentDate(Calendar today) {
        List<CalendarEvent> events = CalendarManager.getInstance().getEvents();

        int toIndex = 0;
        for (int i = 0; i < events.size(); i++) {
            if (DateHelper.sameDate(today, events.get(i).getInstanceDay())) {
                toIndex = i;
                break;
            }
        }

        final int finalToIndex = toIndex;
        setSelection(finalToIndex);
    }

    // endregion
}
