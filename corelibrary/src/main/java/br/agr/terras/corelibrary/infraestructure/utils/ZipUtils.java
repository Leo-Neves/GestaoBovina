package br.agr.terras.corelibrary.infraestructure.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by edson on 14/10/16.
 */
public class ZipUtils {

    public static void unpackZip(String path) {
        try {
            InputStream inputStream = new FileInputStream(path);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            String filename;
            byte[] buffer = new byte[1024];
            int count;

            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                filename = zipEntry.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
                if (zipEntry.isDirectory()) {
                    File fmd = new File(filename);
                    fmd.mkdirs();
                    continue;
                }
                FileOutputStream fout = new FileOutputStream(filename);
                while ((count = zipInputStream.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }
                fout.close();
                zipInputStream.closeEntry();
            }

            zipInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } finally {
            zis.close();
        }
    }

    public static InputStream getInputStreamCARFromZip(File carFile) throws IOException {
        ZipFile zipFile = new ZipFile(carFile);
        int i = 0;
        for (Enumeration e = zipFile.entries(); e.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            if (entry.getName().length() > 20) {
                System.out.println(entry.getName());
                System.out.println(i);
                return zipFile.getInputStream(entry);
            }
        }
        return null;
    }

    static public void extractFolder(String zipFile) throws IOException{
        File file = new File(zipFile);
        try{
            extractFolder(file, Charset.defaultCharset());
        }catch (Exception e){
            if (VersionUtils.getSdkVersion()>= Build.VERSION_CODES.N)
            try{
                extractFolder(file, Charset.forName("CP866"));
            }catch (Exception f){
                try{
                    extractFolder(file, Charset.forName("CP437"));
                }
                catch (IOException g){
                    throw g;
                }
            }
            else throw new ZipException();
        }
    }

    @SuppressLint("NewApi")
    private static void extractFolder(File file, Charset charset) throws IOException {
        System.out.println(file.getPath());
        int BUFFER = 2048;
        ZipFile zip;
        if (VersionUtils.getSdkVersion()>= Build.VERSION_CODES.N)
            zip = new ZipFile(file, charset);
        else
            zip = new ZipFile(file);
        String newPath = file.getPath().substring(0, file.getPath().length() - 4);

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            //destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip
                        .getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte data[] = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos,
                        BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }

            if (currentEntry.endsWith(".zip")) {
                // found a zip file, try to open
                extractFolder(destFile.getAbsolutePath());
            }
        }
    }


}
