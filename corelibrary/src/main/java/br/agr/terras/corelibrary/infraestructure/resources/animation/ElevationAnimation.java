package br.agr.terras.corelibrary.infraestructure.resources.animation;

import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import br.agr.terras.corelibrary.infraestructure.utils.VersionUtils;
import br.agr.terras.materialdroid.CardView;

/**
 * Created by leo on 03/10/16.
 */
public class ElevationAnimation extends Animation {
    private final float mStartElevation;
    private final float mDeltaElevation;
    private View content;

    public ElevationAnimation(View content, float startElevation, float endElevation) {
        mStartElevation = startElevation;
        mDeltaElevation = endElevation - startElevation;
        this.content = content;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float elevation = (mStartElevation + (mDeltaElevation * interpolatedTime));
        if (content instanceof CardView)
            ((CardView)content).setCardElevation(elevation);
        else if (VersionUtils.getSdkVersion() >= Build.VERSION_CODES.LOLLIPOP){
            content.setElevation(elevation);
        }
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }



}
