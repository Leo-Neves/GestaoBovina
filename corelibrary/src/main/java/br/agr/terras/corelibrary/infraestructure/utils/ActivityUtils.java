package br.agr.terras.corelibrary.infraestructure.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

import br.agr.terras.corelibrary.infraestructure.CORE;

/**
 * Created by leo on 30/01/17.
 */

public class ActivityUtils {

    public static boolean temLeitorDePDF() {
        PackageManager packageManager = CORE.getContext().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        if (packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void abrirLeitorPDF(Activity activity, File arquivoPDF){
        Uri uri = Uri.fromFile(arquivoPDF);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(uri, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(pdfIntent);
    }
}
