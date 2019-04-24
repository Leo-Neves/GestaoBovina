package br.agr.terras.materialdroid.childs.tourguide;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;

/**
 * Created by leo on 27/09/16.
 */
public class TextoPopup {
    public String mTitle, mDescription;
    public int mBackgroundColor, mTextColor;
    public Animation mEnterAnimation, mExitAnimation;
    public boolean mShadow;
    public int mGravity;
    public View.OnClickListener mOnClickListener;
    public int width = -1;
    public int height = -1;
    public DimensionUnit dimensionUnitWidth;
    public DimensionUnit dimensionUnitHeight;

    public enum DimensionUnit{
        PERCENT, DP;
    }

    public TextoPopup(){
        /* default values */
        mTitle = "";
        mDescription = "";
        mBackgroundColor = Color.parseColor("#3498db");
        mTextColor = Color.parseColor("#FFFFFF");

        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(1000);
        mEnterAnimation.setFillAfter(true);
        mEnterAnimation.setInterpolator(new BounceInterpolator());
        mShadow = true;

        // TODO: exit animation
        mGravity = Gravity.CENTER;
    }
    /**
     * Set title text
     * @param title
     * @return return TextoPopup instance for chaining purpose
     */
    public TextoPopup setTitulo(String title){
        mTitle = title;
        return this;
    }

    /**
     * Set description text
     * @param description
     * @return return TextoPopup instance for chaining purpose
     */
    public TextoPopup setDescricao(String description){
        mDescription = description;
        return this;
    }

    /**
     * Set background color
     * @param backgroundColor
     * @return return TextoPopup instance for chaining purpose
     */
    public TextoPopup setBackgroundColor(int backgroundColor){
        mBackgroundColor = backgroundColor;
        return this;
    }

    /**
     * Set text color
     * @param textColor
     * @return return TextoPopup instance for chaining purpose
     */
    public TextoPopup setTextColor(int textColor){
        mTextColor = textColor;
        return this;
    }

    /**
     * Set enter animation
     * @param enterAnimation
     * @return return TextoPopup instance for chaining purpose
     */
    public TextoPopup setEnterAnimation(Animation enterAnimation){
        mEnterAnimation = enterAnimation;
        return this;
    }
    /**
     * Set exit animation
     * @param exitAnimation
     * @return return TextoPopup instance for chaining purpose
     */
//    TODO:
//    public TextoPopup setExitAnimation(Animation exitAnimation){
//        mExitAnimation = exitAnimation;
//        return this;
//    }
    /**
     * Set the gravity, the setGravity is centered relative to the targeted button
     * @param gravity Gravity.CENTER, Gravity.TOP, Gravity.BOTTOM, etc
     * @return return TextoPopup instance for chaining purpose
     */
    public TextoPopup setGravity(int gravity){
        mGravity = gravity;
        return this;
    }
    /**
     * Set if you want to have setShadow
     * @param shadow
     * @return return TextoPopup instance for chaining purpose
     */
    public TextoPopup setShadow(boolean shadow){
        mShadow = shadow;
        return this;
    }

    public TextoPopup setWidth(int width, DimensionUnit dimensionUnit) {
        this.width = width;
        this.dimensionUnitWidth = dimensionUnit;
        return this;
    }

    public TextoPopup setHeight(int height, DimensionUnit dimensionUnit) {
        this.height = height;
        this.dimensionUnitHeight = dimensionUnit;
        return this;
    }

    public TextoPopup setOnClickListener(View.OnClickListener onClickListener){
        mOnClickListener = onClickListener;
        return this;
    }
}
