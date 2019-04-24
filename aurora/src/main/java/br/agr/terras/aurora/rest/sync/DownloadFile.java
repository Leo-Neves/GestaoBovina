package br.agr.terras.aurora.rest.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import br.agr.terras.aurora.entities.EntityAndroidDownload;
import br.agr.terras.aurora.log.Logger;
import br.agr.terras.aurora.repositories.RepositoryAndroidRest;
import br.agr.terras.aurora.rest.EnvioListener;
import br.agr.terras.aurora.rest.Header;

/**
 * Created by leo on 26/08/15.
 */
public class DownloadFile {
    private HttpURLConnection connection;
    private String path;
    private String url1;
    private Header header;
    private EnvioListener listener;

    public DownloadFile() {
    }

    public DownloadFile(final String path, final String url1, Header header, final EnvioListener listener) {
        this.path = path;
        this.url1 = url1;
        this.header = header;
        this.listener = listener;
    }

    public void fromEntityAndroidDownload(Context context, final EntityAndroidDownload entity, final RepositoryAndroidRest repository, String extensao) {
        final String path = Environment.getExternalStorageDirectory().getPath() + File.separator + context.getPackageName() + File.separator + randomString(4) + "." + extensao;
        Log.i("DownloadFile", "Path: " + path);
        File file = new File(path);
        File directory = new File(file.getParentFile().getAbsolutePath());
        try {
            directory.mkdirs();
            file.createNewFile();
            URL url = new URL(entity.getFile_url());
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(path);
            byte[] b = new byte[2048];
            int lenght;
            while ((lenght = is.read(b)) != -1) {
                os.write(b, 0, lenght);
            }
            is.close();
            os.close();
            repository.iniciarEdicaoDados();
            entity.setFile_path(path);
            repository.salvarSemTransaction(entity);
            repository.finalizarEdicaoDados();
        } catch (IOException e) {
            Log.e("DownloadFile", "Não foi possível baixar: " + entity.getFile_url());
            e.printStackTrace();
        }
    }

    public void baixar() {
        if (Looper.myLooper() != Looper.getMainLooper())
            baixar(path, url1, header, listener);
        else {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    baixar(path, url1, header, listener);
                    return null;
                }
            }.execute();
        }
    }

    public void baixar(final String path, final String url1, Header header, final EnvioListener listener) {
        URL url2 = null;
        try {
            url2 = new URL(url1);
        } catch (MalformedURLException e) {
            Logger.e("MalformedURLException\n", url1);
            listener.onError(e, 0);
            e.printStackTrace();
            return;
        }
        final URL url = url2;
        Log.i("DownloadFile", "Path: " + path);
        try {
            connection = (HttpURLConnection) url.openConnection();
            for (String key : header.keySet()) {
                connection.setRequestProperty(key, header.get(key));
                Log.i(getClass().getSimpleName(), key + "," + header.get(key));
            }
            connection.connect();
        } catch (IOException e) {
            Logger.e("InternetConncectionException");
            e.printStackTrace();
            listener.onError(e, 500);
            return;
        }
        try {
            int status;
            InputStream inputStream;
            status = connection.getResponseCode();
            if (status >= 400) {
                inputStream = connection.getErrorStream();
            } else {
                inputStream = connection.getInputStream();
            }
            OutputStream os = new FileOutputStream(path);
            byte[] b = new byte[2048];
            int lenght;
            while ((lenght = inputStream.read(b)) != -1) {
                os.write(b, 0, lenght);
            }
            inputStream.close();
            os.close();
            File f = new File(path);
            f.getParentFile().mkdirs();
            Log.i(getClass().getSimpleName(), "Download baixado. Size: " + f.length() / 1024 + "kb");
            listener.onSuccess("Success");
        } catch (IOException e) {
            Log.e("DownloadFile", "Não foi possível baixar: " + url1);
            e.printStackTrace();
            listener.onError(e, 500);
        }
    }

    public void cancelar() {
        if (connection != null)
            connection.disconnect();
    }

    private String randomString(int len) {
        String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(random.nextInt(AB.length())));
        return sb.toString();
    }
}
