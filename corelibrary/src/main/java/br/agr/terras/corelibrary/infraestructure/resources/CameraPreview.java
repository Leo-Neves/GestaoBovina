package br.agr.terras.corelibrary.infraestructure.resources;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	private boolean mPreviewRunning = false;
	private Context context;

	private String TAG = "CameraPreview";

	@SuppressWarnings("deprecation")
	public CameraPreview(Context context) {
		super(context);
		this.context = context;
		this.mCamera = getCameraInstance();
		this.mSurfaceHolder = this.getHolder();
		this.mSurfaceHolder.addCallback(this);
		this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}
	
	public Camera getCamera(){
		return mCamera;
	}
	private Camera getCameraInstance() {
		Camera camera = null;

		try {
			if (camera == null) {
				camera = Camera.open();
				// camera.setDisplayOrientation(90);
			}

			/*
			 * Camera.Parameters parameters = camera.getParameters();
			 * 
			 * parameters.setPictureFormat(PixelFormat.JPEG);
			 * //parameters.setRotation(90); //parameters.setPreviewSize(h, w);
			 * camera.setParameters(parameters);
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}

		return camera;
	}
	

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		try {
			if (mPreviewRunning) {
				mCamera.stopPreview();
			}

			
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
			mPreviewRunning = true;
		} catch (IOException e) {
			// left blank for now
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

		if (mCamera != null) {
			this.getHolder().removeCallback(this);
			mPreviewRunning = false;
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
			Log.e("surfaceDestroyed", "surfaceDestroyed");
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
			int width, int height) {
		try {
			Camera.Parameters myParameters = mCamera.getParameters();
			Camera.Size myBestSize = getBestPreviewSize(myParameters);
			width = myBestSize.width;
			height = myBestSize.height;
			
			if (width > 1024 && height > 768){
				// otimizar imagem para nao dar OutOfMemory
			}
			Log.i(TAG, "size selected = " + myBestSize.width + "x"
					+ myBestSize.height);
			Log.i(TAG, width+ " - " + height);
				      
	        
	        setDefaultSize(mCamera,width,height);
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
		} catch (Exception e) {

		}
	}

	private void setDefaultSize(Camera camera,int width,int height) {
		Camera.Parameters myParameters = mCamera.getParameters();
		myParameters.setPreviewSize(width, height);
		myParameters.setPictureSize(width, height);
		camera.setParameters(myParameters);
	}

	private void setSize2(Camera camera, int width, int height) {
		Camera.Parameters myParameters = mCamera.getParameters();
		Camera.Size myBestSize = getBestPreviewSize(myParameters);

		if (myBestSize != null) {
			myParameters.setPreviewSize(myBestSize.width, myBestSize.height);
			mCamera.setParameters(myParameters);

			Toast.makeText(
					context,
					"Best Size:\n" + String.valueOf(myBestSize.width) + " : "
							+ String.valueOf(myBestSize.height),
					Toast.LENGTH_LONG).show();

		}
	}

	private Camera.Size getBestPreviewSize(Camera.Parameters parameters) {
		Camera.Size bestSize = null;
		List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();

		bestSize = sizeList.get(0);

		for (int i = 1; i < sizeList.size(); i++) {
			if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
				bestSize = sizeList.get(i);
			}
		}

		return bestSize;
	}

}