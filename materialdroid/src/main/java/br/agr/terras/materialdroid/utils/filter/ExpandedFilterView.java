package br.agr.terras.materialdroid.utils.filter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import br.agr.terras.materialdroid.R.dimen;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

public final class ExpandedFilterView extends ViewGroup {
    private View mPrevItem;
    private Integer mPrevX;
    private Integer mPrevY;
    private int mPrevHeight;
    private float mStartX;
    private float mStartY;
    @Nullable
    public CollapseListener listener;
    public int margin;
    @NotNull
    public LinkedHashMap<FilterItem, Coord> filters;
    private HashMap _$_findViewCache;

    @Nullable
    public final CollapseListener getListener() {
        return this.listener;
    }

    public final void setListener(@Nullable CollapseListener var1) {
        this.listener = var1;
    }

    public final int getMargin() {
        return this.margin;
    }

    public final void setMargin(int var1) {
        this.margin = var1;
    }

    @NotNull
    public final LinkedHashMap getFilters() {
        return this.filters;
    }

    protected void onLayout(boolean p0, int p1, int p2, int p3, int p4) {
        if(!this.filters.isEmpty()) {
            int i = 0;
            int var7 = this.getChildCount() - 1;
            if(i <= var7) {
                while(true) {
                    View var10000 = this.getChildAt(i);
                    Intrinsics.checkExpressionValueIsNotNull(var10000, "getChildAt(i)");
                    View child = var10000;
                    Map var10 = (Map)this.filters;
                    if(var10 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.Map<K, V>");
                    }

                    Coord coord = (Coord)var10.get(child);
                    if(coord != null) {
                        child.layout(coord.getX(), coord.getY(), coord.getX() + child.getMeasuredWidth(), coord.getY() + child.getMeasuredHeight());
                    }

                    if(i == var7) {
                        break;
                    }

                    ++i;
                }
            }
        }

    }

    private final boolean canPlaceOnTheSameLine(View filterItem) {
        if(this.mPrevItem != null) {
            Integer var10000 = this.mPrevX;
            if(this.mPrevX == null) {
                Intrinsics.throwNpe();
            }

            int var3 = var10000.intValue();
            View var10001 = this.mPrevItem;
            if(this.mPrevItem == null) {
                Intrinsics.throwNpe();
            }

            int occupiedWidth = var3 + var10001.getMeasuredWidth() + this.margin + filterItem.getMeasuredWidth();
            return occupiedWidth <= this.getMeasuredWidth();
        } else {
            return false;
        }
    }

    private  int calculateDesiredHeight() {
        int height = mPrevHeight;

        if (filters.isEmpty()) {
            for (int i =0;i<getChildCount();i++) {
                FilterItem child = (FilterItem) getChildAt(i);

                child.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                if (mPrevItem == null) {
                    mPrevX = margin;
                    mPrevY = margin;
                    height = child.getMeasuredHeight() + margin;
                } else if (canPlaceOnTheSameLine(child)) {
                    mPrevX = mPrevX + mPrevItem.getMeasuredWidth() + margin / 2;
                } else {
                    mPrevX = margin;
                    mPrevY = mPrevY + mPrevItem.getMeasuredHeight() + margin / 2;
                    height += child.getMeasuredHeight() + margin / 2;
                }

                mPrevItem = child;

                if (filters.size() < getChildCount()) {
                    filters.put(child, new Coord(mPrevX, mPrevY));
                }
            }
            height = (height > 0)? height + margin : 0;
            mPrevHeight = height;
        }

        return mPrevHeight;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(ViewGroupExtensions.calculateSize(widthMeasureSpec, -1), ViewGroupExtensions.calculateSize(heightMeasureSpec, this.calculateDesiredHeight()));
    }

    public boolean onTouchEvent(@NotNull MotionEvent event) {
        Intrinsics.checkParameterIsNotNull(event, "event");
        switch(event.getAction()) {
            case 0:
                this.mStartX = event.getX();
                this.mStartY = event.getY();
                break;
            case 2:
                if(event.getY() - this.mStartY < (float)-20) {
                    CollapseListener var10000 = this.listener;
                    if(this.listener != null) {
                        var10000.collapse();
                    }

                    this.mStartX = 0.0F;
                    this.mStartY = 0.0F;
                }
        }

        return true;
    }

    public ExpandedFilterView(@NotNull Context context) {
        this(context, (AttributeSet)null);
        Intrinsics.checkParameterIsNotNull(context, "context");
    }

    public ExpandedFilterView(@NotNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        Intrinsics.checkParameterIsNotNull(context, "context");
    }

    public ExpandedFilterView(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleRes) {
        super(context, attrs, defStyleRes);
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.margin = ViewGroupExtensions.dpToPx((ViewGroup)this, ViewGroupExtensions.getDimen((ViewGroup)this, dimen.margin));
        this.filters = new LinkedHashMap();
    }

    public View _$_findCachedViewById(int var1) {
        if(this._$_findViewCache == null) {
            this._$_findViewCache = new HashMap();
        }

        View var2 = (View)this._$_findViewCache.get(Integer.valueOf(var1));
        if(var2 == null) {
            var2 = this.findViewById(var1);
            this._$_findViewCache.put(Integer.valueOf(var1), var2);
        }

        return var2;
    }

    public void _$_clearFindViewByIdCache() {
        if(this._$_findViewCache != null) {
            this._$_findViewCache.clear();
        }

    }
}
