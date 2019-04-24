package br.agr.terras.corelibrary.infraestructure.resources.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoUtils {
	public static final Bitmap.CompressFormat TYPE_IMAGE = Bitmap.CompressFormat.JPEG;
	public static final int QUALITY = 100;
	
	
	public static File saveBitmapIntoInternal(Context context,Bitmap bitmap){
		String nomeImagem = NamePhotoGenerator.generateName(TYPE_IMAGE);
		File fileImage = StorageUtils.getOutputInternalFile(context,nomeImagem);
		saveBitmapIntoFile(bitmap,fileImage);
		return fileImage;
	}
	
	public static File saveBitmapIntoExternal(Context context,Bitmap bitmap,String directory){
		String nomeImagem = NamePhotoGenerator.generateName(TYPE_IMAGE);
		File fileImage = StorageUtils.getOutputExternalFile(directory, nomeImagem);
		saveBitmapIntoFile(bitmap,fileImage);
		refreshGallery(context);
		return fileImage;
	}
	
	public static File createNewExternalImageFile(String directory){
		String nomeImagem = NamePhotoGenerator.generateName(TYPE_IMAGE);
		File fileImage = StorageUtils.getOutputExternalFile(directory, nomeImagem);
		return fileImage;
	}
	
	public static File createNewCacheImageFile(Context context){
		String fileName = NamePhotoGenerator.generateName(TYPE_IMAGE);
		return new File(context.getCacheDir(), fileName);
	}
	
	public static File createNewInternalImageFile(Context context){
		String fileName = NamePhotoGenerator.generateName(TYPE_IMAGE);
		return new File(context.getFilesDir(), fileName);
	}
	
	public static File saveBitmapIntoCache(Context context,Bitmap bitmap){
		String nomeImagem = NamePhotoGenerator.generateName(TYPE_IMAGE);
		File fileImage = StorageUtils.getOutputCacheFile(context,nomeImagem);
		saveBitmapIntoFile(bitmap,fileImage);
		return fileImage;
	}
	
	
	public static Bitmap createBitmapFromBytes(byte[] fotoDataStream) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPurgeable = true;
		opts.inTempStorage = new byte[8 * 1024];
		opts.inSampleSize = 6;
		opts.inInputShareable = true;

		return BitmapFactory.decodeByteArray(fotoDataStream, 0,
				fotoDataStream.length);
	}
	
	public static void saveBitmapIntoFile(Bitmap bitmap,File file){
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(TYPE_IMAGE, QUALITY, fos);
			fos.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
		}
	}
	
	private static int calcDpToPx(float dp, int dpi) {
		return (int) (dp * (dpi / 160));
	}

	private static int calcDpToPx(Context context,float dp) {
	 float density = context.getResources().getDisplayMetrics().density;
	    return Math.round((float)dp * density);
	}
	
	
	
	public static File saveThumbnail(Context context,Bitmap bitmap,int width_dp, int height_dp) throws IOException{
		int ldpi = 120;
		int mdpi = 160;
		int hdpi = 240;
		int xhdpi = 320;
		int xxhdpi = 480;
		
		int width_px = 0;
		int height_px = 0;
		String nameImage = NamePhotoGenerator.generateNameThumbnail(TYPE_IMAGE);
		
		width_px = calcDpToPx(context,width_dp);
		height_px = calcDpToPx(context,height_dp);
		
		/*
		switch (context.getResources().getDisplayMetrics().densityDpi) {

		case DisplayMetrics.DENSITY_LOW:
			width = calcDpToPx(width_dp, ldpi);
			height = calcDpToPx(height_dp, ldpi);

			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			width = calcDpToPx(width_dp, mdpi);
			height = calcDpToPx(height_dp, mdpi);
			break;
		case DisplayMetrics.DENSITY_HIGH:
			width = calcDpToPx(width_dp, hdpi);
			height = calcDpToPx(height_dp, hdpi);
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			width = calcDpToPx(width_dp, xhdpi);
			height = calcDpToPx(height_dp, xhdpi);
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			width = calcDpToPx(width_dp, xxhdpi);
			height = calcDpToPx(height_dp, xxhdpi);
			break;

		}
		*/

		Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
				bitmap, width_px, height_px);
		
		File fileImage = StorageUtils.getOutputInternalFile(context,nameImage);
		saveBitmapIntoFile(thumbImage,fileImage);
		return fileImage;
	}
	
	public static void saveExternalThumbnail(Context context, String path, String nameImage,Bitmap bitmap,int width_dp, int height_dp) throws IOException{
		int ldpi = 120;
		int mdpi = 160;
		int hdpi = 240;
		int xhdpi = 320;
		int xxhdpi = 480;
		
		int width_px = 0;
		int height_px = 0;
		
		width_px = calcDpToPx(context,width_dp);
		height_px = calcDpToPx(context,height_dp);
		
		/*
		switch (context.getResources().getDisplayMetrics().densityDpi) {

		case DisplayMetrics.DENSITY_LOW:
			width = calcDpToPx(width_dp, ldpi);
			height = calcDpToPx(height_dp, ldpi);

			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			width = calcDpToPx(width_dp, mdpi);
			height = calcDpToPx(height_dp, mdpi);
			break;
		case DisplayMetrics.DENSITY_HIGH:
			width = calcDpToPx(width_dp, hdpi);
			height = calcDpToPx(height_dp, hdpi);
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			width = calcDpToPx(width_dp, xhdpi);
			height = calcDpToPx(height_dp, xhdpi);
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			width = calcDpToPx(width_dp, xxhdpi);
			height = calcDpToPx(height_dp, xxhdpi);
			break;

		}
		*/

		Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
				bitmap, width_px, height_px);
		
		File fileImage = new File(path, nameImage);
		saveBitmapIntoFile(thumbImage,fileImage);
		
	}
	
	/** 
	 * Atualiza a galeria de foto.
	 * @param context
	 */
	public static void refreshGallery(Context context) {
		
		/*
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, 
			     Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		*/
		
		context.sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
						+ Environment.getExternalStorageDirectory())));
		
		/*
		MediaScannerConnection.scanFile(context, new String[] { Environment.getExternalStorageDirectory().toString() }, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) 
              {
                  Log.i("ExternalStorage", "Scanned " + path + ":");
                  Log.i("ExternalStorage", "-> uri=" + uri);
              }
            });
        */
	}

}
