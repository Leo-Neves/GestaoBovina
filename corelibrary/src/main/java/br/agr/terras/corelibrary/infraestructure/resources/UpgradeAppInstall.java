package br.agr.terras.corelibrary.infraestructure.resources;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author alan_lins
 *
 */
public class UpgradeAppInstall {

	private Context context;
	private String nameFile = "app.apk";
	private String url_download;
	private File fileApp;
	private boolean runningDownload = true;
	private OnDownloadCallback onDownloadCallback = new OnDownloadCallback() {
		@Override
		public void onProgressUpdate(Integer... progress) {
		}
	};
	
	
	/**
	 * @return File downloaded
	 */
	public File getFileApp(){
		return fileApp;
	}


	/**
	 * @param context Context
	 * @param url_download url where is located your apk on web
	 */
	public UpgradeAppInstall(Context context, String url_download) {
		this.context = context;
		this.url_download = url_download;
	}
	
	/**
	 * @return whether download is stopped
	 */
	public boolean isStoppedDownload(){
		return !runningDownload; 
	}
	
	
	/**
	 * @param onDownloadCallback listener useful for progress update from AsyncTask
	 */
	public void setOnDownloadCallback(OnDownloadCallback onDownloadCallback){
		this.onDownloadCallback = onDownloadCallback;
	}
	

	/**
	 * Stop download
	 */
	public void stopDownload(){
		runningDownload = false;
	}
	

	/**
	 * Start download
	 */
	public void startDownload() {
		fileApp = new File(context.getExternalFilesDir(null), nameFile);
		
		HttpURLConnection connection = null;
		InputStream input = null;
		OutputStream output = null;
		try {

			// URL url = new URL(sUrl[0]);
			URL url = new URL(url_download);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			int fileLength = connection.getContentLength();

			// download the file
			input = new BufferedInputStream(url.openStream());
			output = new FileOutputStream(fileApp);

			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				if (runningDownload) {
					total += count;
					onDownloadCallback.onProgressUpdate((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}else{
					try {
						if (input != null) {
							input.close();
						}

						if (output != null) {
							output.close();
						}

						if (connection != null)
							connection.disconnect();

					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
			
				
			}

			output.flush();
			output.close();
			input.close();
		} catch (Exception e) {
			Log.e("YourApp", "Well that didn't work out so well...");
			Log.e("YourApp", e.getMessage());
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {
			}

			if (connection != null)
				connection.disconnect();
		}
	}

	/**
	 * Install the .apk downloaded
	 */
	public void installApk() {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.fromFile(fileApp),
				"application/vnd.android.package-archive");
		Log.d("UpgradeAppInstall", "install new .apk");
		context.startActivity(i);
	}

	/**
	 * @author alan_lins
	 * interface useful for progress update from AsyncTask
	 */
	public interface OnDownloadCallback {
		void onProgressUpdate(Integer... progress);
	}
}
