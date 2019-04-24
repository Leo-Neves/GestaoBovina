package br.agr.terras.materialdroid.utils.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by leo on 28/09/16.
 */
public class BreadcrumbDrawable extends Drawable {

    private Paint paint;
    private Path path;
    private int color;
    private Rect bounds;
    private Canvas canvas;
    private int totalChildreen;

    public BreadcrumbDrawable(int totalChildreen) {
        this.totalChildreen = totalChildreen;
        configurePath();
    }

    private void configurePath() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        path = new Path();
    }

    @Override
    public void draw(Canvas canvas) {
        this.canvas = canvas;
        updatePath();
    }


    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    private void updatePath() {
        Log.i(getClass().getSimpleName(),"top: "+bounds.top+"\nleft: "+bounds.left+"\nright: "+bounds.right+"\nbottom: "+bounds.bottom);
        int h = Math.abs(bounds.bottom - bounds.top);
        int w = Math.abs(bounds.left - bounds.right);
        int h1 = (int)((h*0.45));
        int h2 = (int)((h*0.55));
        int l1 = (int)(w/(totalChildreen*2));
        int l2 = (int)((w/(totalChildreen*2))*((totalChildreen*2)-1));
        Log.i(getClass().getSimpleName(),"h1: "+h1+"\nh2: "+h2);
        Log.i(getClass().getSimpleName(),"l1: "+l1+"\nl2: "+l2);
        paint.setColor(color);
        path.reset();
        path.addRect(l1, h1,l2,h2, Path.Direction.CW);
        path.close();
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.bounds = bounds;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        invalidateSelf();
    }
}
