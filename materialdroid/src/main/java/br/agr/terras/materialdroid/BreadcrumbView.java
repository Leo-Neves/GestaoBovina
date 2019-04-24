package br.agr.terras.materialdroid;

/**
 * Created by leo on 28/09/16.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import br.agr.terras.materialdroid.childs.LabelBreadcrumbView;
import br.agr.terras.materialdroid.utils.ViewUtil;
import br.agr.terras.materialdroid.utils.drawable.BreadcrumbDrawable;
import br.agr.terras.materialdroid.utils.drawable.LabelDrawable;


public class BreadcrumbView extends ViewGroup implements View.OnClickListener {
    private LabelDrawable.Sides startType = LabelDrawable.Sides.CLOSED;
    private LabelDrawable.Sides endType = LabelDrawable.Sides.CLOSED;

    private String path = "One.Two.Three.Four";
    private String separator = "\\.";
    private int maxLevels = 4;
    private int margin;
    private float textHeight = 0;

    private OnPathLabelClickListener onPathLabelClickListener;
    private int textUnselectedColor = Color.WHITE;
    private int textSelectedColor = Color.WHITE;
    private int borderColor = Color.BLUE;
    private int unselectedColor = Color.GRAY;
    private int selectedColor = Color.GRAY;
    private boolean forwardByClick;
    private int textSize = 15;
    int viewHeight;
    Paint fontSizePaint;
    int selectedPosition;


    public BreadcrumbView(Context context) {
        super(context);
        init(null);
    }

    public BreadcrumbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BreadcrumbView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BreadcrumbView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        fontSizePaint = new Paint();

        //Load attributes from xml if any
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BreadcrumbView);
            maxLevels = a.getInt(R.styleable.BreadcrumbView_maxLevels, maxLevels);
            String new_separator = a.getString(R.styleable.BreadcrumbView_separator);
            if (new_separator != null) {
                separator = new_separator;
            }
            String new_path = a.getString(R.styleable.BreadcrumbView_path);
            if (new_path != null) {
                path = new_path;
            }
            borderColor = a.getColor(R.styleable.BreadcrumbView_borderColor, borderColor);
            unselectedColor = a.getColor(R.styleable.BreadcrumbView_unselectedColor, unselectedColor);
            selectedColor = a.getColor(R.styleable.BreadcrumbView_selectedColor, selectedColor);
            textUnselectedColor = a.getColor(R.styleable.BreadcrumbView_textUnselectedColor, textUnselectedColor);
            textSelectedColor = a.getColor(R.styleable.BreadcrumbView_textSelectedColor, textSelectedColor);
            textSize = a.getDimensionPixelSize(R.styleable.BreadcrumbView_labelTextSize, textSize);
            forwardByClick = a.getBoolean(R.styleable.BreadcrumbView_forwardByClick, true);
            if (a.getInt(R.styleable.BreadcrumbView_startType, 0) == 0) {
                startType = LabelDrawable.Sides.CLOSED;
            } else {
                startType = LabelDrawable.Sides.OPEN;
            }
            if (a.getInt(R.styleable.BreadcrumbView_endType, 0) == 0) {
                endType = LabelDrawable.Sides.CLOSED;
            } else {
                endType = LabelDrawable.Sides.OPEN;
            }
            a.recycle();
        }
        setupChildren();
        fillChildren();
        BreadcrumbDrawable breadcrumbDrawable = new BreadcrumbDrawable(maxLevels);
        breadcrumbDrawable.setColor(borderColor);
        setBackgroundDrawable(breadcrumbDrawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            viewHeight = (int) textHeight;
        } else {
            viewHeight = getDefaultSize((int) textHeight, heightMeasureSpec);
        }
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                viewHeight);
        margin = getMeasuredHeight() / 4;
        for (int i = 0; i < maxLevels; i++) {
            LabelBreadcrumbView child = (LabelBreadcrumbView) getChildAt(i);
            int viewWidth = getMeasuredWidth() / maxLevels + margin / 2;
            if (i > 0 && i < maxLevels - 1) {
                viewWidth = viewWidth + margin / 2;
            }
            int wspec = MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY);
            int hspec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
            child.setPadding((int) textHeight / 2, 0, (int) textHeight / 2, 0);
            child.measure(wspec, hspec);
        }


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {

            layoutChildren(left, 0, right, bottom);
        }
    }

    private void layoutChildren(int left, int top, int right, int bottom) {

        int viewLeft = 0;
        int viewRight = 0;


        for (int i = 0; i < maxLevels; i++) {
            LabelBreadcrumbView child = (LabelBreadcrumbView) getChildAt(i);

            viewRight = viewLeft + child.getMeasuredWidth();

            child.layout(viewLeft, 0, viewRight, viewHeight);


            viewLeft = viewRight - margin;


        }
    }

    private void fillChildren() {
        String[] parts = path.split(separator);
        int partsCount = parts.length;
        for (int i = 0; i < maxLevels; i++) {
            LabelBreadcrumbView child = (LabelBreadcrumbView) getChildAt(i);
            if (i < partsCount) {
                child.setText(String.valueOf(i + 1));
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(INVISIBLE);
            }
        }
    }


    private void setupChildren() {
        removeAllViews();
        float fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, textSize, getResources().getDisplayMetrics());
        fontSizePaint.setTextSize(fontSize);
        textHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()) + fontSizePaint.getFontMetrics().bottom - fontSizePaint.getFontMetrics().top;
        for (int i = 0; i < maxLevels; i++) {
            LabelBreadcrumbView child = new LabelBreadcrumbView(getContext());
            child.setId(ViewUtil.generateViewId());
            child.setColorUnselected(unselectedColor);
            child.setColorSelected(selectedColor);
            child.setColorBorder(borderColor);
            child.setTextUnselectedColor(textUnselectedColor);
            child.setTextSelectedColor(textSelectedColor);
            child.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            child.setOnClickListener(this);
            addView(child);
            child.setSelected(false);
        }
    }


    public int getMaxLevels() {
        return maxLevels;
    }

    /**
     * Sets the maximum number of levels that the breadcrumb will have. Any crumb over that number won't be paint.
     * Any crumb under that number will left a blank space.
     *
     * @param maxLevels The maximum number of levels
     */
    public void setMaxLevels(int maxLevels) {
        if (maxLevels != this.maxLevels) {
            this.maxLevels = maxLevels;
            setupChildren();
            fillChildren();
        }


    }

    public String getSeparator() {
        return separator;
    }

    /**
     * Sets the symbol used as separator of crumbs in the path
     *
     * @param separator A regular expression that will be used to split the path into crumbs
     */
    public void setSeparator(String separator) {
        this.separator = separator;
        forceLayout();
    }

    public String getPath() {
        return path;
    }

    /**
     * Sets the current path
     *
     * @param path The path that will be splitted and transformed into crumbs
     */
    public void setPath(String path) {
        this.path = path;

        fillChildren();

    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        for (int i = 0; i < maxLevels; i++)
            getChildAt(i).setSelected(i == position);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public void onClick(View v) {
        int clickedPosition = indexOfChild(v);
        Log.i(getClass().getSimpleName(), "Clicked position in breadcrumb: " + clickedPosition + "\nSelected position in breadcrumb: " + selectedPosition);
        if (forwardByClick) {
            for (int i = 0; i < maxLevels; i++)
                getChildAt(i).setSelected(false);
            getChildAt(clickedPosition).setSelected(true);
            if (onPathLabelClickListener != null)
                onPathLabelClickListener.onPathLabelClick(indexOfChild(v));
            selectedPosition = clickedPosition;
        } else {
            if (clickedPosition <= selectedPosition) {
                for (int i = 0; i < maxLevels; i++)
                    getChildAt(i).setSelected(false);
                getChildAt(clickedPosition).setSelected(true);
                if (onPathLabelClickListener != null)
                    onPathLabelClickListener.onPathLabelClick(indexOfChild(v));
                selectedPosition = clickedPosition;
            }
        }
    }

    /**
     * Interface to be implemented by classes who want to listen at crumb click
     */
    public interface OnPathLabelClickListener {
        /**
         * Listener fired when a crumb is clicked
         *
         * @param position Number of the crumb clicked starting at 0
         */
        void onPathLabelClick(int position);
    }

    public OnPathLabelClickListener getOnPathLabelClickListener() {
        return onPathLabelClickListener;
    }

    public void setOnPathLabelClickListener(OnPathLabelClickListener onPathLabelClickListener) {
        this.onPathLabelClickListener = onPathLabelClickListener;
    }

    public int getTextUnselectedColor() {
        return textUnselectedColor;
    }

    /**
     * Color of the current text
     *
     * @param textUnselectedColor The color
     */
    public void setTextUnselectedColor(int textUnselectedColor) {
        this.textUnselectedColor = textUnselectedColor;
        for (int i = 0; i < getChildCount(); i++) {
            ((LabelBreadcrumbView) getChildAt(i)).setTextColor(textUnselectedColor);
        }
    }

    public int getBorderColor() {
        return borderColor;
    }

    /**
     * Background color for the lables
     *
     * @param borderColor The color
     */
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        for (int i = 0; i < getChildCount(); i++) {
            ((LabelDrawable) getChildAt(i).getBackground()).setColorExternal(borderColor);
        }
    }

    public int getUnselectedColor() {
        return unselectedColor;
    }

    /**
     * Background color for the lables
     *
     * @param unselectedColor The color
     */
    public void setUnselectedColor(int unselectedColor) {
        this.unselectedColor = unselectedColor;
        for (int i = 0; i < getChildCount(); i++) {
            ((LabelDrawable) getChildAt(i).getBackground()).setColorInternal(unselectedColor);
        }
    }

    public LabelDrawable.Sides getEndType() {
        return endType;
    }

    public void setEndType(LabelDrawable.Sides endType) {
        if (getChildCount() > 0) {
            ((LabelDrawable) getChildAt(getChildCount() - 1).getBackground()).setEnd(endType);
        }
        this.endType = endType;
    }

    public LabelDrawable.Sides getStartType() {

        return startType;
    }

    public void setStartType(LabelDrawable.Sides startType) {
        if (getChildCount() > 0) {
            ((LabelDrawable) getChildAt(0).getBackground()).setStart(startType);
        }
        this.startType = startType;
    }
}
