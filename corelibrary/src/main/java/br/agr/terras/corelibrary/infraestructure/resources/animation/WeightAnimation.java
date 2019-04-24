package br.agr.terras.corelibrary.infraestructure.resources.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by edson on 28/03/16.
 */
public class WeightAnimation extends Animation{
    private final float mStartWeight;
    private final float mDeltaWeight;
    private View content;

    public WeightAnimation(View content, float startWeight, float endWeight) {
        mStartWeight = startWeight;
        mDeltaWeight = endWeight - startWeight;
        this.content = content;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) content.getLayoutParams();
        lp.weight = (mStartWeight + (mDeltaWeight * interpolatedTime));
        content.setLayoutParams(lp);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }



}
