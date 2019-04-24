package br.agr.terras.materialdroid.childs.tourguide;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created by leo on 27/09/16.
 */
public class Sobreposicao {
    public int mBackgroundColor;
    public boolean mDisableClick;
    public Style mStyle;
    public Animation mEnterAnimation, mExitAnimation;
    public View.OnClickListener mOnClickListener;
    public int mPadding;

    public enum Style {
        Circulo, Retangulo, Nenhum
    }
    public Sobreposicao() {
        this(true, Color.parseColor("#55000000"), Style.Nenhum);
    }

    public Sobreposicao(boolean disableClick, int backgroundColor, Style style) {
        mDisableClick = disableClick;
        mBackgroundColor = backgroundColor;
        mStyle = style;
    }

    /**
     * Set background color
     * @param backgroundColor
     * @return return TextoPopup instance for chaining purpose
     */
    public Sobreposicao setBackgroundColor(int backgroundColor){
        mBackgroundColor = backgroundColor;
        return this;
    }

    /**
     * Set to true if you want to block all user input to pass through this overlay, set to false if you want to allow user input under the overlay
     * @param yes_no
     * @return return Sobreposicao instance for chaining purpose
     */
    public Sobreposicao disableClick(boolean yes_no){
        mDisableClick = yes_no;
        return this;
    }

    public Sobreposicao setStyle(Style style){
        mStyle = style;
        return this;
    }

    public Sobreposicao setPadding(int padding){
        mPadding = padding;
        return this;
    }

    /**
     * Set enter animation
     * @param enterAnimation
     * @return return Sobreposicao instance for chaining purpose
     */
    public Sobreposicao setEnterAnimation(Animation enterAnimation){
        mEnterAnimation = enterAnimation;
        return this;
    }
    /**
     * Set exit animation
     * @param exitAnimation
     * @return return Sobreposicao instance for chaining purpose
     */
    public Sobreposicao setExitAnimation(Animation exitAnimation){
        mExitAnimation = exitAnimation;
        return this;
    }

    /**
     * Set onClickListener for the Sobreposicao
     * @param onClickListener
     * @return return Sobreposicao instance for chaining purpose
     */
    public Sobreposicao setOnClickListener(View.OnClickListener onClickListener){
        mOnClickListener=onClickListener;
        return this;
    }
}
