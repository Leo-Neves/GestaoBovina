package br.agr.terras.corelibrary.infraestructure.utils;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.CORE;

/**
 * Created by leo on 24/05/16.
 */
public class ColorUtils {
    private static int colorDefault = -1;

    public static int getById(int resId){
        return CORE.getResources().getColor(resId);
    }

    public static int getColorByName(String colorName){
        String[] colorsNames = CORE.getResources().getStringArray(R.array.color_detector_nomes);
        TypedArray ta = CORE.getResources().obtainTypedArray(R.array.color_detector);
        for (int i=0;i<colorsNames.length;i++){
            if (colorName.toUpperCase().contains(colorsNames[i].toUpperCase()))
                return ta.getColor(i,0);
        }
        ta.recycle();
        return getColorDefault();
    }

    public static void setColorDefault(int colorDefault){
        ColorUtils.colorDefault = colorDefault;
    }

    private static int getColorDefault(){
        if (colorDefault==-1)
            colorDefault = CORE.getResources().getColor(R.color.blue_ecotrack);
        return colorDefault;
    }

    public static ColorStateList colorToStateList(int color){
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };
        int[] colors = new int[]{
                color, color, color, color
        };
        return new ColorStateList(states, colors);
    }

    public static void setColorEdge(int color){
        if (VersionUtils.getSdkVersion() < Build.VERSION_CODES.LOLLIPOP){
            int glowDrawableId = CORE.getResources().getIdentifier("overscroll_glow", "drawable", "android");
            int edgeDrawableId = CORE.getResources().getIdentifier("overscroll_edge", "drawable", "android");
            Drawable androidGlow = CORE.getResources().getDrawable(glowDrawableId);

            Drawable androidEdge = CORE.getResources().getDrawable(edgeDrawableId);
            androidGlow.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            androidEdge.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }
}
