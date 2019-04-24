// CollapsedFilterView.java
package br.agr.terras.materialdroid.utils.filter;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import br.agr.terras.materialdroid.R.dimen;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

public final class CollapsedFilterView extends ViewGroup {
    public int margin;
    public boolean isBusy;
    @Nullable
    public CollapseListener scrollListener;
    private float mStartX;
    private float mStartY;
    private int mRealMargin;
    private HashMap _$_findViewCache;

    public final int getMargin() {
        return this.margin;
    }

    public final void setMargin(int var1) {
        this.margin = var1;
    }

    public final boolean isBusy() {
        return this.isBusy;
    }

    public final void setBusy(boolean var1) {
        this.isBusy = var1;
    }

    @Nullable
    public final CollapseListener getScrollListener() {
        return this.scrollListener;
    }

    public final void setScrollListener(@Nullable CollapseListener var1) {
        this.scrollListener = var1;
    }

    protected void onLayout(boolean p0, int p1, int p2, int p3, int p4) {
        int i = 0;
        int var7 = this.getChildCount() - 1;
        if(i <= var7) {
            while(true) {
                View var10000 = this.getChildAt(i);
                if(var10000 == null) {
                    throw new TypeCastException("null cannot be cast to non-null type br.agr.terras.materialdroid.utils.filter.FilterItem");
                }

                FilterItem child = (FilterItem)var10000;
                child.layout(0, 0, child.getCollapsedSize() / 2 + child.getMeasuredWidth() / 2 + 1, child.getMeasuredHeight());
                if(i == var7) {
                    break;
                }

                ++i;
            }
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(this.getChildCount() > 0) {
            View var10000 = this.getChildAt(0);
            if(var10000 == null) {
                throw new TypeCastException("null cannot be cast to non-null type br.agr.terras.materialdroid.utils.filter.FilterItem");
            }

            FilterItem child = (FilterItem)var10000;
            child.measure(-2, -2);
            ViewParent var10001 = this.getParent();
            if(var10001 == null) {
                throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
            }

            this.mRealMargin = ViewGroupExtensions.calculateMargin(((ViewGroup)var10001).getMeasuredWidth(), child.getCollapsedSize(), this.margin);
            int width = this.getChildCount() * child.getCollapsedSize() + this.getChildCount() * this.mRealMargin + this.mRealMargin;
            this.setMeasuredDimension(width, ViewGroupExtensions.calculateSize(this.margin * 2 + child.getCollapsedSize(), -1));
        } else {
            this.setMeasuredDimension(0, 0);
        }

    }

    public final boolean removeItem(@NotNull FilterItem child) {
        Intrinsics.checkParameterIsNotNull(child, "child");
        if(this.isBusy) {
            return false;
        } else {
            int index = this.indexOfChild((View)child);
            this.isBusy = true;
            ValueAnimator var3 = ValueAnimator.ofFloat(new float[]{0.0F, (float)Constant.ANIMATION_DURATION / (float)2}).setDuration(Constant.ANIMATION_DURATION / (long)2);
            var3.addUpdateListener((AnimatorUpdateListener)(new CollapsedFilterView$removeItem$$inlined$apply$lambda$1(this, index, child)));
            var3.start();
            return true;
        }
    }

    public boolean onInterceptTouchEvent(@Nullable MotionEvent ev) {
        return this.getChildCount() > 0;
    }

    public boolean onTouchEvent(@NotNull MotionEvent event) {
        Intrinsics.checkParameterIsNotNull(event, "event");
        switch(event.getAction()) {
            case 0:
                this.mStartX = event.getX();
                this.mStartY = event.getY();
                break;
            case 1:
                if(!this.isBusy && ViewGroupExtensions.isClick(this.mStartX, this.mStartY, event.getX(), event.getY())) {
                    FilterItem var2 = this.findViewByCoord(event.getX());
                    if(var2 != null) {
                        var2.dismiss();
                    }
                }
                break;
            case 2:
                if(Math.abs(this.mStartX - event.getX()) < (float)20 && event.getY() - this.mStartY > (float)20) {
                    if(!this.isBusy) {
                        CollapseListener var10000 = this.scrollListener;
                        if(this.scrollListener != null) {
                            var10000.expand();
                        }
                    }

                    this.mStartX = 0.0F;
                    this.mStartY = 0.0F;
                }
        }

        return true;
    }

    private final FilterItem findViewByCoord(float x) {
        int i = 0;
        int var3 = this.getChildCount() - 1;
        if(i <= var3) {
            while(true) {
                View var10000 = this.getChildAt(i);
                if(var10000 == null) {
                    throw new TypeCastException("null cannot be cast to non-null type br.agr.terras.materialdroid.utils.filter.FilterItem");
                }

                FilterItem item = (FilterItem)var10000;
                if(this.containsCoord(item, x)) {
                    return item;
                }

                if(i == var3) {
                    break;
                }

                ++i;
            }
        }

        return null;
    }

    private final boolean containsCoord(FilterItem item, float x) {
        return item.getX() + (float)(item.getFullSize() / 2) - (float)(item.getCollapsedSize() / 2) <= x && x <= item.getX() + (float)(item.getFullSize() / 2) + (float)(item.getCollapsedSize() / 2);
    }

    public CollapsedFilterView(@NotNull Context context) {
        this(context, (AttributeSet)null);
        Intrinsics.checkParameterIsNotNull(context, "context");
    }

    public CollapsedFilterView(@NotNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        Intrinsics.checkParameterIsNotNull(context, "context");
    }

    public CollapsedFilterView(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleRes) {
        super(context, attrs, defStyleRes);
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.margin = ViewGroupExtensions.dpToPx((ViewGroup)this, ViewGroupExtensions.getDimen((ViewGroup)this, dimen.margin));
        this.mRealMargin = this.margin;
    }

    // $FF: synthetic method
    public static final int access$getMRealMargin$p(CollapsedFilterView $this) {
        return $this.mRealMargin;
    }

    // $FF: synthetic method
    public static final void access(CollapsedFilterView $this, int var1) {
        $this.mRealMargin = var1;
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
// CollapsedFilterView$removeItem$$inli
final class CollapsedFilterView$removeItem$$inlined$apply$lambda$1 implements AnimatorUpdateListener {
    // $FF: synthetic field
    final CollapsedFilterView this$0;
    // $FF: synthetic field
    final int $index$inlined;
    // $FF: synthetic field
    final FilterItem $child$inlined;

    CollapsedFilterView$removeItem$$inlined$apply$lambda$1(CollapsedFilterView var1, int var2, FilterItem var3) {
        this.this$0 = var1;
        this.$index$inlined = var2;
        this.$child$inlined = var3;
    }

    public final void onAnimationUpdate(ValueAnimator it) {
        Object var10000 = it.getAnimatedValue();
        if(var10000 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Float");
        } else {
            float ratio = ((Float)var10000).floatValue() / (float)(Constant.ANIMATION_DURATION / (long)2);
            int i = this.$index$inlined + 1;
            int var4 = this.this$0.getChildCount() - 1;
            if(i <= var4) {
                while(true) {
                    View var6 = this.this$0.getChildAt(i);
                    if(var6 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type br.agr.terras.materialdroid.utils.filter.FilterItem");
                    }

                    FilterItem item = (FilterItem)var6;
                    if(ratio == 0.0F) {
                        item.setStartX(item.getX());
                    }

                    item.setTranslationX(item.getStartX() + (float)(-this.$child$inlined.getCollapsedSize() - CollapsedFilterView.access$getMRealMargin$p(this.this$0)) * ratio);
                    this.$child$inlined.setAlpha((float)1 - ratio);
                    if(i == var4) {
                        break;
                    }

                    ++i;
                }
            }

            if(ratio == 1.0F) {
                this.$child$inlined.setTranslationX(this.$child$inlined.getStartX() + (float)(-this.$child$inlined.getCollapsedSize() - CollapsedFilterView.access$getMRealMargin$p(this.this$0)) * ratio);
                this.this$0.setBusy(false);
            }

        }
    }
}
