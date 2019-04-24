package br.agr.terras.materialdroid.childs.tourguide;

import android.graphics.Color;
import android.view.Gravity;

/**
 * Created by leo on 27/09/16.
 */
public class Ponteiro {
    public final int gravity;
    public final int color;

    public Ponteiro() {
        this(Gravity.CENTER, Color.WHITE);
    }

    public Ponteiro(int gravity, int color) {
        this.gravity = gravity;
        this.color = color;
    }

}
