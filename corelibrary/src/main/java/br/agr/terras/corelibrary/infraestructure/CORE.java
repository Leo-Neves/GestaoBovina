package br.agr.terras.corelibrary.infraestructure;

import android.content.Context;
import android.content.res.Resources;

import br.agr.terras.aurora.log.Logger;
import br.agr.terras.corelibrary.infraestructure.utils.VersionUtils;

/**
 * Created by leo on 07/06/16.
 */
public class CORE {
    private static Context context;

    public static void init(Context context) {
        CORE.context = context;
        Logger.init(VersionUtils.getApplicationName());
    }

    public static Context getContext() {
        return context;
    }

    public static Resources getResources(){
        return context.getResources();
    }

    public static String getString(int id){
        return context.getString(id);
    }

    public static int getColor (int id){
        return context.getResources().getColor(id);
    }
}
