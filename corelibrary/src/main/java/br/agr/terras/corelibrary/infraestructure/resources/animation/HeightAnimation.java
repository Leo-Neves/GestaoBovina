package br.agr.terras.corelibrary.infraestructure.resources.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by leo on 09/09/16.
 */
public class HeightAnimation extends Animation {
    private final int startHeight;
    private final int deltaHeight;
    private View content;

    public HeightAnimation(View content, int startHeight, int endHeight) {
        this.startHeight = startHeight;
        deltaHeight = endHeight - startHeight;
        this.content = content;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) content.getLayoutParams();
        lp.height = (int) (startHeight + (deltaHeight * interpolatedTime));
        content.setLayoutParams(lp);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
