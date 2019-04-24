package br.agr.terras.corelibrary.infraestructure.utils;

import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;

import br.agr.terras.corelibrary.infraestructure.CORE;

/**
 * Created by leo on 07/06/16.
 */
public class DimensionUtils {
    public static final int MATCH_PARENT = LinearLayout.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;

    public static int convertDpToPx(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, CORE.getResources().getDisplayMetrics());
    }

    public static int getScreenWidth(Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.y;
    }

    public static int convertToSystemDensity(int dimen, Dimension dimension){
        return (int)(dimension.getResized(dimen) * CORE.getResources().getDisplayMetrics().density);
    }

    public static int convertToSystemDensity(int dimen, int dimension){
        int width = CORE.getResources().getDisplayMetrics().widthPixels;
        int height = CORE.getResources().getDisplayMetrics().heightPixels;
        double x = Math.pow(width/(CORE.getResources().getDisplayMetrics().density * 160),2);
        double y = Math.pow(height/(CORE.getResources().getDisplayMetrics().density * 160),2);
        double inches = Math.sqrt(x+y);
        double denstity = Math.sqrt((width*width) + (height*height)) / inches;
        Log.i(DimensionUtils.class.getSimpleName(), String.format(MyDateUtils.getLocaleBR(), "w: %d\th: %d\tinched: %f\tdensity: %f",width, height, inches, denstity));
        return (int)(denstity * dimen)/dimension;
    }

    public enum Dimension{
        LDPI(0.75F),
        MDPI(1),
        HDPI(0.66F),
        XHDPI(0.5F),
        XXHDPI(0.33F);

        float density;
        Dimension(float density){
            this.density = density;
        }

        public int getResized(int dimen){
            return (int)(dimen*density);
        }
    }


}
