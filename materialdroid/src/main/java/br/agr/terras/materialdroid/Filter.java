package br.agr.terras.materialdroid;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import br.agr.terras.materialdroid.utils.filter.CollapseListener;
import br.agr.terras.materialdroid.utils.filter.CollapseView;
import br.agr.terras.materialdroid.utils.filter.CollapsedFilterContainer;
import br.agr.terras.materialdroid.utils.filter.CollapsedFilterView;
import br.agr.terras.materialdroid.utils.filter.Constant;
import br.agr.terras.materialdroid.utils.filter.Coord;
import br.agr.terras.materialdroid.utils.filter.ExpandedFilterView;
import br.agr.terras.materialdroid.utils.filter.FilterAdapter;
import br.agr.terras.materialdroid.utils.filter.FilterFocusListener;
import br.agr.terras.materialdroid.utils.filter.FilterItem;
import br.agr.terras.materialdroid.utils.filter.FilterItemListener;
import br.agr.terras.materialdroid.utils.filter.FilterListener;
import br.agr.terras.materialdroid.utils.filter.FilterModel;
import br.agr.terras.materialdroid.utils.filter.ViewGroupExtensions;
import kotlin.jvm.internal.Intrinsics;

import static br.agr.terras.materialdroid.utils.filter.ViewGroupExtensions.dpToPx;
import static br.agr.terras.materialdroid.utils.filter.ViewGroupExtensions.getDimen;


/**
 * Created by galata on 08.09.16.
 */
public class Filter<T extends FilterModel> extends FrameLayout implements FilterItemListener, CollapseListener {

    private ExpandedFilterView expandedFilter;
    private LinearLayout subFilters;
    private CollapsedFilterContainer collapsedContainer;
    private ScrollView expandedFilterScroll;
    private FrameLayout container;
    private View blackBackground;
    private RelativeLayout relative_container;
    private View dividerTop;
    private View dividerBottom;
    private TextView collapsedText;
    private HorizontalScrollView collapsedFilterScroll;
    private CollapsedFilterView collapsedFilter;
    private CollapseView collapseView;

    private FilterAdapter<T> adapter;
    private FilterListener<T> listener;
    private FilterFocusListener focusListener;
    int margin = dpToPx(this, getDimen(this, R.dimen.margin));
    private String noSelectedItemText = "";

    public void setNoSelectedItemText(String value) {
        this.noSelectedItemText = value;
        collapsedText.setText(value);
    }

    private String textToReplaceArrow = "";

    public void setTextToReplaceArrow(String value) {
        this.textToReplaceArrow = value;
        collapseView.setText(value);
    }

    private boolean replaceArrowByText;

    public void setReplaceArrowByText(boolean replaceArrowByText) {
        this.replaceArrowByText = replaceArrowByText;
        collapseView.setHasText(replaceArrowByText);
    }

    private int collapsedBackground = Color.WHITE;

    public void setCollapsedBackground(int value) {
        this.collapsedBackground = value;
        collapsedContainer.setContainerBackground(value);
        collapsedContainer.invalidate();
    }

    private int expandedBackground = Color.WHITE;

    public void setExpandedBackground(int value) {
        this.expandedBackground = value;
        expandedFilter.setBackgroundColor(value);
        expandedFilter.invalidate();
    }

    private boolean mIsBusy = false;

    private Boolean isCollapsed = null;

    private String STATE_SUPER = "state_super";
    private String STATE_SELECTED = "state_selected";
    private String STATE_REMOVED = "state_removed";
    private String STATE_COLLAPSED = "state_collapsed";

    private LinkedHashMap<FilterItem, Coord> mSelectedFilters = new LinkedHashMap<FilterItem, Coord>();
    private LinkedHashMap<FilterItem, Coord> mRemovedFilters = new LinkedHashMap<FilterItem, Coord>();
    private LinkedHashMap<FilterItem, T> mItems = new LinkedHashMap<FilterItem, T>();
    private List<T> mSelectedItems = new ArrayList<T>();
    private List<T> mRemovedItems = new ArrayList<T>();

    public Filter(Context context) {
        this(context, null);
    }

    public Filter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Filter(Context context, AttributeSet attrs, int defStyleRes) {
        super(context, attrs, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.filter, this, true);
        expandedFilter = (ExpandedFilterView) findViewById(R.id.expandedFilter);
        subFilters = (LinearLayout) findViewById(R.id.subFilters);
        collapsedContainer = (CollapsedFilterContainer) findViewById(R.id.collapsedContainer);
        expandedFilterScroll = (ScrollView) findViewById(R.id.expandedFilterScroll);
        container = (FrameLayout) findViewById(R.id.container);
        blackBackground = findViewById(R.id.blackBackground);
        relative_container = (RelativeLayout) findViewById(R.id.relative_container);
        dividerTop = findViewById(R.id.dividerTop);
        dividerBottom = findViewById(R.id.dividerBottom);
        collapsedText = (TextView) findViewById(R.id.collapsedText);
        collapsedFilterScroll = (HorizontalScrollView) findViewById(R.id.collapsedFilterScroll);
        collapsedFilter = (CollapsedFilterView) findViewById(R.id.collapsedFilter);
        collapseView = (CollapseView) findViewById(R.id.collapseView);
        collapseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        blackBackground.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse();
            }
        });
        collapsedFilter.scrollListener = this;
        collapsedContainer.listener = this;
        expandedFilter.listener = this;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Filter, 0, 0);
        try {
            collapsedContainer.setContainerBackground(attributes.getColor(R.styleable.Filter_f_collapsedBackground, Color.WHITE));
            expandedFilter.setBackgroundColor(attributes.getColor(R.styleable.Filter_f_expandedBackground, Color.WHITE));
        } finally {
            attributes.recycle();
        }
    }

    public void build() {
        if (!validate()) {
            return;
        }
        mItems.clear();
        expandedFilter.post(new Runnable() {
            @Override
            public void run() {
                if (adapter != null) {
                    for (int i = 0; i < adapter.getItems().size(); i++) {
                        FilterItem view = adapter.createView(i, adapter.getItems().get(i));
                        view.setListener(Filter.this);
                        expandedFilter.addView(view);
                        mItems.put(view, adapter.getItems().get(i));
                    }
                }
                if (isCollapsed == null) {
                    mIsBusy = false;
                    mRemovedFilters.clear();
                    isCollapsed = true;
                    removeItemsFromParent();
                    collapsedContainer.bringToFront();
                    collapsedContainer.requestFocus();
                    blackBackground.setClickable(false);
                    collapsedText.setVisibility(View.VISIBLE);
                    collapsedText.setAlpha(1F);
                    blackBackground.setAlpha(0F);
                    collapsedContainer.setTranslationY(-getHeight() + 28F + collapsedContainer.getHeight());
                    expandedFilterScroll.setTranslationY(-getMeasuredHeight() + collapsedContainer.getHeight());
                }
            }
        });
        expandedFilter.margin = margin;
        collapsedFilter.margin = margin;
    }

    public LinearLayout getSubFilters() {
        return subFilters;
    }

    public void refreshLayout() {
        if (!isCollapsed)
            collapsedContainer.setTranslationY(-getMeasuredHeight() + (expandedFilterScroll.getHeight()) + collapsedContainer.getHeight());
    }

    private boolean validate() {
        boolean var3;
        if (this.adapter != null && (this.adapter != null ? this.adapter.getItems() : null) != null) {
            Boolean var2;
            label27:
            {
                FilterAdapter var10000 = this.adapter;
                if (this.adapter != null) {
                    List var1 = var10000.getItems();
                    var2 = (var1.isEmpty());
                    break label27;
                }

                var2 = null;
            }

            if (var2 == null) {
                Intrinsics.throwNpe();
            }

            if (!var2) {
                return true;
            }
        }

        var3 = false;
        return var3;
    }

    public void collapse() {
        if (mIsBusy || collapsedFilter.isBusy) return;
        mIsBusy = true;
        mRemovedFilters.clear();

        isCollapsed = true;

        removeItemsFromParent();
        container.bringToFront();
        container.requestFocus();
        blackBackground.setClickable(false);
        if (focusListener != null)
            focusListener.onCollapsingStarted();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, Constant.ANIMATION_DURATION).setDuration(Constant.ANIMATION_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator it) {
                float ratio = (float) it.getAnimatedValue() / Constant.ANIMATION_DURATION;

                collapseView.rotateArrow(180 * (1 - ratio));
                collapseView.turnIntoArrow(ratio);
                int index = 0;
                for (FilterItem filterItem : mSelectedFilters.keySet()) {
                    int x = ViewGroupExtensions.calculateX(index, collapsedFilterScroll.getMeasuredWidth(), margin, filterItem.getCollapsedSize());
                    filterItem.decrease(ratio);
                    if (index >= ViewGroupExtensions.calculateCount(collapsedFilterScroll.getMeasuredWidth(), filterItem.getCollapsedSize(), margin)) {
                        filterItem.setAlpha(1 - ratio * 3);
                    } else {
                        filterItem.setTranslationX(filterItem.getStartX() + (x - filterItem.getStartX() - filterItem.getMeasuredWidth() / 2 + filterItem.getCollapsedSize() / 2) * ratio);
                        filterItem.setTranslationY(filterItem.getStartY() + (dpToPx(filterItem, getDimen(filterItem, R.dimen.margin)) / 4 - filterItem.getStartY()) * ratio);
                        //Log.i(Filter.this.getClass().getSimpleName(), "filterItem.translationX: " + filterItem.getTranslationX());
                    }

                    if (ratio == 1f) {
                        filterItem.removeFromParent();
                        collapsedFilter.addView(filterItem);
                        filterItem.setTranslationX(x - filterItem.getMeasuredWidth() / 2 + filterItem.getCollapsedSize() / 2);
                        filterItem.setTranslationY(dpToPx(filterItem, getDimen(filterItem, R.dimen.margin)) / 4);
                        filterItem.setAlpha(1f);
                        filterItem.bringToFront();
                    }
                    index++;
                }
                dividerTop.setAlpha(1 - 2 * ratio);
                expandedFilterScroll.setTranslationY((-container.getHeight() * (ratio)));
                collapsedContainer.setTranslationY(-getMeasuredHeight() + (expandedFilterScroll.getHeight() * (1 - ratio)) + collapsedContainer.getHeight());
                //Log.i(Filter.this.getClass().getSimpleName(), "Collapsing ------ collapsedContainer.translationY = " + collapsedContainer.getTranslationY() + "\texpandedFilterScroll.translationY: " + expandedFilterScroll.getTranslationY());

                blackBackground.setAlpha((1 - ratio) * 0.8F);
                if (mSelectedFilters.isEmpty()) {
                    collapsedText.setVisibility(View.VISIBLE);
                    collapsedText.setAlpha(ratio);
                } else {
                    collapsedText.setVisibility(View.GONE);
                    collapsedText.setAlpha(1 - ratio);
                }

                if (ratio == 1f) {
                    if (focusListener != null)
                        focusListener.onCollapsingFinished();
                    collapsedContainer.bringToFront();
                    collapsedContainer.requestFocus();
                    mIsBusy = false;
                    //Log.i(getClass().getSimpleName(), "measuredHeight: " + getMeasuredHeight());
                    //Log.i(getClass().getSimpleName(), "containerHeight: " + container.getHeight());
                    //Log.i(getClass().getSimpleName(), "collapsedContainerHeight: " + collapsedContainer.getHeight());
                    //Log.i(getClass().getSimpleName(), "expandedFilterHeight: " + expandedFilter.getHeight());
                    //Log.i(getClass().getSimpleName(), "expandedFilterScrollHeight: " + expandedFilterScroll.getHeight());
                    //Log.i(getClass().getSimpleName(), "collapsedContainerPaddingTop: " + collapsedContainer.getPaddingTop());
                    //Log.i(getClass().getSimpleName(), "collapsedFilterHeight: " + collapsedFilter.getHeight());
                    //Log.i(getClass().getSimpleName(), "resources.displayMetrics.heightPixels/80F: " + getResources().getDisplayMetrics().heightPixels / 80F);
                }

            }
        });
        valueAnimator.start();
        notifyListener();
    }

    public void expand() {
        if (collapsedFilter.isBusy || mIsBusy) return;

        mIsBusy = true;

        isCollapsed = false;

        removeItemsFromParent();
        container.bringToFront();
        container.requestFocus();
        blackBackground.setClickable(true);
        //collapsedContainer.translationY = 0F
        if (focusListener != null)
            focusListener.onExpandingStarted();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, Constant.ANIMATION_DURATION).setDuration(Constant.ANIMATION_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator it) {
                float ratio = (float) it.getAnimatedValue() / Constant.ANIMATION_DURATION;

                collapseView.rotateArrow(180 * ratio);
                collapseView.turnIntoOkButton(ratio);

                int index = 0;
                for (FilterItem filterItem : mSelectedFilters.keySet()) {
                    int x = mSelectedFilters.get(filterItem).getX();
                    int y = mSelectedFilters.get(filterItem).getY();

                    if (index < ViewGroupExtensions.calculateCount(collapsedFilterScroll.getMeasuredWidth(), filterItem.getCollapsedSize(), margin)) {
                        filterItem.setTranslationX(filterItem.getStartX() + (x - filterItem.getStartX()) * ratio);
                        filterItem.setTranslationY(filterItem.getStartY() + (y - filterItem.getStartY()) * ratio);
                    } else {
                        filterItem.setTranslationX(x);
                        filterItem.setTranslationY(y);
                        filterItem.setAlpha(ratio);
                    }
                    filterItem.increase(ratio);

                    if (ratio == 1f) {
                        filterItem.removeFromParent();
                        expandedFilter.addView(filterItem);
                        filterItem.setTranslationX(0F);
                        filterItem.setTranslationY(0F);
                    }
                    index++;
                }

                for (FilterItem filterItem : mRemovedFilters.keySet()) {
                    filterItem.setAlpha(ratio);

                    filterItem.removeFromParent();
                    expandedFilter.addView(filterItem);
                    filterItem.setTranslationX(mRemovedFilters.get(filterItem).getX() * (1 - ratio));
                    filterItem.setTranslationY(mRemovedFilters.get(filterItem).getY() * (1 - ratio));
                }
                collapsedText.setAlpha(1 - ratio);
                blackBackground.setAlpha(ratio * 0.8F);
                dividerTop.setAlpha(2 * ratio);
                expandedFilterScroll.setTranslationY(-container.getHeight() * (1 - ratio));
                collapsedContainer.setTranslationY(-getMeasuredHeight() + (expandedFilterScroll.getHeight() * ratio) + collapsedContainer.getHeight());
                //Log.i(Filter.this.getClass().getSimpleName(), "Expanding ------ collapsedContainer.translationY = " + collapsedContainer.getTranslationY() + "\texpandedFilterScroll.translationY: " + expandedFilterScroll.getTranslationY());

                if (ratio == 1f) {
                    if (focusListener != null)
                        focusListener.onExpandingFinished();
                    expandedFilterScroll.bringToFront();
                    expandedFilterScroll.requestFocus();
                    collapsedText.setVisibility(View.GONE);
                    mIsBusy = false;
                }
            }
        });
        valueAnimator.start();
        for (FilterItem filterItem : mRemovedFilters.keySet()) {
            int x = mRemovedFilters.get(filterItem).getX();
            int y = mRemovedFilters.get(filterItem).getY();

            filterItem.setTranslationX(x);
            filterItem.setTranslationY(y);
            filterItem.increase(1f);
            filterItem.deselect();
        }
    }

    private float getWidthItens(int i) {
        float width = 0F;
        int index = 0;
        for (FilterItem filterItem : mItems.keySet()) {
            if (index == i)
                return width;
            width += filterItem.getWidth() + (2 * dpToPx(filterItem, 22.5F));
        }
        return width;
    }

    private void removeItemsFromParent() {
        Iterable $receiver$iv = (Iterable) this.mSelectedFilters.keySet();
        Iterator var2 = $receiver$iv.iterator();
        while (var2.hasNext()) {
            Object element$iv = var2.next();
            FilterItem item = (FilterItem) element$iv;
            Intrinsics.checkExpressionValueIsNotNull(item, "item");
            this.remove(item);
        }

    }

    private void remove(FilterItem item) {
        float x = item.getX();
        float y = item.getY();
        item.removeFromParent();
        container.addView(item);
        item.setTranslationX(x);
        item.setTranslationY(y);
        item.setStartX(x);
        item.setStartY(y);
        item.bringToFront();
    }

    public void onItemSelected(FilterItem item) {
        T filter = mItems.get(item);
        if (mItems.containsValue(item)) {
            mSelectedItems.add(filter);
        }
        mSelectedFilters.put(item, new Coord((int) item.getX(), (int) item.getY()));
        if (listener != null)
            listener.onFilterSelected(filter);
    }

    public void onItemDeselected(FilterItem item) {
        T filter = mItems.get(item);
        if (mItems.containsValue(item)) {
            mSelectedItems.remove(filter);
        }
        mSelectedFilters.remove(item);
        if (listener != null)
            listener.onFilterDeselected(filter);
    }

    public void onItemRemoved(final FilterItem item) {
        Coord coord = mSelectedFilters.get(item);
        if (coord != null && collapsedFilter.removeItem(item)) {
            mSelectedFilters.remove(item);
            mSelectedItems.remove(mItems.get(item));
            mRemovedFilters.put(item, coord);

            new Runnable() {
                @Override
                public void run() {

                }
            };
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    remove(item);
                    if (mSelectedFilters.isEmpty()) {
                        collapsedText.setVisibility(VISIBLE);
                        collapsedText.setAlpha(1);
                    }
                }
            }, Constant.ANIMATION_DURATION / 2);

            notifyListener();
        }
    }

    private void notifyListener() {
        if (listener != null)
            if (mSelectedFilters.isEmpty()) {
                listener.onNothingSelected();
            } else {
                listener.onFiltersSelected(getSelectedItems());
            }
    }

    public ArrayList<T> getSelectedItems() {
        ArrayList<T> itens = new ArrayList<T>();
        for (FilterItem item : mSelectedFilters.keySet()) {
            T i = mItems.get(item);

            if (item != null) {
                itens.add(i);
            }
        }

        return itens;
    }

    private void deselectAll() {
        for (FilterItem item : mSelectedFilters.keySet())
            item.deselect(false);
        mSelectedFilters.clear();
        mSelectedItems.clear();
        if (listener != null)
            listener.onNothingSelected();
    }

//    override fun onSaveInstanceState(): Parcelable {
//        val superState = super.onSaveInstanceState()
//        return Bundle().apply {
//            putParcelable(STATE_SUPER, superState)
//            putBoolean(STATE_COLLAPSED, isCollapsed!!)
//            val selected = mSelectedItems
//            val removed = mRemovedItems
//            if (selected is Serializable) {
//                putSerializable(STATE_SELECTED, selected)
//            }
//            if (removed is Serializable) {
//                putSerializable(STATE_REMOVED, removed)
//            }
//        }
//    }
//
//    override fun onRestoreInstanceState(state: Parcelable?) {
//        if (state is Bundle) {
//            super.onRestoreInstanceState(state.getParcelable(STATE_SUPER))
//            val selected = state.getSerializable(STATE_SELECTED) as? List<T>
//            val removed = state.getSerializable(STATE_REMOVED) as? List<T>
//            isCollapsed = state.getBoolean(STATE_COLLAPSED)
//            if (selected is ArrayList<T> && removed is ArrayList<T>) {
//                mSelectedItems = selected
//                mRemovedItems = removed
//                expandedFilter.post {
//                    restore(expandedFilter.filters)
//                }
//            }
//        }
//    }

    private void restore(final LinkedHashMap<FilterItem, Coord> filters) {
        mSelectedFilters.clear();
        expandedFilter.post(new Runnable() {
            @Override
            public void run() {
                for (FilterItem filterItem : filters.keySet()) {
                    Coord coord = filters.get(filterItem);
                    T item = (T) mItems.get(filterItem);
                    if (mSelectedItems.contains(item)) {
                        mSelectedFilters.put(filterItem, coord);
                        filterItem.select(false);
                    } else if (mRemovedItems.contains(item)) {
                        mRemovedFilters.put(filterItem, coord);
                        filterItem.deselect(false);
                    }
                }
                if (isCollapsed == null || isCollapsed) {
                    collapse();
                } else {
                    expand();
                }
            }
        });
    }

    public void toggle() {
        if (collapsedFilter.isBusy || mIsBusy) return;
        if (isCollapsed != null && isCollapsed) expand();
        else collapse();
    }

    public void setAdapter(FilterAdapter<T> adapter) {
        this.adapter = adapter;
    }

    public void setFocusListener(FilterFocusListener focusListener) {
        this.focusListener = focusListener;
    }

    public void setListener(FilterListener<T> listener) {
        this.listener = listener;
    }
}
