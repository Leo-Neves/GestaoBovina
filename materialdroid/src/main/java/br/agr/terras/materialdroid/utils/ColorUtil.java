package br.agr.terras.materialdroid.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.util.TypedValue;

import br.agr.terras.materialdroid.R;

/**
 * Created by leo on 18/05/16.
 */
public class ColorUtil {

    public static int getColorPrimary(Activity activity){
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        return color;
    }

    private static int getMiddleValue(int prev, int next, float factor){
        return Math.round(prev + (next - prev) * factor);
    }

    public static int getMiddleColor(int prevColor, int curColor, float factor){
        if(prevColor == curColor)
            return curColor;

        if(factor == 0f)
            return prevColor;
        else if(factor == 1f)
            return curColor;

        int a = getMiddleValue(Color.alpha(prevColor), Color.alpha(curColor), factor);
        int r = getMiddleValue(Color.red(prevColor), Color.red(curColor), factor);
        int g = getMiddleValue(Color.green(prevColor), Color.green(curColor), factor);
        int b = getMiddleValue(Color.blue(prevColor), Color.blue(curColor), factor);

        return Color.argb(a, r, g, b);
    }

    public static int getColor(int baseColor, float alphaPercent){
        int alpha = Math.round(Color.alpha(baseColor) * alphaPercent);

        return (baseColor & 0x00FFFFFF) | (alpha << 24);
    }

    public static int HSBtoColor(float h, float s, float b) {
        h = constrain(h, 0.0f, 1.0f);
        s = constrain(s, 0.0f, 1.0f);
        b = constrain(b, 0.0f, 1.0f);

        float red = 0.0f;
        float green = 0.0f;
        float blue = 0.0f;

        final float hf = (h - (int) h) * 6.0f;
        final int ihf = (int) hf;
        final float f = hf - ihf;
        final float pv = b * (1.0f - s);
        final float qv = b * (1.0f - s * f);
        final float tv = b * (1.0f - s * (1.0f - f));

        switch (ihf) {
            case 0:         // Red is the dominant color
                red = b;
                green = tv;
                blue = pv;
                break;
            case 1:         // Green is the dominant color
                red = qv;
                green = b;
                blue = pv;
                break;
            case 2:
                red = pv;
                green = b;
                blue = tv;
                break;
            case 3:         // Blue is the dominant color
                red = pv;
                green = qv;
                blue = b;
                break;
            case 4:
                red = tv;
                green = pv;
                blue = b;
                break;
            case 5:         // Red is the dominant color
                red = b;
                green = pv;
                blue = qv;
                break;
        }

        return 0xFF000000 | (((int) (red * 255.0f)) << 16) |
                (((int) (green * 255.0f)) << 8) | ((int) (blue * 255.0f));
    }

    private static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    public static float hue(@ColorInt int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int V = Math.max(b, Math.max(r, g));
        int temp = Math.min(b, Math.min(r, g));

        float H;

        if (V == temp) {
            H = 0;
        } else {
            final float vtemp = (float) (V - temp);
            final float cr = (V - r) / vtemp;
            final float cg = (V - g) / vtemp;
            final float cb = (V - b) / vtemp;

            if (r == V) {
                H = cb - cg;
            } else if (g == V) {
                H = 2 + cr - cb;
            } else {
                H = 4 + cg - cr;
            }

            H /= 6.f;
            if (H < 0) {
                H++;
            }
        }

        return H;
    }

    /**
     * Returns the saturation component of a color int.
     *
     * @return A value between 0.0f and 1.0f
     *
     * @hide Pending API council
     */
    public static float saturation(@ColorInt int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;


        int V = Math.max(b, Math.max(r, g));
        int temp = Math.min(b, Math.min(r, g));

        float S;

        if (V == temp) {
            S = 0;
        } else {
            S = (V - temp) / (float) V;
        }

        return S;
    }

    /**
     * Returns the brightness component of a color int.
     *
     * @return A value between 0.0f and 1.0f
     *
     * @hide Pending API council
     */
    public static float brightness(@ColorInt int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        int V = Math.max(b, Math.max(r, g));

        return (V / 255.f);
    }

    public static int getFlatBlueColor(Context context, int position){
        int cor = Color.WHITE;
        switch (position){
            case 0:
                cor = context.getResources().getColor(R.color.flat_teal);
                break;
            case 1:
                cor = context.getResources().getColor(R.color.flat_blue);
                break;
            case 2:
                cor = context.getResources().getColor(R.color.flat_pink_light);
                break;
            case 3:
                cor = context.getResources().getColor(R.color.flat_gray);
                break;
            case 4:
                cor = context.getResources().getColor(R.color.flat_purple);
                break;
            case 5:
                cor = context.getResources().getColor(R.color.flat_swimming);
                break;
            case 6:
                cor = context.getResources().getColor(R.color.flat_lavender);
                break;
            case 7:
                cor = context.getResources().getColor(R.color.flat_rose);
                break;
            case 8:
                cor = context.getResources().getColor(R.color.flat_royal);
                break;
            case 9:
                cor = context.getResources().getColor(R.color.flat_concrete);
                break;
        }
        return cor;
    }

    public static int getFlatYellowColor(Context context, int position) {
        int cor = Color.WHITE;
        switch (position) {
            case 0:
                cor = context.getResources().getColor(R.color.flat_grass);
                break;
            case 1:
                cor = context.getResources().getColor(R.color.flat_avacado);
                break;
            case 2:
                cor = context.getResources().getColor(R.color.flat_carrot);
                break;
            case 3:
                cor = context.getResources().getColor(R.color.flat_hibiscus);
                break;
            case 4:
                cor = context.getResources().getColor(R.color.flat_pumpkin);
                break;
        }
        return cor;
    }

    public static int getFlatYellowColorBorder(int position) {
        if (position==(1|2|3))
        return Color.parseColor("#90000000");
        return Color.parseColor("#90FFFFFF");
    }

}
