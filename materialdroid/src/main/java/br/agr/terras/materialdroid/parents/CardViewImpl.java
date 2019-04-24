package br.agr.terras.materialdroid.parents;

import android.content.Context;

import br.agr.terras.materialdroid.utils.cardview.CardViewDelegate;

/**
 * Created by leo on 03/10/16.
 */
public interface CardViewImpl {
    void initialize(CardViewDelegate cardView, Context context, int backgroundColor, float radius,
                    float elevation, float maxElevation);

    void setRadius(CardViewDelegate cardView, float radius);

    float getRadius(CardViewDelegate cardView);

    void setElevation(CardViewDelegate cardView, float elevation);

    float getElevation(CardViewDelegate cardView);

    void initStatic();

    void setMaxElevation(CardViewDelegate cardView, float maxElevation);

    float getMaxElevation(CardViewDelegate cardView);

    float getMinWidth(CardViewDelegate cardView);

    float getMinHeight(CardViewDelegate cardView);

    void updatePadding(CardViewDelegate cardView);

    void onCompatPaddingChanged(CardViewDelegate cardView);

    void onPreventCornerOverlapChanged(CardViewDelegate cardView);

    void setBackgroundColor(CardViewDelegate cardView, int color);
    
    void setColorShadow(CardViewDelegate cardView, int colorEnd);

    void setAlphaShadow(CardViewDelegate cardView, int alphaStart, int alphaEnd);
}

