package br.agr.terras.materialdroid.utils;

import java.util.Random;

/**
 * Created by leo on 18/09/15.
 */
public class RandomString {
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String randomString( int len ){
        Random random = new Random();
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( random.nextInt(AB.length()) ) );
        return sb.toString();
    }
}
