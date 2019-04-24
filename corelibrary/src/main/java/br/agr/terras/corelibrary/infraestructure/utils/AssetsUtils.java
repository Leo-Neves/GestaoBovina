package br.agr.terras.corelibrary.infraestructure.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.agr.terras.corelibrary.infraestructure.CORE;

/**
 * Created by leo on 11/05/16.
 */
public class AssetsUtils {

    public static String stringFromAssets(String fileName) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = CORE.getResources().getAssets()
                    .open(fileName, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    public static Typeface typefaceFromAssets(String font){
        return Typeface.createFromAsset(CORE.getContext().getAssets(),String.format("fonts/%s.ttf", font));

    }
}
