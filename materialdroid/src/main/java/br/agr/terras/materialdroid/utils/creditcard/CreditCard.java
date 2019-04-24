package br.agr.terras.materialdroid.utils.creditcard;

/**
 * Created by leo on 12/09/16.
 */

import android.graphics.drawable.Drawable;

public class CreditCard {
    private String mRegexPattern;
    private Drawable mDrawable;
    private String mType;

    public CreditCard(String regexPattern, Drawable drawable, String type) {
        if (regexPattern == null || drawable == null || type == null) {
            throw new IllegalArgumentException();
        }
        mRegexPattern = regexPattern;
        mDrawable = drawable;
        mType = type;
    }

    public String getRegexPattern() {
        return mRegexPattern;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public String getType() {
        return mType;
    }
}