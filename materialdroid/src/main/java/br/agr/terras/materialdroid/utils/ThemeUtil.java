package br.agr.terras.materialdroid.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.graphics.ColorUtils;
import android.util.TypedValue;

import br.agr.terras.materialdroid.R;

/**
 * Created by leo on 18/05/16.
 */
public class ThemeUtil {

    private static TypedValue value;

    public static int dpToPx(Context context, int dp){
        return (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public static int spToPx(Context context, int sp){
        return (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics()) + 0.5f);
    }

    private static int getColor(Context context, int id, int defaultValue){
        if(value == null)
            value = new TypedValue();

        try{
            android.content.res.Resources.Theme theme = context.getTheme();
            if(theme != null && theme.resolveAttribute(id, value, true)){
                if (value.type >= TypedValue.TYPE_FIRST_INT && value.type <= TypedValue.TYPE_LAST_INT)
                    return value.data;
                else if (value.type == TypedValue.TYPE_STRING)
                    return context.getResources().getColor(value.resourceId);
            }
        }
        catch(Exception ex){}

        return defaultValue;
    }

    public static int windowBackground(Context context, int defaultValue){
        return getColor(context, android.R.attr.windowBackground, defaultValue);
    }

    public static int textColorPrimary(Context context, int defaultValue){
        return getColor(context, android.R.attr.textColorPrimary, defaultValue);
    }

    public static int textColorSecondary(Context context, int defaultValue){
        return getColor(context, android.R.attr.textColorSecondary, defaultValue);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int colorPrimary(Context context, int defaultValue){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return getColor(context, android.R.attr.colorPrimary, defaultValue);

        return getColor(context, R.attr.colorPrimary, defaultValue);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int colorPrimaryDark(Context context, int defaultValue){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return getColor(context, android.R.attr.colorPrimaryDark, defaultValue);

        return getColor(context, R.attr.colorPrimaryDark, defaultValue);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int colorAccent(Context context, int defaultValue){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return getColor(context, android.R.attr.colorAccent, defaultValue);

        return getColor(context, R.attr.colorAccent, defaultValue);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int colorControlNormal(Context context, int defaultValue){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return getColor(context, android.R.attr.colorControlNormal, defaultValue);

        return getColor(context, R.attr.colorControlNormal, defaultValue);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int colorControlActivated(Context context, int defaultValue){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return getColor(context, android.R.attr.colorControlActivated, defaultValue);

        return getColor(context, R.attr.colorControlActivated, defaultValue);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int colorControlHighlight(Context context, int defaultValue){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return getColor(context, android.R.attr.colorControlHighlight, defaultValue);

        return getColor(context, R.attr.colorControlHighlight, defaultValue);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int colorButtonNormal(Context context, int defaultValue){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return getColor(context, android.R.attr.colorButtonNormal, defaultValue);

        return getColor(context, R.attr.colorButtonNormal, defaultValue);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static int colorSwitchThumbNormal(Context context, int defaultValue){
        return getColor(context, R.attr.colorSwitchThumbNormal, defaultValue);
    }

    public static int getType(TypedArray array, int index){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return array.getType(index);
        else{
            TypedValue value = array.peekValue(index);
            return value == null ? TypedValue.TYPE_NULL : value.type;
        }
    }

    public static CharSequence getString(TypedArray array, int index, CharSequence defaultValue){
        String result = array.getString(index);
        return result == null ? defaultValue : result;
    }

    private static final ThreadLocal<TypedValue> TL_TYPED_VALUE = new ThreadLocal<>();
    static final int[] DISABLED_STATE_SET = new int[]{-android.R.attr.state_enabled};
    static final int[] FOCUSED_STATE_SET = new int[]{android.R.attr.state_focused};
    static final int[] ACTIVATED_STATE_SET = new int[]{android.R.attr.state_activated};
    static final int[] PRESSED_STATE_SET = new int[]{android.R.attr.state_pressed};
    static final int[] CHECKED_STATE_SET = new int[]{android.R.attr.state_checked};
    static final int[] SELECTED_STATE_SET = new int[]{android.R.attr.state_selected};
    static final int[] NOT_PRESSED_OR_FOCUSED_STATE_SET = new int[]{
            -android.R.attr.state_pressed, -android.R.attr.state_focused};
    static final int[] EMPTY_STATE_SET = new int[0];
    private static final int[] TEMP_ARRAY = new int[1];
    public static ColorStateList createDisabledStateList(int textColor, int disabledTextColor) {
        // Now create a new ColorStateList com the default color, and the new disabled
        // color
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        int i = 0;
        // Disabled state
        states[i] = DISABLED_STATE_SET;
        colors[i] = disabledTextColor;
        i++;
        // Default state
        states[i] = EMPTY_STATE_SET;
        colors[i] = textColor;
        i++;
        return new ColorStateList(states, colors);
    }
    public static int getThemeAttrColor(Context context, int attr) {
        TEMP_ARRAY[0] = attr;
        TypedArray a = context.obtainStyledAttributes(null, TEMP_ARRAY);
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }
    public static ColorStateList getThemeAttrColorStateList(Context context, int attr) {
        TEMP_ARRAY[0] = attr;
        TypedArray a = context.obtainStyledAttributes(null, TEMP_ARRAY);
        try {
            return a.getColorStateList(0);
        } finally {
            a.recycle();
        }
    }
    public static int getDisabledThemeAttrColor(Context context, int attr) {
        final ColorStateList csl = getThemeAttrColorStateList(context, attr);
        if (csl != null && csl.isStateful()) {
            // If the CSL is stateful, we'll assume it has a disabled state and use it
            return csl.getColorForState(DISABLED_STATE_SET, csl.getDefaultColor());
        } else {
            // Else, we'll generate the color using disabledAlpha from the theme
            final TypedValue tv = getTypedValue();
            // Now retrieve the disabledAlpha value from the theme
            context.getTheme().resolveAttribute(android.R.attr.disabledAlpha, tv, true);
            final float disabledAlpha = tv.getFloat();
            return getThemeAttrColor(context, attr, disabledAlpha);
        }
    }
    private static TypedValue getTypedValue() {
        TypedValue typedValue = TL_TYPED_VALUE.get();
        if (typedValue == null) {
            typedValue = new TypedValue();
            TL_TYPED_VALUE.set(typedValue);
        }
        return typedValue;
    }
    static int getThemeAttrColor(Context context, int attr, float alpha) {
        final int color = getThemeAttrColor(context, attr);
        final int originalAlpha = Color.alpha(color);
        return ColorUtils.setAlphaComponent(color, Math.round(originalAlpha * alpha));
    }


    public static int getColorFromAttrRes(int attr, Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[] {attr});
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }

    public static float getFloatFromAttrRes(int attrRes, Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[] {attrRes});
        try {
            return a.getFloat(0, 0);
        } finally {
            a.recycle();
        }
    }

}