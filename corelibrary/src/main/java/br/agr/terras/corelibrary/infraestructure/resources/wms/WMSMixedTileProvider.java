package br.agr.terras.corelibrary.infraestructure.resources.wms;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WMSMixedTileProvider implements TileProvider {

	private final WMSTileProvider provider;
	private final Context context;
	private String TAG = "WMSMixedTileProvider";
	private String layer;
	private String EPSG = "900913";

	private boolean alwaysOnline = false;

	public WMSMixedTileProvider(final Context context, final String url,
			String layer) {
		this.context = context;
		this.layer = layer;
		provider = new WMSTileProvider(new WmsParametros(createUrlFull(url)));
	}

	@Override
	public Tile getTile(final int arg0, final int arg1, final int arg2) {
		final String filename = getTileFilename(arg0, arg1, arg2);

		if (alwaysOnline) {
			Log.i(TAG, "always online");
			final Tile tile = provider.getTile(arg0, arg1, arg2);
			saveTileInCache(tile, filename);
			return tile;
		}

		boolean contained = false;
		for (final String file : context.fileList()) {
			if (file.contains(filename)) {
				contained = true;
				Log.i(TAG, filename + " encontrado");
				break;
			}
		}

		if (contained) {

			byte[] image;

			try {
				image = readTileFromCache(filename);
			} catch (Exception e) {
				Log.e(TAG, "problema na leitura do Tile");
				return null;
			}
			try {

				if (BitmapFactory.decodeByteArray(image, 0, image.length) != null) {
					Tile tile = new Tile(256, 256, image);
					return tile;
				} else {
					Log.e(TAG, filename + " excluindo");
					if (context.deleteFile(filename)) {
						Log.e(TAG, filename + " excluído");
					}
					return null;
				}
			} catch (Exception e) {
				Log.e(TAG, "problema na criação do Tile");
				return null;
			}

		} else {
			Log.i(TAG, filename + " não encontrado");

			final Tile t = provider.getTile(arg0, arg1, arg2);
			if (t != null) {
				saveTileInCache(t, filename);
			}
			return t;

		}
	}


	private void saveTileInCache(Tile tile, String fileName) {
		try {
			if (tile == null) {
				throw new Exception("Tile nulo");
			}

			final FileOutputStream fos = context.openFileOutput(
					fileName, Context.MODE_PRIVATE);
			fos.write(tile.data);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			Log.e(TAG,
					"erro de gravar em cache no tile:" + fileName
							+ e.getMessage());
		}
	}

	private byte[] readTileFromCache(String fileName) {
		try {
			InputStream is = context.openFileInput(fileName);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = is.read(b)) != -1) {
				bos.write(b, 0, bytesRead);
			}
			byte[] bytes = bos.toByteArray();
			return bytes;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getTileFilename(final int arg0, final int arg1,
			final int arg2) {
		return  "tilecache_"+layer+"_"+arg0 + "_" + arg1 + "_" + arg2 + ".png";
	}

	public boolean isAlwaysOnline() {
		return alwaysOnline;
	}

	public void setAlwaysOnline(boolean alwaysOnline) {
		this.alwaysOnline = alwaysOnline;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public String getEPSG() {
		return EPSG;
	}

	public void setEPSG(String ePSG) {
		EPSG = ePSG;
	}

	private String createUrlFull(String url) {
		return url + "&LAYERS=" + getLayer() + "&TRANSPARENT=TRUE"
				+ "&FORMAT=image/png" + "&SERVICE=WMS" + "&VERSION=1.1.1"
				+ "&REQUEST=GetMap" + "&SRS=EPSG:" + getEPSG()
				+ "&BBOX=%f,%f,%f,%f" + "&WIDTH=256" + "&HEIGHT=256";
	}

}
