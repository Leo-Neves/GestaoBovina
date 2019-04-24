package br.agr.terras.corelibrary.infraestructure.resources.photo;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class StorageUtils {

	public static File copyCacheToSdCard(Context context,String pathFileName,String directoryTarget) throws IOException {
		File dirTaget = createOutputExternalDirectory(directoryTarget);
		File fileCache = new File(pathFileName);
		copyFileToAnotherPlace(context,fileCache, dirTaget.getAbsolutePath());
		return new File(directoryTarget,fileCache.getName());
	}
	
	public static void copyFileToAnotherFile(Context context,File fileSource,File fileTarget) throws IOException {
		copyFiles(context,fileSource,fileTarget);
	}
	
	public static void copyFileToAnotherPlace(Context context,File fileSource,String directory) throws IOException {
		File fileTarget = new File(directory,fileSource.getName());
		copyFiles(context,fileSource,fileTarget);
	}
	
	private static void copyFiles(Context context,File fileSource, File fileTarget)  throws IOException {
		FileChannel inChannel = new FileInputStream(fileSource).getChannel();
		FileChannel outChannel = new FileOutputStream(fileTarget).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}
	
	
	public static File getOutputInternalFile(Context context,String nome){
		return new File(context.getFilesDir(), nome);
	}
	
	public static File getOutputCacheFile(Context context,String fileName){
		return new File(context.getCacheDir(), fileName);
	}
	
	public static File getOutputExternalFile(String directory,String nome){

		File mediaDirectory = createOutputExternalDirectory(directory);
		
		return new File(mediaDirectory, nome);
	}
	
	private static File createOutputExternalDirectory(String directory){
		File mediaDirectory = new File(
				Environment.getExternalStorageDirectory(), directory);
		
		if(!mediaDirectory.exists()){
			mediaDirectory.mkdirs();
		}
		return mediaDirectory;
	}
}
