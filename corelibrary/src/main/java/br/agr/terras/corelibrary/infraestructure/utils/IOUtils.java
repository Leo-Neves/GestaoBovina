package br.agr.terras.corelibrary.infraestructure.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by leo on 09/11/16.
 */

public class IOUtils {

    public static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();
    }

    public static byte[] fileToByteArray(File file) throws IOException {
        InputStream inputStream = new FileInputStream(file);
        byte[] bytes = inputStreamToByteArray(inputStream);
        return bytes;
    }

    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getBytesSize(@NonNull byte[] array) {
        double filesize = array.length / 1024D;
        if (filesize >= 1024)
            return String.format(MyDateUtils.getLocaleBR(), "%.02f %s", filesize / 1024D, "MB");
        else
            return String.format(MyDateUtils.getLocaleBR(), "%.02f %s", filesize, "KB");
    }

    public static String getFileSize(String path) {
        double filesize = new File(path).length() / 1024D;
        if (filesize >= 1024)
            return String.format(MyDateUtils.getLocaleBR(), "%.02f %s", filesize / 1024D, "MB");
        else
            return String.format(MyDateUtils.getLocaleBR(), "%.02f %s", filesize, "KB");
    }

    public static void delete(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                delete(child);
        fileOrDirectory.delete();
    }

    public static void moveFile(File input, String output) {
        String inputPath, inputFile, outputPath, outputFile;
        inputFile = input.getName();
        inputPath = input.getParent() + File.separator;
        outputPath = output.substring(0, output.lastIndexOf(File.separator) + 1);
        outputFile = output.substring(output.lastIndexOf(File.separator) + 1, output.length());
        Log.i(IOUtils.class.getSimpleName(), String.format("inputFile: %s\ninputPath: %s\noutputFile: %s\noutputPath: %s", inputFile, inputPath, outputFile, outputPath));
        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists())
                dir.mkdirs();
            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + outputFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            // write the output file
            out.flush();
            out.close();
            out = null;
            // delete the original file
            new File(inputPath + inputFile).delete();
            Log.i(IOUtils.class.getSimpleName(), "Terminou de salvar outro arquivo");
        } catch (Exception e) {
            Log.e(IOUtils.class.getSimpleName(), e.getMessage());
        }
    }

    public static void copyFile(File file, String outputPath) {
        String inputPath, inputFile;
        inputFile = file.getName();
        inputPath = file.getParent();
        InputStream in = null;
        OutputStream out = null;
        try {
            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + inputFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e(IOUtils.class.getSimpleName(), e.getMessage());
        }
    }
}
