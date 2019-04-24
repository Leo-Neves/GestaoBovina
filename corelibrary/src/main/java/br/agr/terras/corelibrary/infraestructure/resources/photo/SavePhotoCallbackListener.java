package br.agr.terras.corelibrary.infraestructure.resources.photo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.location.Location;

public interface SavePhotoCallbackListener {

	
	public Location getLocationGPS();
	
	public Activity getAtivity();
	/** This method will execute when photo is processed com geotag embedded and logo
	 * @param absolutePath Path absolte where photo is placed 
	 * @param bitmap photo as a bitmap
	 */
	public void onPostExecutePhoto(String absolutePath, Bitmap bitmap, String md5sum, String namePhoto, Location location);
	
}
