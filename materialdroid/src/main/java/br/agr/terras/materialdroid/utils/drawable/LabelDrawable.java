package br.agr.terras.materialdroid.utils.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by leo on 28/09/16.
 */
public class LabelDrawable extends Drawable {


    public enum Sides {OPEN, CLOSED}

    ;

    private Paint paintExternal, paintInternal;
    private Path pathExternal, pathInternal;
    private Sides start = Sides.CLOSED;
    private Sides end = Sides.OPEN;
    private int colorExternal, colorInternal;
    private Rect bounds;
    private Canvas canvas;


    public LabelDrawable() {
        configurePathExternal();
        configurePathInternal();
    }

    private void configurePathExternal() {
        paintExternal = new Paint();
        paintExternal.setAntiAlias(true);
        paintExternal.setStyle(Paint.Style.FILL);
        paintExternal.setColor(Color.RED);
        pathExternal = new Path();
    }

    private void configurePathInternal() {
        paintInternal = new Paint();
        paintInternal.setAntiAlias(true);
        paintInternal.setStyle(Paint.Style.FILL);
        paintInternal.setColor(Color.RED);
        pathInternal = new Path();
    }

    @Override
    public void draw(Canvas canvas) {
        //canvas.drawPath(pathExternal, paintExternal);
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
        int h = Math.abs(bounds.bottom - bounds.top);
        int w = Math.abs(bounds.left - bounds.right);
        paintExternal.setColor(colorExternal);
        paintInternal.setColor(colorInternal);
        pathExternal.reset();
        paintInternal.reset();
        pathExternal.moveTo(bounds.left + w / 2, bounds.top + h / 2);
        pathInternal.moveTo(bounds.left + w / 2, bounds.top + h / 2);

        /*if (end==Sides.OPEN) {
            pathExternal.lineTo(bounds.right - h / 2, 0);
            pathExternal.lineTo(bounds.right, bounds.top + h / 2);
            pathExternal.lineTo(bounds.right - h / 2, bounds.bottom);
        }else{
            pathExternal.lineTo(bounds.right,bounds.top);
            pathExternal.lineTo(bounds.right, bounds.bottom);
        }
        pathExternal.lineTo(bounds.left,bounds.bottom);
        if (start==Sides.OPEN) {
            pathExternal.lineTo(bounds.left + h/2, bounds.top+h/2);
        }*/
        /*pathExternal.addCircle(bounds.left+w/2, bounds.top+h/2, h/2, Path.Direction.CW);
        pathInternal.addCircle(bounds.left+w/2, bounds.top+h/2, (0.89F)*(h/2), Path.Direction.CW);
        pathExternal.close();
        pathInternal.close();*/
        int saveColor = paintExternal.getColor();
        paintExternal.setColor(colorInternal);
        Paint.Style saveStyle = paintExternal.getStyle();
        paintExternal.setStyle(Paint.Style.FILL);
        canvas.drawCircle(bounds.left + w / 2, bounds.top + h / 2, (h / 2), paintExternal);
        paintExternal.setColor(colorExternal);
        paintExternal.setStyle(Paint.Style.STROKE);
        float saveStrokeWidth = paintExternal.getStrokeWidth();
        paintExternal.setStrokeWidth(4);
        canvas.drawCircle(bounds.left + w / 2, bounds.top + h / 2, (h / 2) - (4 / 2), paintExternal);
        paintExternal.setStrokeWidth(saveStrokeWidth);
        paintExternal.setColor(saveColor);
        paintExternal.setStyle(saveStyle);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.bounds = bounds;
    }

    public Sides getStart() {
        return start;
    }

    public void setStart(Sides start) {
        this.start = start;
        this.invalidateSelf();

    }

    public Sides getEnd() {
        return end;
    }

    public void setEnd(Sides end) {
        this.end = end;
        this.invalidateSelf();
    }


    public int getColorExternal() {
        return colorExternal;
    }

    public void setColorExternal(int color) {
        this.colorExternal = color;
        invalidateSelf();
    }

    public void setColorInternal(int color) {
        this.colorInternal = color;
        invalidateSelf();
    }
}
