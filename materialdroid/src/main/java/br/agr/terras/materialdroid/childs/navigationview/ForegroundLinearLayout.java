package br.agr.terras.materialdroid.childs.navigationview;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.design.R.styleable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;

@RestrictTo({Scope.LIBRARY_GROUP})
public class ForegroundLinearLayout extends LinearLayoutCompat {
    private Drawable foreground;
    private final Rect selfBounds;
    private final Rect overlayBounds;
    private int foregroundGravity;
    protected boolean mForegroundInPadding;
    boolean foregroundBoundsChanged;

    public ForegroundLinearLayout(Context context) {
        this(context, (AttributeSet)null);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.selfBounds = new Rect();
        this.overlayBounds = new Rect();
        this.foregroundGravity = 119;
        this.mForegroundInPadding = true;
        this.foregroundBoundsChanged = false;
        TypedArray a = ThemeEnforcement.obtainStyledAttributes(context, attrs, styleable.ForegroundLinearLayout, defStyle, 0, new int[0]);
        this.foregroundGravity = a.getInt(styleable.ForegroundLinearLayout_android_foregroundGravity, this.foregroundGravity);
        Drawable d = a.getDrawable(styleable.ForegroundLinearLayout_android_foreground);
        if (d != null) {
            this.setForeground(d);
        }

        this.mForegroundInPadding = a.getBoolean(styleable.ForegroundLinearLayout_foregroundInsidePadding, true);
        a.recycle();
    }

    public int getForegroundGravity() {
        return this.foregroundGravity;
    }

    public void setForegroundGravity(int foregroundGravity) {
        if (this.foregroundGravity != foregroundGravity) {
            if ((foregroundGravity & 8388615) == 0) {
                foregroundGravity |= 8388611;
            }

            if ((foregroundGravity & 112) == 0) {
                foregroundGravity |= 48;
            }

            this.foregroundGravity = foregroundGravity;
            if (this.foregroundGravity == 119 && this.foreground != null) {
                Rect padding = new Rect();
                this.foreground.getPadding(padding);
            }

            this.requestLayout();
        }

    }

    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.foreground;
    }

    @RequiresApi(11)
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.foreground != null) {
            this.foreground.jumpToCurrentState();
        }

    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.foreground != null && this.foreground.isStateful()) {
            this.foreground.setState(this.getDrawableState());
        }

    }

    public void setForeground(Drawable drawable) {
        if (this.foreground != drawable) {
            if (this.foreground != null) {
                this.foreground.setCallback((Callback)null);
                this.unscheduleDrawable(this.foreground);
            }

            this.foreground = drawable;
            if (drawable != null) {
                this.setWillNotDraw(false);
                drawable.setCallback(this);
                if (drawable.isStateful()) {
                    drawable.setState(this.getDrawableState());
                }

                if (this.foregroundGravity == 119) {
                    Rect padding = new Rect();
                    drawable.getPadding(padding);
                }
            } else {
                this.setWillNotDraw(true);
            }

            this.requestLayout();
            this.invalidate();
        }

    }

    public Drawable getForeground() {
        return this.foreground;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.foregroundBoundsChanged |= changed;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.foregroundBoundsChanged = true;
    }

    public void draw(@NonNull Canvas canvas) {
        super.draw(canvas);
        if (this.foreground != null) {
            Drawable foreground = this.foreground;
            if (this.foregroundBoundsChanged) {
                this.foregroundBoundsChanged = false;
                Rect selfBounds = this.selfBounds;
                Rect overlayBounds = this.overlayBounds;
                int w = this.getRight() - this.getLeft();
                int h = this.getBottom() - this.getTop();
                if (this.mForegroundInPadding) {
                    selfBounds.set(0, 0, w, h);
                } else {
                    selfBounds.set(this.getPaddingLeft(), this.getPaddingTop(), w - this.getPaddingRight(), h - this.getPaddingBottom());
                }

                Gravity.apply(this.foregroundGravity, foreground.getIntrinsicWidth(), foreground.getIntrinsicHeight(), selfBounds, overlayBounds);
                foreground.setBounds(overlayBounds);
            }

            foreground.draw(canvas);
        }

    }

    @TargetApi(21)
    @RequiresApi(21)
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        if (this.foreground != null) {
            this.foreground.setHotspot(x, y);
        }

    }
}
