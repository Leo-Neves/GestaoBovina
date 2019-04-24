package br.agr.terras.aurora;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import br.agr.terras.aurora.log.Logger;


/**
 * Created by leo on 27/10/16.
 */

public class AURORA {
    public static Context context;
    private static RequestQueue requestQueue;

    public static void init(Context context){
        AURORA.context = context;
        Logger.init("Aurora");
    }

    public static RequestQueue getRequestQueue(){
        if (requestQueue==null)
            requestQueue = Volley.newRequestQueue(context);
        return requestQueue;
    }
}
