package br.agr.terras.corelibrary.infraestructure.utils;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.Normalizer;

/**
 * Created by leo on 28/10/16.
 */

public class StringUtils {

    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String limitarCasasDecimais(double numero, int limite) {
        String zeros = "0.";
        for (int i = 0; i < limite; i++)
            zeros += "0";
        if (zeros.length()==2) zeros = "0";
        return new DecimalFormat(zeros).format(numero);
    }

    public static String formatarMonetario(double value) {
        boolean podeInserirPonto = false;
        String digitado = new DecimalFormat("#.00").format(value);
        char[] array = new StringBuilder(digitado.substring(0, digitado.length() - 2)).reverse().toString().toCharArray();
        String numero = "";
        for (int i = 0; i < array.length; i++) {
            char letra = array[i];
            if (isNumero(letra)) {
                numero += letra;
                if (i > 0 && i % 3 == 0 && i + 1 < array.length) {
                    numero += ".";
                }
            }
        }
        numero = new StringBuilder(numero).reverse().toString();
        numero += digitado.substring(digitado.length() - 2, digitado.length());
        Log.i(StringUtils.class.getSimpleName(), "value: "+value);
        Log.i(StringUtils.class.getSimpleName(), digitado);
        Log.i(StringUtils.class.getSimpleName(), numero);
        String valor = numero.replaceFirst("^0+(?!$)", "");
        String texto = "R$ ";
        if (valor.length() == 0) {
            texto += "0,00";
            return texto;
        }
        if (valor.length() == 1) {
            texto += "0,0" + valor;
            return texto;
        }
        if (valor.length() == 2) {
            texto += "0," + valor;
            return texto;
        }
        texto += valor.substring(0, valor.length() - 2) + "," + valor.substring(valor.length() - 2);
        Log.i(StringUtils.class.getSimpleName(), texto);
        return texto;
    }

    private static boolean isNumero(char letra) {
        if (letra == '1' || letra == '2' || letra == '3' || letra == '4' || letra == '5' || letra == '6' || letra == '7' || letra == '8' || letra == '9' || letra == '0')
            return true;
        return false;
    }

    public static String getFirstWord(String text) {
        if (text.indexOf(' ') > -1) { // Check if there is more than one word.
            return text.substring(0, text.indexOf(' ')); // Extract first word.
        } else {
            return text; // Text is the first word itself.
        }
    }

    public static int nthIndexOf(String text, char needle, int n) {
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == needle) {
                n--;
                if (n == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static String join(final Object[] array, final char separator) {
        if (array == null) {
            return null;
        }
        int startIndex = 0;
        int endIndex = array.length;
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }
}
