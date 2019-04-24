package br.agr.terras.corelibrary.infraestructure.resources;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by leandro on 21/07/15.
 */
public class TypefaceCustomFont {
    public static Typeface getRobotoTypeface(Context context){
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
    }
}
