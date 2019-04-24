package br.agr.terras.corelibrary.infraestructure.resources.photo;

import android.media.ExifInterface;

import java.io.IOException;

public class GeoTagUtils {

	private static StringBuilder sb = new StringBuilder(20);

	/**
	 * returns ref for latitude which is S or N.
	 * 
	 * @param latitude
	 * @return S or N
	 */
	
	public static void geoTag(String path, double latitude, double longitude) {
		try {
			ExifInterface exif = new ExifInterface(path);
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,
					GeoTagUtils.convert(latitude));
			exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,
					GeoTagUtils.latitudeRef(latitude));
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,
					GeoTagUtils.convert(longitude));
			exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,
					GeoTagUtils.longitudeRef(longitude));
			exif.saveAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static int getOrientationTag(String path){
		int orientation = ExifInterface.ORIENTATION_UNDEFINED; 
		
		try {
			ExifInterface exif = new ExifInterface(path);
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return orientation;
	}
	
	public static void setOrientationTag(String path, int orientation){
		
		try {
			ExifInterface exif = new ExifInterface(path);
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, ""+orientation);
			exif.saveAttributes();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}
	
	public static String latitudeRef(double latitude) {
		return latitude < 0.0d ? "S" : "N";
	}

	/**
	 * returns ref for latitude which is S or N.
	 * 
	 * @param latitude
	 * @return S or N
	 */
	public static String longitudeRef(double longitude) {
		return longitude < 0.0d ? "W" : "E";
	}

	/**
	 * convert latitude into DMS (degree minute second) format. For instance<br/>
	 * -79.948862 becomes<br/>
	 * 79/1,56/1,55903/1000<br/>
	 * It works for latitude and longitude<br/>
	 * 
	 * @param latitude
	 *            could be longitude.
	 * @return
	 */
	synchronized public static final String convert(double latitude) {
		latitude = Math.abs(latitude);
		int degree = (int) latitude;
		latitude *= 60;
		latitude -= (degree * 60.0d);
		int minute = (int) latitude;
		latitude *= 60;
		latitude -= (minute * 60.0d);
		int second = (int) (latitude * 1000.0d);

		sb.setLength(0);
		sb.append(degree);
		sb.append("/1,");
		sb.append(minute);
		sb.append("/1,");
		sb.append(second);
		sb.append("/1000,");
		return sb.toString();
	}
}
