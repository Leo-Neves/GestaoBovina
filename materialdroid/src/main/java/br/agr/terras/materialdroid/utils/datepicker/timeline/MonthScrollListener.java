package br.agr.terras.materialdroid.utils.datepicker.timeline;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.Calendar;

import br.agr.terras.materialdroid.R;

public class MonthScrollListener extends RecyclerView.OnScrollListener {

    private YearView yearView;
    private MonthView monthView;

    private Calendar calendar = Calendar.getInstance();
    private int year = -1;
    private int yearStartOffset = 0;
    private int yearEndOffset = 0;
    private int monthCount = 12;

    private int monthItemWidth;

    public MonthScrollListener(YearView yearView, MonthView monthView) {
        this.yearView = yearView;
        this.monthView = monthView;

        monthItemWidth = yearView.getResources().getDimensionPixelSize(R.dimen.mti_month_width);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int scrollOffset = recyclerView.computeHorizontalScrollOffset();
        int scrollOffsetCenter = scrollOffset + (recyclerView.getMeasuredWidth() / 2);
        int centerPosition = scrollOffsetCenter / monthItemWidth;

        if (!(scrollOffsetCenter >= yearStartOffset && scrollOffsetCenter <= yearEndOffset)) {
            calendar.set(monthView.getStartYear(), monthView.getStartMonth(), monthView.getStartDay());
            int startDay = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.add(Calendar.DAY_OF_YEAR, centerPosition);
            year = calendar.get(Calendar.YEAR);
            int yearDayCount = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);

            if (year != monthView.getStartYear()) {
                int yearDay = calendar.get(Calendar.DAY_OF_YEAR);
                yearStartOffset = scrollOffsetCenter
                        - yearDay * monthItemWidth
                        - scrollOffsetCenter % monthItemWidth;
                monthCount = 12;
            } else {
                yearStartOffset = 0;
                monthCount = 12 - monthView.getStartMonth();
                yearDayCount -= startDay;
            }

            yearEndOffset = yearStartOffset + yearDayCount * monthItemWidth;
        }

        Log.v("TimeScrollListener", "yearStartOffset: " + yearStartOffset + ", " + "yearEndOffset: " + yearEndOffset + ", "
                + "scrollOffsetCenter: " + scrollOffsetCenter);
        float progress = (float) (scrollOffsetCenter - yearStartOffset) / (yearEndOffset - yearStartOffset);
        int yearOffset = (int) ((1 - progress) * (monthCount * yearView.getItemWidth()));
        Log.v("TimeScrollListener", "progress: " + progress + ", monthOffset: " + yearOffset);
        yearView.scrollToYearPosition(year, yearOffset);
    }
}
