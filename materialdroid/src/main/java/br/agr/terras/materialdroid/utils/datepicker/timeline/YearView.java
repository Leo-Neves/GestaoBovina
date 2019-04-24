package br.agr.terras.materialdroid.utils.datepicker.timeline;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import br.agr.terras.materialdroid.R;

public class YearView extends RecyclerView {

    private YearAdapter adapter;
    private LinearLayoutManager layoutManager;
    private OnYearSelectedListener onYearSelectedListener;

    private int defaultColor, colorSelected, colorBeforeSelection;

    private int startYear = 1970;

    private int selectedYear;
    private int selectedPosition = -1;
    private int yearCount = Integer.MAX_VALUE;

    public YearView(Context context) {
        super(context);
        init();
    }

    public YearView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public YearView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        final Calendar calendar = Calendar.getInstance();
        setSelectedYear(calendar.get(Calendar.YEAR), false);

        setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        adapter = new YearAdapter();
        setLayoutManager(layoutManager);
        setAdapter(adapter);
    }

    public void setSelectedYear(int year) {
        setSelectedYear(year, true, true);
    }

    public void setSelectedYear(int year, boolean callListener) {
        setSelectedYear(year, callListener, true);
    }

    public void setSelectedYear(int year, boolean callListener, boolean centerOnPosition) {
        onYearSelected(year, callListener, centerOnPosition);
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    private void onYearSelected(int year, boolean callListener, boolean centerOnPosition) {
        int oldPosition = selectedPosition;
        selectedPosition = getPositionForDate(year);
        selectedYear = year;

        if (selectedPosition == oldPosition) {
            if (centerOnPosition) {
                centerOnPosition(selectedPosition);
            }
            return;
        }

        if (adapter != null && layoutManager != null) {
            final int rangeStart = Math.min(oldPosition, selectedPosition);
            final int rangeEnd = Math.max(oldPosition, selectedPosition);
            adapter.notifyItemRangeChanged(rangeStart, rangeEnd - rangeStart + 1);

            // Animate scroll
            if (centerOnPosition) {
                centerOnPosition(selectedPosition);
            }

            if (callListener && onYearSelectedListener != null) {
                onYearSelectedListener.onYearSelected(year, selectedPosition);
            }
        } else if (centerOnPosition) {
            post(new Runnable() {
                @Override
                public void run() {
                    centerOnPosition(selectedPosition);
                }
            });
        }
    }

    public void centerOnPosition(int position) {
        if (getChildCount() == 0) {
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isLaidOut()) {
                return;
            }
        }
        // Animate scroll
        int offset = getMeasuredWidth() / 2 - getItemWidth() / 2;
        layoutManager.scrollToPositionWithOffset(position, offset);
    }

    public void centerOnDate(int year) {
        centerOnPosition(getPositionForDate(year));
    }

    public void centerOnSelection() {
        centerOnPosition(selectedPosition);
    }

    void scrollToYearPosition(int year, int offsetYear) {
        if (getChildCount() == 0) {
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isLaidOut()) {
                return;
            }
        }
        // Animate scroll
        layoutManager.scrollToPositionWithOffset(getPositionForDate(year + 1),
                offsetYear + getMeasuredWidth() / 2 - getItemWidth() / 2);
    }

    int getItemWidth() {
        return getChildAt(0).getMeasuredWidth();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    private int getYearForPosition(int position) {
        return position + startYear;
    }

    private int getPositionForDate(int year) {
        return year - startYear;
    }

    public void setOnYearSelectedListener(OnYearSelectedListener onYearSelectedListener) {
        this.onYearSelectedListener = onYearSelectedListener;
    }

    public OnYearSelectedListener getOnYearSelectedListener() {
        return onYearSelectedListener;
    }

    /**
     * Default indicator and text color
     */
    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    /**
     * Color when month is selected
     */
    public void setColorSelected(int colorSelected) {
        this.colorSelected = colorSelected;
    }

    /**
     * Color when month is before the current selected month
     */
    public void setColorBeforeSelection(int colorBeforeSelection) {
        this.colorBeforeSelection = colorBeforeSelection;
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public int getColorBeforeSelection() {
        return colorBeforeSelection;
    }

    public int getColorSelected() {
        return colorSelected;
    }

    public void setFirstDate(int startYear) {
        this.startYear = startYear;
        selectedYear = startYear;
        selectedPosition = 0;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    void setYearCount(int yearCount) {
        if (this.yearCount == yearCount) {
            return;
        }

        this.yearCount = yearCount;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setLastDate(int endYear) {
        if (endYear < startYear) {
            throw new IllegalArgumentException("Last visible date cannot be before first visible date");
        }

        Calendar firstDate = Calendar.getInstance();
        firstDate.set(startYear, 0, 1);
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(endYear, 11, 1);
        int diffYear = lastDate.get(Calendar.YEAR) - firstDate.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + lastDate.get(Calendar.MONTH) - firstDate.get(Calendar.MONTH);

        setYearCount(diffMonth + 1);
    }

    private class YearAdapter extends Adapter<YearViewHolder> {

        YearAdapter() {

        }

        @Override
        public YearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.datepicker_item_month, parent, false);
            return new YearViewHolder(view);
        }

        @Override
        public void onBindViewHolder(YearViewHolder holder, int position) {
            final int year = getYearForPosition(position);
            holder.bind(year, position == selectedPosition, position < selectedPosition);
        }

        @Override
        public int getItemCount() {
            return yearCount;
        }
    }

    private class YearViewHolder extends ViewHolder {

        private final TextView lbl;
        private final DotView indicator;

        private int year;

        YearViewHolder(View root) {
            super(root);

            indicator = (DotView) root.findViewById(R.id.mti_view_indicator);
            lbl = (TextView) root.findViewById(R.id.mti_month_lbl);

            root.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onYearSelected(year, true, true);
                }
            });
        }

        void bind(int year, boolean selected, boolean beforeSelection) {
            this.year = year;

            String text = String.valueOf(year);
            lbl.setText(text);
            int color = selected ? colorSelected : beforeSelection ? colorBeforeSelection : defaultColor;
            lbl.setTextColor(color);
            indicator.setColor(color);
            indicator.setCircleSizeDp(selected ? 12 : 5);
        }
    }

    public interface OnYearSelectedListener {

        void onYearSelected(int year, int index);
    }

    public interface DateLabelAdapter {

        CharSequence getLabel(Calendar calendar, int index);
    }
}
