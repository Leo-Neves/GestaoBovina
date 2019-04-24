package br.agr.terras.materialdroid.utils.filter;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import br.agr.terras.materialdroid.R.id;
import br.agr.terras.materialdroid.R.layout;
import kotlin.jvm.internal.Intrinsics;

public final class CollapseView extends FrameLayout {
    private HashMap findViewCache;

    public final void setText(@NotNull String text) {
        Intrinsics.checkParameterIsNotNull(text, "text");
        ((AppCompatButton)this.findViewById(id.buttonOk)).setText((CharSequence)text);
    }

    public final void setHasText(boolean hasText) {
        ((AppCompatButton)this.findViewById(id.buttonOk)).setVisibility(hasText?VISIBLE:GONE);
    }

    public final void rotateArrow(float rotation) {
        ((AppCompatImageView)this.findViewById(id.imageArrow)).setRotation(rotation);
    }

    public final void turnIntoOkButton(float ratio) {
        if(((AppCompatButton)this.findViewById(id.buttonOk)).getVisibility() == VISIBLE) {
            this.scale(this.getIncreasingScale(ratio), this.getDecreasingScale(ratio));
        }
    }

    public final void turnIntoArrow(float ratio) {
        if(((AppCompatButton)this.findViewById(id.buttonOk)).getVisibility() == VISIBLE) {
            this.scale(this.getDecreasingScale(ratio), this.getIncreasingScale(ratio));
        }
    }

    private final float getIncreasingScale(float ratio) {
        return ratio < 0.5F?0.0F:(float)2 * ratio - (float)1;
    }

    private final float getDecreasingScale(float ratio) {
        return ratio > 0.5F?0.0F:(float)1 - (float)2 * ratio;
    }

    private final void scale(float okScale, float arrowScale) {
        ((AppCompatButton)this.findViewById(id.buttonOk)).setScaleX(okScale);
        ((AppCompatButton)this.findViewById(id.buttonOk)).setScaleY(okScale);
        ((AppCompatImageView)this.findViewById(id.imageArrow)).setScaleX(arrowScale);
        ((AppCompatImageView)this.findViewById(id.imageArrow)).setScaleY(arrowScale);
    }

    public void setOnClickListener(@Nullable OnClickListener l) {
        ((AppCompatButton)this.findViewById(id.buttonOk)).setOnClickListener(l);
        ((AppCompatImageView)this.findViewById(id.imageArrow)).setOnClickListener(l);
    }

    public CollapseView(@Nullable Context context) {
        this(context, (AttributeSet)null);
    }

    public CollapseView(@Nullable Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapseView(@Nullable Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(layout.view_collapse, (ViewGroup)this, true);
    }

    public View findCachedViewById(int var1) {
        if(this.findViewCache == null) {
            this.findViewCache = new HashMap();
        }

        View var2 = (View)this.findViewCache.get(Integer.valueOf(var1));
        if(var2 == null) {
            var2 = this.findViewById(var1);
            this.findViewCache.put(Integer.valueOf(var1), var2);
        }

        return var2;
    }

    public void clearFindViewByIdCache() {
        if(this.findViewCache != null) {
            this.findViewCache.clear();
        }

    }
}
