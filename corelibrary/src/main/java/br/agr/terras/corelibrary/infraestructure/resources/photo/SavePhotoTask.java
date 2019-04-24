package br.agr.terras.corelibrary.infraestructure.resources.photo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;

import br.agr.terras.corelibrary.infraestructure.resources.ImagemService;
import br.agr.terras.corelibrary.infraestructure.resources.Md5Service;

public class SavePhotoTask extends AsyncTask<byte[], String, String> {
	private SavePhotoCallbackListener savePhotoCallbackListener;
	private Dialog dialog;
	private byte[] fotoDataStream;
	private Bitmap imagem;
	private Bitmap imagemunida;
	private ImagemService imageUtils;
	private String folderPath;
	private String md5sum;
	private Location location;
	private double latitude = 0;
	private double longitude = 0;
	private double precisao = 0;
	private double altitude = 0;

	public SavePhotoTask(SavePhotoCallbackListener savePhotoCallbackListener, String folderPath) {
		this.savePhotoCallbackListener = savePhotoCallbackListener;
		this.folderPath = folderPath;
	}

	@Override
	protected void onPreExecute() {

		dialog = new Dialog(savePhotoCallbackListener.getAtivity());
		//dialog.setMax(100);
		//dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
		//dialog.setMessage("salvando foto ...");
		dialog.setTitle("Aguarde");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

	}

	@Override
	protected String doInBackground(byte[]... data) {
		processarFoto(data[0], dialog);
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		dialog.dismiss();
		savePhotoCallbackListener.onPostExecutePhoto(imageUtils.getCaminhoImagemCompleto(),imagemunida,md5sum,imageUtils.getArquivoImagem().getName(),location);
	}
	

	public void processarFoto(byte[] fotoDataStream, ProgressDialog dialog) {
		this.fotoDataStream = fotoDataStream;
		capturaGPS();
		criarImagem();
		dialog.incrementProgressBy(20);

		//colocarMarcaDagua();

		geraMd5Sum();
		dialog.incrementProgressBy(20);

		armazenaNoDispositivo();
		dialog.incrementProgressBy(20);
		
		addGeoTag();
		dialog.incrementProgressBy(20);
		
		
		addOrientationTag();
		dialog.incrementProgressBy(20);
		
	}
	
	public void processarFoto(byte[] fotoDataStream, Dialog dialog) {
		this.fotoDataStream = fotoDataStream;
		capturaGPS();
		criarImagem();
		
		//colocarMarcaDagua();

		geraMd5Sum();

		armazenaNoDispositivo();
		
		addGeoTag();
	
		addOrientationTag();
	}

	
	private void capturaGPS() {
		if(savePhotoCallbackListener.getLocationGPS() != null){
			location = savePhotoCallbackListener.getLocationGPS();
			latitude = savePhotoCallbackListener.getLocationGPS().getLatitude();
			longitude = savePhotoCallbackListener.getLocationGPS().getLongitude();
			precisao = savePhotoCallbackListener.getLocationGPS().getAccuracy();
			altitude = savePhotoCallbackListener.getLocationGPS().getAltitude();  	
		}
	}

	private void criarImagem() {
		
		// otimiza a imagem capturada
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inPurgeable = true;
		opts.inTempStorage = new byte[8 * 1024];
		opts.inSampleSize = 6;
		opts.inInputShareable = true;

		imagem = BitmapFactory.decodeByteArray(fotoDataStream, 0,
				fotoDataStream.length);
		
		imagem = Bitmap.createScaledBitmap(imagem, 1280, 720, false);
		imagemunida = imagem;

		
	}


	private void armazenaNoDispositivo() {
		imageUtils = new ImagemService(savePhotoCallbackListener.getAtivity());
		imageUtils.armazenarFotoPublica(imagemunida, folderPath);
	}

	private void geraMd5Sum() {
		this.md5sum = Md5Service.getMD5_Hash(imagemunida);
	}

	private void addGeoTag() {
		GeoTagUtils.geoTag(imageUtils.getCaminhoImagemCompleto(), latitude,
				longitude);
		imagemunida = BitmapFactory.decodeFile(imageUtils.getCaminhoImagemCompleto());
		
	}
	
	private void addOrientationTag() {
		GeoTagUtils.getOrientationTag(imageUtils.getCaminhoImagemCompleto());
	}
}
