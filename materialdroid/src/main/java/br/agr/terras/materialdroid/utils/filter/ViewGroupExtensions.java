package br.agr.terras.materialdroid.utils.filter;

import android.util.DisplayMetrics;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

import org.jetbrains.annotations.NotNull;

import kotlin.jvm.internal.Intrinsics;

public final class ViewGroupExtensions {
    public static final int calculateSize(int measureSpec, int desiredSize) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int var10000;
        switch(mode) {
            case Integer.MIN_VALUE:
                var10000 = Math.min(desiredSize, size);
                break;
            case 1073741824:
                var10000 = size;
                break;
            default:
                var10000 = desiredSize;
        }

        int actualSize = var10000;
        return actualSize;
    }

    public static final int dpToPx(@NotNull ViewGroup $receiver, int dp) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        DisplayMetrics displayMetrics = $receiver.getContext().getResources().getDisplayMetrics();
        return Math.round((float)dp * (displayMetrics.xdpi / (float)160));
    }

    public static final int dpToPx(@NotNull ViewGroup $receiver, float dp) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        DisplayMetrics displayMetrics = $receiver.getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / (float)160));
    }

    public static final int getDimen(@NotNull ViewGroup $receiver, int res) {
        Intrinsics.checkParameterIsNotNull($receiver, "$receiver");
        return $receiver.getContext().getResources().getDimensionPixelOffset(res);
    }

    public static final int calculateX(int position, int size, int minMargin, int itemSize) {
        int realMargin = calculateMargin(size, itemSize, minMargin);
        return position * itemSize + position * realMargin + realMargin;
    }

    public static final int calculateMargin(int width, int itemWidth, int margin) {
        int count = calculateCount(width, itemWidth, margin);
        return count > 0?(width - count * itemWidth) / count:0;
    }

    public static final int calculateCount(int width, int itemWidth, int margin) {
        return width / (itemWidth + margin);
    }

    public static final boolean isClick(float startX, float startY, float x, float y) {
        return Math.abs(x - startX) < (float)20 && Math.abs(y - startY) < (float)20;
    }
}
