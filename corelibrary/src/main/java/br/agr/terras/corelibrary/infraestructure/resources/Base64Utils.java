package br.agr.terras.corelibrary.infraestructure.resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Base64Utils {

	public static StringBuffer toBase64(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(ImagemService.TYPE_IMAGE, ImagemService.QUALITY, stream);
		byte[] bytes = stream.toByteArray();
		StringBuffer sb = new StringBuffer(Base64.encodeToString(bytes, Base64.NO_WRAP));
		return sb;
	}

	public static StringBuffer toBase64(byte[] bytes) {
		StringBuffer sb = new StringBuffer(Base64.encodeToString(bytes,
				Base64.NO_WRAP));

		return sb;
	}

	public static Bitmap fromBase64(String base64){
		byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

}
