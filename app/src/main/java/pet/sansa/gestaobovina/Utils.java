package pet.sansa.gestaobovina;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String currentPhotoPath;
    public static String currentUuid;

    public static  File createImageFile(String boiUuid) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                boiUuid,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        Utils.currentPhotoPath = image.getAbsolutePath();
        Utils.currentUuid = boiUuid;
        return image;
    }
}
