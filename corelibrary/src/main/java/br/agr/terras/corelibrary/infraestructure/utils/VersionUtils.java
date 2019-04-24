package br.agr.terras.corelibrary.infraestructure.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import br.agr.terras.corelibrary.infraestructure.CORE;

/**
 * Created by leo on 16/05/16.
 */
public class VersionUtils {

    public static boolean isModeDebug(){
         return (CORE.getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static String getVersionName(){
        PackageInfo info = null;
        try {
            info = CORE.getContext().getPackageManager().getPackageInfo(
                    CORE.getContext().getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info.versionName;
    }

    public static String getApplicationName( ){
        return CORE.getString(CORE.getContext().getApplicationInfo().labelRes);
    }

    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
}
