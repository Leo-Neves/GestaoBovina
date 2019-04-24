package br.agr.terras.corelibrary.infraestructure.resources;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogJson {
	
	public static int LOG_ENVIO = 1;
	public static int LOG_RESPOSTA = 2;
	
	private static String cleanSpecialCharactersUrl(String url) {
		url = url.replaceAll("\\.", "_");
		url = url.replaceAll("/", "_");
		url = url.replaceAll(":", "_");
		return url;

	}
	
	
	public static void writeLogFile(Context context, String body, String url,int tipoLogFile, String folderLogs) {
		if ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {

			FileOutputStream fos = null;
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HH_mm_ss_SSS")
					.format(new Date());

			String fileName = cleanSpecialCharactersUrl(url) + "_";
			if (tipoLogFile == LOG_ENVIO) {
				fileName += "envio_";
			} else {
				fileName += "resposta_";
			}

			fileName += timeStamp;

			try {
				final File dir = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ "/"+folderLogs+"/");

				if (!dir.exists()) {
					dir.mkdirs();
				}

				final File myFile = new File(dir, fileName + ".txt");

				if (!myFile.exists()) {
					myFile.createNewFile();
				}

				fos = new FileOutputStream(myFile);

				fos.write(body.getBytes());
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
