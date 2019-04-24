package br.agr.terras.corelibrary.infraestructure.resources.photo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.util.Log;
import android.view.View;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.IOException;

public class ModifyPhoto {
	private static String TAG = "ModifyPhoto";

	public static void modify(Activity activity, String path, Location location) {
		int orientation = GeoTagUtils.getOrientationTag(path);
		reduzirImagem(path);
		GeoTagUtils.geoTag(path, location.getLatitude(),
				location.getLongitude());
		GeoTagUtils.setOrientationTag(path, orientation);
		PhotoUtils.refreshGallery(activity);
	}
	
	public static void modify(Activity activity, String path) {
		int orientation = GeoTagUtils.getOrientationTag(path);
		reduzirImagem(path);
		GeoTagUtils.setOrientationTag(path, orientation);
		PhotoUtils.refreshGallery(activity);
	}
	
	
	
	public static void reduzirImagem(String path) {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		bitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, false);
		PhotoUtils.saveBitmapIntoFile(bitmap, new File(path));
	}
	
	public static Bitmap rotateImage(Activity activity,String filePath){
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
	    //Bitmap resultBitmap = bitmap;

	    try
	    {
	        ExifInterface exifInterface = new ExifInterface(filePath);
	        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

	        Matrix matrix = new Matrix();

	        if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
	        {
	            matrix.postRotate(90F);
	            Log.i("ROTATE", ""+90F);
	        }
	        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
	        {
	            matrix.postRotate(180F);
	            Log.i("ROTATE", ""+180F);
	        }
	        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
	        {
	            matrix.postRotate(270F);
	            Log.i("ROTATE", ""+270F);
	        }
	        
	        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(),true);
	        
	        // Rotate the bitmap
	        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	        
	        PhotoUtils.saveBitmapIntoFile(bitmap, new File(filePath));
	    }
	    catch (Exception exception)
	    {
	    	exception.getMessage();
	    	
	    }
	    
	    PhotoUtils.refreshGallery(activity);
	    
	    
	    return bitmap;
	}
	
	public static void genareteThumbnail(Activity activity, String imagePath, String imagePathThumbnail ){
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
		String[] arrayString = imagePath.split("/");
		String nameImageThumbnail = arrayString[arrayString.length - 1];
		
		
		
		try {
			PhotoUtils.saveExternalThumbnail(activity, imagePathThumbnail, nameImageThumbnail, bitmap, 96, 96);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		PhotoUtils.refreshGallery(activity);
		
	}

	public static void resizeImage(Context context, String path, double megaPixel){
		Log.i(TAG, "Start scalling image to "+megaPixel+"MP");
		megaPixel*=1000000;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inJustDecodeBounds = true;
		final File imageFile = new File(path/*.substring(0,path.length()-2)+"g"*/);
		//originalFile.renameTo(imageFile);
		String value=null;
		long filesize=getFolderSize(imageFile)/1024;//call function and convert bytes into Kb
		if(filesize>=1024)
			value=(filesize/1024.0)+" MB";
		else
			value=filesize+" Kb";
		Log.i(TAG,"Original image size: "+value);
		BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bitmapOptions);
		int imageHeight = bitmapOptions.outHeight;
		int imageWidth = bitmapOptions.outWidth;
		Log.i(TAG, "Image original width: "+imageWidth);
		Log.i(TAG, "Image original height: "+imageHeight);
		double imageMP = imageWidth*imageHeight;
		Log.i(TAG, "Image original MP: "+imageMP/1000000.0);
		double escalar = imageMP/megaPixel;
		if (escalar>1) {
			Log.i(TAG, "Scale factor: "+escalar);
			escalar = (1/Math.sqrt(escalar));
		}
		else{
			Log.e(TAG, "Scale factor: "+escalar+" cannot resize. Scale changed to 1");
			escalar = 1;
		}
		ImageLoader imageLoader = ImageLoader.getInstance();
		ImageSize targetSize = new ImageSize(imageWidth, imageHeight).scale((float)escalar); // result Bitmap will be fit to this size
		Log.i(TAG, "Image modified width: "+targetSize.getWidth());
		Log.i(TAG, "Image modified height: "+targetSize.getHeight());
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheOnDisk(true)
				.cacheInMemory(true)
				.considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.build();
		ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(context)
				.threadPoolSize(1)
				.memoryCache(new WeakMemoryCache())
				.build();
		imageLoader.init(imageLoaderConfiguration);
		Log.i(TAG,"Path to Universal Image Loader: file://"+path);
		ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap bmp) {
				PhotoUtils.saveBitmapIntoFile(bmp, imageFile);
				Long filesize=getFolderSize(imageFile)/1024;//call function and convert bytes into Kb
				String value;
				if(filesize>=1024)
					value=filesize/1024.0+" Mb";
				else
					value=filesize+" Kb";
				Log.i(TAG,"Modified image size: "+value);
				Log.i(TAG, "End scalling image");
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {

			}
		};
		imageLoader.loadImage("file://"+path, targetSize, options, imageLoadingListener);

	}

	private static long getFolderSize(File f) {
		long size = 0;
		if (f.isDirectory()) {
			for (File file : f.listFiles()) {
				size += getFolderSize(file);
			}
		} else {
			size=f.length();
		}
		return size;
	}


}

