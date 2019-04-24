package br.agr.terras.materialdroid.utils.cardview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import br.agr.terras.materialdroid.utils.drawable.RoundRectDrawableWithShadow;

/**
 * Created by leo on 03/10/16.
 */
public class CardViewJellybeanMr1 extends CardViewEclairMr1 {

    @Override
    public void initStatic() {
        RoundRectDrawableWithShadow.sRoundRectHelper
                = new RoundRectDrawableWithShadow.RoundRectHelper() {
            @Override
            public void drawRoundRect(Canvas canvas, RectF bounds, float cornerRadius,
                                      Paint paint) {
                canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, paint);
            }
        };
    }
}
