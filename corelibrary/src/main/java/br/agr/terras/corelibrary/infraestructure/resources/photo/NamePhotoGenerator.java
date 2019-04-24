package br.agr.terras.corelibrary.infraestructure.resources.photo;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NamePhotoGenerator {
	public static String generateName(Bitmap.CompressFormat TYPE_IMAGE) {

		String nomeImagemGerado = "IMG_" + getTimeStampString() + getExtension(TYPE_IMAGE);
		return nomeImagemGerado;
	}
	
	public static String generateNameThumbnail(Bitmap.CompressFormat TYPE_IMAGE) {
		
		String nomeImagemGerado = "IMG_" + getTimeStampString() + "_thumb"+getExtension(TYPE_IMAGE);
		return nomeImagemGerado;
	}
	
	@SuppressLint("SimpleDateFormat")
	private static String getTimeStampString(){
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
		.format(new Date());
		return timeStamp;
	}
	
	private static String getExtension(Bitmap.CompressFormat TYPE_IMAGE){
		if(TYPE_IMAGE == Bitmap.CompressFormat.JPEG){
			return ".jpeg";
		}else if(TYPE_IMAGE == Bitmap.CompressFormat.PNG){
			return ".png";
		}
		return ".png";
	}
}
