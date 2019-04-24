package br.agr.terras.corelibrary.infraestructure.resources;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import br.agr.terras.corelibrary.infraestructure.resources.photo.NamePhotoGenerator;

@Deprecated
public class ImagemService {
	private boolean isFolderCreated;
	private boolean folderExists;
	private File diretorioImagens;
	private File arquivoImagem;
	private byte[] imagemDataStream;
	private Bitmap imagemBitmap;
	public String enderecoPastaImagens = "Ecotrack/Media/Ecotrack-images";
	private Context context;
	public static final int QUALITY = 100;
	public static final Bitmap.CompressFormat TYPE_IMAGE = Bitmap.CompressFormat.JPEG;

	public ImagemService(Context context) {
		this.context = context;
	}

	public void armazenarFoto(byte[] imagemDataStream) {
		this.imagemDataStream = imagemDataStream;
		String nomeImagem = NamePhotoGenerator.generateName(TYPE_IMAGE);
		this.arquivoImagem = new File(context.getFilesDir(), nomeImagem);

		if (arquivoImagem == null) {
			return;
		}
		escreverEmArquivoImagem();
	}

	private String nomeImagem;

	public void armazenarFoto(Bitmap imagemBitmap) {
		String nomeImagem = NamePhotoGenerator.generateName(TYPE_IMAGE);
		this.imagemBitmap = imagemBitmap;
		this.arquivoImagem = new File(context.getFilesDir(), nomeImagem);
		if (arquivoImagem == null) {
			return;
		}
		escreverEmArquivoImagemBitmap();
	}

	/*
	
	public void armazenarFoto(Bitmap imagemBitmap, String nome) {
		this.imagemBitmap = imagemBitmap;

		
		String nomeImagem = nome + getExtension();
		this.arquivoImagem = new File(context.getFilesDir(), nomeImagem);

		if (arquivoImagem == null) {
			return;
		}
		escreverEmArquivoImagemBitmap();

	}
	*/

	public void armazenarFotoPublica(Bitmap imagemBitmap, String folderPath) {
		this.imagemBitmap = imagemBitmap;
		this.enderecoPastaImagens = folderPath;
		arquivoImagem = criarArquivoDeImagemPublico();
		if (arquivoImagem == null) {
			return;
		}
		escreverEmArquivoImagemBitmap();
		atualizarGaleria(context);
	}

	public void armazenarFotoCache(Bitmap imagemBitmap) {
		nomeImagem = NamePhotoGenerator.generateName(TYPE_IMAGE);
		this.arquivoImagem = new File(context.getCacheDir(), nomeImagem);
		try {
			FileOutputStream fos = new FileOutputStream(arquivoImagem);
			imagemBitmap.compress(TYPE_IMAGE, QUALITY, fos);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		atualizarGaleria(context);
	}

	public File criarArquivoDeImagemPublico() {
		criarDiretorioExternoImagens();
		String nomeImagem = NamePhotoGenerator.generateName(TYPE_IMAGE);
		this.arquivoImagem = new File(diretorioImagens.getAbsolutePath(),
				nomeImagem);
		return this.arquivoImagem;
	}

	public boolean criarDiretorioExternoImagens() {
		diretorioImagens = getDiretorioExternoImagemEcotrack();
		isFolderCreated = diretorioImagens.mkdirs();
		folderExists = diretorioImagens.exists();

		if (!folderExists) {
			if (!isFolderCreated) {
				Log.e("MyCameraApp", "failed to create directory");
				return false;
			}
		}
		return true;
	}

	private void escreverEmArquivoImagem() {
		try {
			FileOutputStream fos = new FileOutputStream(arquivoImagem);
			fos.write(imagemDataStream);
			fos.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
		}
	}

	private void escreverEmArquivoImagemBitmap() {
		try {
			FileOutputStream fos = new FileOutputStream(arquivoImagem);
			imagemBitmap.compress(TYPE_IMAGE, QUALITY, fos);
			fos.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {
		}
	}

	public String getCaminhoImagemCompleto() {
		return arquivoImagem.getAbsolutePath();
	}

	public boolean isFolderCreated() {
		return isFolderCreated;
	}

	public File getArquivoImagem() {
		return this.arquivoImagem;
	}

	public File getDiretorioImagens() {
		return diretorioImagens;
	}

	public File getDiretorioExternoImagemEcotrack() {
		Log.i("ImagemService","enderecoPastaImagens = "+enderecoPastaImagens);
		File mediaDirectory = new File(
				Environment.getExternalStorageDirectory(), enderecoPastaImagens);
		
		Log.i("ImagemService","mediaDirectory = "+mediaDirectory.getAbsolutePath());
		
		return mediaDirectory;
	}

	public static void atualizarGaleria(Context context) {
		/*
		 * activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
		 * .parse("file://" + Environment.getExternalStorageDirectory())));
		 */

		context.sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
						+ Environment.getExternalStorageDirectory())));

	}

	public void copyCacheToSdCard() throws IOException {

		File src = new File(getCaminhoImagemCompleto());
		criarDiretorioExternoImagens();
		File dst = new File(diretorioImagens.getAbsolutePath(), nomeImagem);

		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
		atualizarGaleria(context);
	}

	public void copyCacheToSdCard(Context context,
			String fullPathCache, String nameImage) throws IOException {
		File diretorioImagens = getDiretorioExternoImagemEcotrack();
		boolean isFolderCreated = diretorioImagens.mkdirs();
		boolean folderExists = diretorioImagens.exists();

		if (!folderExists) {
			if (!isFolderCreated) {
				Log.e("MyCameraApp", "failed to create directory");
			}
		}

		File src = new File(fullPathCache);
		File dst = new File(diretorioImagens.getAbsolutePath(), nameImage);

		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}

		atualizarGaleria(context);
	}

	public static void copyCacheToSdCard(Context context,
			String fullPathCache, String nameImage, String outFolderImages)
			throws IOException {

		File diretorioImagens = new File(
				Environment.getExternalStorageDirectory(), outFolderImages);
		diretorioImagens.mkdirs();

		File src = new File(fullPathCache);
		File dst = new File(diretorioImagens.getAbsolutePath(), nameImage);

		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}

		atualizarGaleria(context);
	}

	private static int ldpi = 120;
	private static int mdpi = 160;
	private static int hdpi = 240;
	private static int xhdpi = 320;
	private static int xxhdpi = 480;

	private static int calcDpToPx(float dp, int dpi) {
		return (int) (dp * (dpi / 160));
	}

	public static String generateThumbnail(Context context,
			String absolutePath,int width_dp, int height_dp) throws IOException {

		int width = 0;
		int height = 0;
		String nameImage = NamePhotoGenerator.generateNameThumbnail(TYPE_IMAGE);
		
		switch (context.getResources().getDisplayMetrics().densityDpi) {

		case DisplayMetrics.DENSITY_LOW:
			width = calcDpToPx(width_dp, ldpi);
			height = calcDpToPx(height_dp, ldpi);

			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			width = calcDpToPx(width_dp, mdpi);
			height = calcDpToPx(height_dp, mdpi);
			break;
		case DisplayMetrics.DENSITY_HIGH:
			width = calcDpToPx(width_dp, hdpi);
			height = calcDpToPx(height_dp, hdpi);
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			width = calcDpToPx(width_dp, xhdpi);
			height = calcDpToPx(height_dp, xhdpi);
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			width = calcDpToPx(width_dp, xxhdpi);
			height = calcDpToPx(height_dp, xxhdpi);
			break;

		}

		Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
				BitmapFactory.decodeFile(absolutePath), width, height);

		OutputStream fOut = null;
		File file = new File(context.getFilesDir(), nameImage);
		fOut = new FileOutputStream(file);

		thumbImage.compress(TYPE_IMAGE, QUALITY, fOut);
		fOut.flush();
		fOut.close();
		
		return file.getAbsolutePath();

	}
	
	public static String generateThumbnail(Context context,
			String fullPathCache,String outFolderImages,int width_dp, int height_dp) throws IOException {

		int width = 0;
		int height = 0;
		String nameImage = NamePhotoGenerator.generateNameThumbnail(TYPE_IMAGE);
		
		switch (context.getResources().getDisplayMetrics().densityDpi) {

		case DisplayMetrics.DENSITY_LOW:
			width = calcDpToPx(width_dp, ldpi);
			height = calcDpToPx(height_dp, ldpi);

			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			width = calcDpToPx(width_dp, mdpi);
			height = calcDpToPx(height_dp, mdpi);
			break;
		case DisplayMetrics.DENSITY_HIGH:
			width = calcDpToPx(width_dp, hdpi);
			height = calcDpToPx(height_dp, hdpi);
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			width = calcDpToPx(width_dp, xhdpi);
			height = calcDpToPx(height_dp, xhdpi);
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			width = calcDpToPx(width_dp, xxhdpi);
			height = calcDpToPx(height_dp, xxhdpi);
			break;

		}

		Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
				BitmapFactory.decodeFile(fullPathCache), width, height);
		
		OutputStream fOut = null;
		File file = new File(outFolderImages, nameImage);
		File src = new File(outFolderImages);
		src.mkdirs();
		if(!file.exists()){
			file.createNewFile();
			
		}
		fOut = new FileOutputStream(file);

		thumbImage.compress(TYPE_IMAGE, QUALITY, fOut);
		fOut.flush();
		fOut.close();
		
		return file.getAbsolutePath();

	}
}
