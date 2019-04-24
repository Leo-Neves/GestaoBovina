package br.agr.terras.corelibrary.infraestructure.resources.strings;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by leo on 01/12/15.
 */
public class RoundDecimals {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
