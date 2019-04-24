package br.agr.terras.materialdroid;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Calendar;

import br.agr.terras.materialdroid.utils.datepicker.timeline.MonthScrollListener;
import br.agr.terras.materialdroid.utils.datepicker.timeline.MonthView;
import br.agr.terras.materialdroid.utils.datepicker.timeline.TimelineScrollListener;
import br.agr.terras.materialdroid.utils.datepicker.timeline.Utils;
import br.agr.terras.materialdroid.utils.datepicker.timeline.TimelineView;
import br.agr.terras.materialdroid.utils.datepicker.timeline.YearView;

public final class DateTimeline extends LinearLayout implements MonthView.OnMonthSelectedListener, YearView.OnYearSelectedListener{

    private YearView yearView;
    private MonthView monthView;
    private TimelineView timelineView;
    private OnDateSelectedListener onDateSelectedListener;
    private TimelineScrollListener timelineScrollListener;
    private MonthScrollListener monthScrollListener;

    public DateTimeline(Context context) {
        this(context, null);
    }

    public DateTimeline(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DateTimeline(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DateTimeline(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final Calendar calendar = Calendar.getInstance();
        int startYear = calendar.get(Calendar.YEAR);
        //noinspection WrongConstant
        if (calendar.get(Calendar.MONTH) == Calendar.JANUARY) {
            // If we are in january, we'll probably want to have previous year :)
            startYear--;
        }
        final int startMonth = Calendar.JANUARY;
        final int startDay = 1;

        // Load default values
        int primaryColor = Utils.getPrimaryColor(getContext());
        int primaryDarkColor = Utils.getPrimaryDarkColor(getContext());
        int tabSelectedColor = ContextCompat.getColor(getContext(), R.color.mti_lbl_tab_selected);
        int tabBeforeSelectionColor = ContextCompat.getColor(getContext(), R.color.mti_lbl_tab_before_selection);
        int lblDayColor = ContextCompat.getColor(getContext(), R.color.mti_lbl_day);
        int lblDateColor = ContextCompat.getColor(getContext(), R.color.mti_lbl_date);
        int lblDateSelectedColor = ContextCompat.getColor(getContext(), R.color.mti_lbl_date_selected);
        int bgLblDateSelectedColor = ContextCompat.getColor(getContext(), R.color.mti_bg_lbl_date_selected_color);
        int ringLblDateSelectedColor = ContextCompat.getColor(getContext(), R.color.mti_ring_lbl_date_color);
        int bgLblTodayColor = ContextCompat.getColor(getContext(), R.color.mti_bg_lbl_today);
        int lblLabelColor = ContextCompat.getColor(getContext(), R.color.mti_lbl_label);

        // Load xml attrs
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DatePickerTimeline, defStyleAttr, 0);
        primaryColor = a.getColor(R.styleable.DatePickerTimeline_mti_primaryColor, primaryColor);
        primaryDarkColor = a.getColor(R.styleable.DatePickerTimeline_mti_primaryDarkColor, primaryDarkColor);
        tabSelectedColor = a.getColor(R.styleable.DatePickerTimeline_mti_tabSelectedColor, tabSelectedColor);
        tabBeforeSelectionColor = a.getColor(R.styleable.DatePickerTimeline_mti_tabBeforeSelectionColor, tabBeforeSelectionColor);
        lblDayColor = a.getColor(R.styleable.DatePickerTimeline_mti_lblDayColor, lblDayColor);
        lblDateColor = a.getColor(R.styleable.DatePickerTimeline_mti_lblDateColor, lblDateColor);
        lblDateSelectedColor = a.getColor(R.styleable.DatePickerTimeline_mti_lblDateSelectedColor, lblDateSelectedColor);
        bgLblDateSelectedColor = a.getColor(R.styleable.DatePickerTimeline_mti_bgLblDateSelectedColor, bgLblDateSelectedColor);
        ringLblDateSelectedColor = a.getColor(R.styleable.DatePickerTimeline_mti_ringLblDateSelectedColor, ringLblDateSelectedColor);
        bgLblTodayColor = a.getColor(R.styleable.DatePickerTimeline_mti_bgLblTodayColor, bgLblTodayColor);
        lblLabelColor = a.getColor(R.styleable.DatePickerTimeline_mti_lblLabelColor, lblLabelColor);
        boolean followScroll = a.getBoolean(R.styleable.DatePickerTimeline_mti_followScroll, true);
        int yearDigitCount = a.getInt(R.styleable.DatePickerTimeline_mti_yearDigitCount, 2);
        boolean yearOnNewLine = a.getBoolean(R.styleable.DatePickerTimeline_mti_yearOnNewLine, true);
        a.recycle();

        final LayerDrawable selectedDrawable = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.mti_bg_lbl_date_selected);
        ((GradientDrawable) selectedDrawable.getDrawable(0)).setColor(ringLblDateSelectedColor);
        ((GradientDrawable) selectedDrawable.getDrawable(1)).setColor(bgLblDateSelectedColor);
        final LayerDrawable todayDrawable = (LayerDrawable) ContextCompat.getDrawable(getContext(), R.drawable.mti_bg_lbl_date_today);
        ((GradientDrawable) todayDrawable.getDrawable(1)).setColor(bgLblTodayColor);

        setOrientation(VERTICAL);
        final View view = inflate(getContext(), R.layout.datepicker_timeline, this);

        yearView = (YearView) view.findViewById(R.id.mti_year_view);
        monthView = (MonthView) view.findViewById(R.id.mti_month_view);
        timelineView = (TimelineView) view.findViewById(R.id.mti_timeline);

        yearView.setBackgroundColor(primaryColor);
        yearView.setFirstDate(startYear);
        yearView.setDefaultColor(primaryDarkColor);
        yearView.setColorSelected(tabSelectedColor);
        yearView.setColorBeforeSelection(tabBeforeSelectionColor);
        yearView.setOnYearSelectedListener(this);

        monthView.setBackgroundColor(primaryColor);
        monthView.setFirstDate(startYear, startMonth);
        monthView.setDefaultColor(primaryDarkColor);
        monthView.setColorSelected(tabSelectedColor);
        monthView.setColorBeforeSelection(tabBeforeSelectionColor);
        monthView.setYearDigitCount(yearDigitCount);
        monthView.setYearOnNewLine(yearOnNewLine);
        monthView.setOnMonthSelectedListener(this);

        timelineView.setBackgroundColor(Color.WHITE);
        timelineView.setFirstDate(startYear, startMonth, startDay);
        timelineView.setDayLabelColor(lblDayColor);
        timelineView.setDateLabelColor(lblDateColor);
        timelineView.setDateLabelSelectedColor(lblDateSelectedColor);
        timelineView.setLabelColor(lblLabelColor);
        timelineView.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                monthView.setSelectedMonth(year, month, false, timelineScrollListener == null);

                if (onDateSelectedListener != null) {
                    onDateSelectedListener.onDateSelected(year, month, day, index);
                }
            }
        });

        timelineView.setSelectedDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        if (followScroll) {
            timelineScrollListener = new TimelineScrollListener(monthView, timelineView);
            timelineView.addOnScrollListener(timelineScrollListener);
            monthScrollListener = new MonthScrollListener(yearView, monthView);
            monthView.addOnScrollListener(monthScrollListener);
        }
    }

    public int getSelectedYear() {
        return timelineView.getSelectedYear();
    }

    public int getSelectedMonth() {
        return timelineView.getSelectedMonth();
    }

    public int getSelectedDay() {
        return timelineView.getSelectedDay();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    public OnDateSelectedListener getOnDateSelectedListener() {
        return onDateSelectedListener;
    }

    public void setDateLabelAdapter(@Nullable MonthView.DateLabelAdapter dateLabelAdapter) {
        timelineView.setDateLabelAdapter(dateLabelAdapter);
    }

    public YearView getYearView(){ return yearView;}

    public MonthView getMonthView() {
        return monthView;
    }

    public TimelineView getTimelineView() {
        return timelineView;
    }

    public void setSelectedDate(int year, int month, int day) {
        timelineView.setSelectedDate(year, month, day);
    }

    @Override
    public void onYearSelected(int year, int index) {
        monthView.setSelectedDate(year, 0, 1);
        timelineView.setSelectedDate(year, 0, 1);
    }

    @Override
    public void onMonthSelected(int year, int month, int index) {
        timelineView.setSelectedDate(year, month, 1);
    }

    public void setFirstVisibleDate(int year, int month, int day) {
        yearView.setFirstDate(year);
        monthView.setFirstDate(year, month);
        timelineView.setFirstDate(year, month, day);
    }

    public void setLastVisibleDate(int year, int month, int day) {
        yearView.setLastDate(year);
        monthView.setLastDate(year, month);
        timelineView.setLastDate(year, month, day);
    }

    public void centerOnSelection() {
        yearView.centerOnSelection();
        monthView.centerOnSelection();
        timelineView.centerOnSelection();
    }

    public int getYearSelectedPosition() {
        return yearView.getSelectedPosition();
    }

    public int getMonthSelectedPosition() {
        return monthView.getSelectedPosition();
    }

    public int getTimelineSelectedPosition() {
        return timelineView.getSelectedPosition();
    }

    public void setFollowScroll(boolean followScroll) {
        if (!followScroll && timelineScrollListener != null && monthScrollListener != null) {
            timelineView.removeOnScrollListener(timelineScrollListener);
            timelineScrollListener = null;
            monthView.removeOnScrollListener(monthScrollListener);
            monthScrollListener = null;
        } else if (followScroll && timelineScrollListener == null && monthScrollListener == null) {
            timelineScrollListener = new TimelineScrollListener(monthView, timelineView);
            timelineView.addOnScrollListener(timelineScrollListener);
            monthScrollListener = new MonthScrollListener(yearView, monthView);
            monthView.addOnScrollListener(monthScrollListener);
        }
    }

    public interface OnDateSelectedListener {

        void onDateSelected(int year, int month, int day, int index);
    }
}