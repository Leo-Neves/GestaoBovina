package br.agr.terras.corelibrary.infraestructure.resources.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by edson on 28/03/16.
 */
public class FadeAnimation extends Animation{
    public static final int FADE_IN = 33;
    public static final int FADE_OUT = 99;
    private int modo;
    private View content;

    public FadeAnimation(View content, int modo) {
        this.content = content;
        this.modo = modo;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(modo == FADE_IN){
            content.setAlpha(interpolatedTime);
        }
        if(modo == FADE_OUT){
            content.setAlpha(1 - interpolatedTime);
        }

    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

    

}
