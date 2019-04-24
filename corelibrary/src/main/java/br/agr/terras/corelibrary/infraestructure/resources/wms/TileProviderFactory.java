package br.agr.terras.corelibrary.infraestructure.resources.wms;

import android.content.Context;

public class TileProviderFactory {
	
	public static WMSMixedTileProvider getWMSLimitePropriedade(Context context){
		final String url = "http://54.232.114.165/cgi-bin/mapserv?map=/opt/fgs/apps/ecotrack2/mapfile/analise_paragominas.map";
        WMSMixedTileProvider tileProvider = new WMSMixedTileProvider(context,url,"car_shapes");
		return tileProvider;
	}
	
	public static WMSMixedTileProvider getWMSTalhoes(Context context){
		final String url = "http://54.232.114.165/cgi-bin/mapserv?map=/opt/fgs/apps/ecotrack2/mapfile/analise_paragominas.map"; 
        WMSMixedTileProvider tileProvider = new WMSMixedTileProvider(context,url,"analise_talhao");
		return tileProvider;
	}

}
