package br.agr.terras.corelibrary.infraestructure.resources.wms;

import java.util.HashMap;
import java.util.Iterator;

import static br.agr.terras.corelibrary.infraestructure.resources.wms.WMSTileProvider.MAXX;
import static br.agr.terras.corelibrary.infraestructure.resources.wms.WMSTileProvider.MAXY;
import static br.agr.terras.corelibrary.infraestructure.resources.wms.WMSTileProvider.MINX;
import static br.agr.terras.corelibrary.infraestructure.resources.wms.WMSTileProvider.MINY;

/**
 * Created by leo on 27/04/17.
 */

public class WmsParametros extends HashMap<String, String> {
    private String baseUrl;
    private String url;

    public WmsParametros(String url) {
        this.baseUrl = url;
    }

    public String getUrlComBbox(double[] bbox) {
        Iterator<String> iterator = keySet().iterator();
        url = baseUrl + "?";
        while (iterator.hasNext()) {
            String key = iterator.next();
            url += key + "=" + get(key);
            url += "&";
        }
        url += "bbox=" + bbox[MINX] + "," + bbox[MINY] + "," + bbox[MAXX] + "," + bbox[MAXY];
        return url;
    }
}
