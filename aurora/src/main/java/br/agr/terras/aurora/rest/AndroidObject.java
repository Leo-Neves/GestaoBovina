package br.agr.terras.aurora.rest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

public class AndroidObject {

	private String android_serial =android.os.Build.SERIAL;
	private String android_id = android.os.Build.ID;
	private String android_model = android.os.Build.MODEL;
	private String android_version = android.os.Build.VERSION.RELEASE;
	private String android_name_app;
	private String android_imei;
	private String android_version_app;
	
	@SuppressLint("HardwareIds")
	public AndroidObject(Context context){
		try {
			android_version_app = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
			android_name_app = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			android_imei =   ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String getAndroid_id() {
		return android_id;
	}			

	public String getAndroid_model() {	
		return android_model;
	}	
	
	public String getAndroid_version() {
		return android_version;
	}
	
	public String getAndroid_imei() {
		android_imei = "";
		return android_imei;
	}

	public String getAndroid_name_app(){
        return android_name_app;
    }
	
	public String getAndroid_serial() {
		return android_serial;
	}

	public String getAndroid_version_app(){
		return android_version_app;
	}
}
