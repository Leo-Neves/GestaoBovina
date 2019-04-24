package br.agr.terras.corelibrary.infraestructure.resources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Service {
    public static String generateHashMD5(byte[] data) {
        MessageDigest m = null;

        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        m.update(data, 0, data.length);
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }

    public static String getMD5_Hash(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(ImagemService.TYPE_IMAGE, ImagemService.QUALITY, baos);
        byte[] bitmapBytes = baos.toByteArray();
        return generateHashMD5(bitmapBytes);
    }

    public static String getMD5_Hash(String pathName) {
        Bitmap bitmap = BitmapFactory.decodeFile(pathName);
        return getMD5_Hash(bitmap);
    }

    public static String getMD5_Hash(Context context, int drawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
        return getMD5_Hash(bitmap);

    }

    public static String getMD5(String texto) {
        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
            m.update(texto.getBytes(), 0, texto.length());
            BigInteger i = new BigInteger(1, m.digest());
            texto = String.format("%1$032X", i);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return texto;
    }
}
