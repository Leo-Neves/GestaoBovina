package br.agr.terras.materialdroid;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by leo on 04/11/16.
 */

public class CircularTextView extends TextView {
    protected final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";
    private Context context;

    public CircularTextView(Context context) {
        super(context);
        init(context, null);
    }

    public CircularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        int id = attrs != null ? attrs.getAttributeResourceValue(ANDROIDXML, "background", -1) : -1;
        if (id == -1)
            id = R.color.mdtp_accent_color;
        int colorBackground = getResources().getColor(id);
        setGravity(Gravity.CENTER);
        //setBackgroundColor(colorBackground);
    }



    public void setBackgroundColor(int color) {
        if (color == -1)
            color = getResources().getColor(R.color.mdtp_accent_color);
        Drawable drawable = getResources().getDrawable(R.drawable.bg_circular_text_view);
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        setBackgroundDrawable(drawable);
    }
}
