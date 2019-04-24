package br.agr.terras.materialdroid.utils.filter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import br.agr.terras.materialdroid.R;
import br.agr.terras.materialdroid.R.id;
import kotlin.jvm.internal.Intrinsics;

public final class CollapsedFilterContainer extends RelativeLayout {
    @Nullable
    public CollapseListener listener;
    private float mStartX;
    private float mStartY;
    private int containerBackground;
    private HashMap _$_findViewCache;

    @Nullable
    public final CollapseListener getListener() {
        return this.listener;
    }

    public final void setListener(@Nullable CollapseListener var1) {
        this.listener = var1;
    }

    public final int getContainerBackground() {
        return this.containerBackground;
    }

    public final void setContainerBackground(int value) {
        this.containerBackground = value;
        ((RelativeLayout)this.findViewById(id.relative_container)).setBackgroundColor(value);
    }

    public boolean onInterceptTouchEvent(@NotNull MotionEvent ev) {
        Intrinsics.checkParameterIsNotNull(ev, "ev");
        boolean isEmpty = ((CollapsedFilterView)this.findViewById(id.collapsedFilter)).getChildCount() == 0;
        boolean containsEvent = ev.getX() >= ((CollapsedFilterView)this.findViewById(id.collapsedFilter)).getX() && ev.getX() <= ((CollapsedFilterView)this.findViewById(id.collapsedFilter)).getX() + (float)((CollapsedFilterView)this.findViewById(id.collapsedFilter)).getMeasuredWidth();
        return isEmpty || !containsEvent;
    }

    public boolean onTouchEvent(@NotNull MotionEvent event) {
        Intrinsics.checkParameterIsNotNull(event, "event");
        CollapseListener var10000;
        switch(event.getAction()) {
            case 0:
                this.mStartX = event.getX();
                this.mStartY = event.getY();
                break;
            case 1:
                if(!((CollapsedFilterView)this.findViewById(id.collapsedFilter)).isBusy() && ViewGroupExtensions.isClick(this.mStartX, this.mStartY, event.getX(), event.getY())) {
                    var10000 = this.listener;
                    if(this.listener != null) {
                        var10000.toggle();
                    }

                    this.mStartX = 0.0F;
                    this.mStartY = 0.0F;
                }
                break;
            case 2:
                if(!((CollapsedFilterView)this.findViewById(id.collapsedFilter)).isBusy() && Math.abs(this.mStartX - event.getX()) < (float)20 && event.getY() - this.mStartY > (float)20) {
                    var10000 = this.listener;
                    if(this.listener != null) {
                        var10000.expand();
                    }

                    this.mStartX = 0.0F;
                    this.mStartY = 0.0F;
                } else if(!((CollapsedFilterView)this.findViewById(id.collapsedFilter)).isBusy() && Math.abs(this.mStartX - event.getX()) < (float)20 && event.getY() - this.mStartY < (float)-20) {
                    var10000 = this.listener;
                    if(this.listener != null) {
                        var10000.collapse();
                    }

                    this.mStartX = 0.0F;
                    this.mStartY = 0.0F;
                }
        }

        return true;
    }

    public CollapsedFilterContainer(@NotNull Context context) {
        this(context, (AttributeSet)null);
        Intrinsics.checkParameterIsNotNull(context, "context");
    }

    public CollapsedFilterContainer(@NotNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        Intrinsics.checkParameterIsNotNull(context, "context");
    }

    public CollapsedFilterContainer(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleRes) {
        super(context, attrs, defStyleRes);
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.containerBackground = -1;
        LayoutInflater.from(context).inflate(R.layout.collapsed_container, (ViewGroup)this, true);
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
