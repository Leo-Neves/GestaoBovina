package br.agr.terras.corelibrary.infraestructure.resources.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class MarcaDaguaUtil {

	public final static int DIREITO = 1;
	public final static int ESQUERDO = 2;

	public final static int ACIMA = 3;
	public final static int ABAIXO = 4;

	private Horizontal horizontal;
	private Vertical vertical;

	private float ponto_x;
	private float ponto_y;
	private Bitmap imagemPrincipal;
	private Bitmap imagemMarcaDagua;
	private Bitmap imagemMesclada;

	public static enum Horizontal{
		ESQUERDA, DIREITA;
	}
	
	public static enum Vertical{
		ACIMA, ABAIXO;
	}
	
	private MarcaDaguaUtil(Bitmap imagemPrincipal, Bitmap imagemMarcaDagua,
			Horizontal horizontal, Vertical vertical) {
		this.imagemMarcaDagua = imagemMarcaDagua;
		this.imagemPrincipal = imagemPrincipal;
		this.horizontal = horizontal;
		this.vertical = vertical;
	}

	public static class Builder {
		private Bitmap imagemPrincipal;
		private Bitmap imagemMarcaDagua;
		private Horizontal horizontal = Horizontal.DIREITA;
		private Vertical vertical = Vertical.ABAIXO;
		private Context context;
		private int drawableImagemMarcaDagua=0;
		private int drawableImagemPrincipal=0;

		
		/**
		 *  Exemplo: MarcaDaguaUtil marcaDaguaUtil = new MarcaDaguaUtil.Builder(context).setImagemPrincipal(imagefilePath).setImagemMarcaDagua(imagepathMarcaagua).build();
				marcaDaguaUtil.mesclar();
		 * @param context
		 */
		public Builder(Context context){
			this.context = context;
		}
		public Builder setImagemPrincipal(String imagePath) {
			this.imagemPrincipal = BitmapFactory.decodeFile(imagePath);
			return this;
		}

		public Builder setImagemMarcaDagua(String imagePath) {
			this.imagemMarcaDagua = BitmapFactory.decodeFile(imagePath);
			return this;
		}

		public Builder setImagemPrincipal(Bitmap imagemPrincipal) {
			this.imagemPrincipal = imagemPrincipal;
			return this;
		}

		public Builder setImagemMarcaDagua(Bitmap imagemMarcaDagua) {
			this.imagemMarcaDagua = imagemMarcaDagua;
			return this;
		}

		public Builder setImagemPrincipal(
				int drawableImagemPrincipal) {
			this.drawableImagemPrincipal = drawableImagemPrincipal;
			return this;
		}

		/**
		 * @param drawableImagemMarcaDagua 
		 * @return Builder
		 */
		public Builder setImagemMarcaDagua(
				int drawableImagemMarcaDagua) {
			this.drawableImagemMarcaDagua = drawableImagemMarcaDagua;
			
			return this;
		}

		/** 
		 * Posição na horizontal da marca d'água.
		 * @param horizontal
		 * @return Builder
		 */
		public Builder setHorizontal(Horizontal horizontal) {
			this.horizontal = horizontal;
			return this;
		}

		/**
		 * Posição na vertical da marca d'água.
		 * @param vertical
		 * @return Builder
		 */
		public Builder setVertical(Vertical vertical) {
			this.vertical = vertical;
			return this;
		}

		public MarcaDaguaUtil build() throws Exception {

			if(drawableImagemMarcaDagua!=0){
				this.imagemMarcaDagua = BitmapFactory.decodeResource(
						context.getResources(), drawableImagemMarcaDagua);
			}
			
			if(drawableImagemPrincipal!=0){
				this.imagemPrincipal = BitmapFactory.decodeResource(
						context.getResources(), drawableImagemPrincipal);
			}
			
			if (imagemMarcaDagua == null) {
				throw new Exception("imagemMarcaDagua não foi informado");
			}

			if (imagemPrincipal == null) {
				throw new Exception("imagemPrincipal não foi informado");
			}
			return new MarcaDaguaUtil(imagemPrincipal, imagemMarcaDagua,
					horizontal, vertical);
		}
	}

	/**
	 * @return Bitmap Retorna um Bitmap da foto com a marca d'água.
	 */
	public Bitmap mesclar() {
		configurarPosicaoImageXY();
		mesclarImagens();
		return imagemMesclada;
	}

	private void mesclarImagens() {
		imagemMesclada = Bitmap.createBitmap(imagemPrincipal.getWidth(),
				imagemPrincipal.getHeight(), imagemPrincipal.getConfig());
		Canvas canvasImagemMesclada = new Canvas(imagemMesclada);
		canvasImagemMesclada.drawBitmap(imagemPrincipal, new Matrix(), null);
		canvasImagemMesclada.drawBitmap(imagemMarcaDagua, ponto_x, ponto_y,
				null);
	}

	private void configurarPosicaoImageXY() {

		if (horizontal == Horizontal.ESQUERDA) {
			ponto_x = 0;
		} else {
			ponto_x = imagemPrincipal.getWidth() - imagemMarcaDagua.getWidth();
		}

		if (vertical == Vertical.ACIMA) {
			ponto_y = 0;
		} else {
			ponto_y = imagemPrincipal.getHeight()
					- imagemMarcaDagua.getHeight();
		}
	}

}
