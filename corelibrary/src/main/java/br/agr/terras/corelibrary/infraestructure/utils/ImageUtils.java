package br.agr.terras.corelibrary.infraestructure.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import br.agr.terras.corelibrary.R;
import br.agr.terras.corelibrary.infraestructure.CORE;

/**
 * Created by leo on 01/06/16.
 */
public class ImageUtils {
    private static DisplayImageOptions options;
    private static ImageLoader imageLoader;

    public static void setImageViewResource(ImageView imageView, int drawable){
        init(imageView.getContext());
        imageLoader.displayImage("drawable://" + drawable, imageView, options);
    }

    public static void setImageViewPath(ImageView imageView, String path){
        init(imageView.getContext());
        imageLoader.displayImage("file://"+path, imageView, options);
    }

    public static void refreshImageLoader(){
        init(CORE.getContext());
    }

    public static void setBackground(final View v, int drawable){
        //if (imageLoader==null)
            init(v.getContext());
        imageLoader.loadImage("drawable://" + drawable, new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                v.setBackgroundDrawable(new BitmapDrawable(loadedImage));
            }
        });
    }

    private static void init(Context context){
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        imageLoader.clearMemoryCache();
    }

    public static Bitmap mergeBitmaps(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0,0, null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static ImageLoader getImageLoader(){
        if(imageLoader == null || !imageLoader.isInited())
            init(CORE.getContext());
        return imageLoader;
    }

    public static Bitmap drawableToBitmap(int drawableId){
        Drawable drawable = CORE.getResources().getDrawable(drawableId);
        return drawableToBitmap(drawable);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;
        if (VersionUtils.getSdkVersion() >= Build.VERSION_CODES.LOLLIPOP && drawable instanceof VectorDrawable) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            ((VectorDrawable) drawable).setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            ((VectorDrawable) drawable).draw(canvas);
        } else if (drawable instanceof BitmapDrawable) {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(CORE.getResources(), R.drawable.memarker_green);
        }
        return bitmap;
    }
}
