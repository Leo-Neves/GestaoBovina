package br.agr.terras.aurora.rest.multipart;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import br.agr.terras.aurora.log.Logger;
import br.agr.terras.aurora.rest.Body;
import br.agr.terras.aurora.rest.DadosParaEnvio;
import br.agr.terras.aurora.rest.Header;

/**
 * Created by leo on 30/11/16.
 */

public class MultipartPost {
    private URL connectURL;
    private String responseString;
    private String descricao;
    private String nomeArquivo;
    private byte[] dataToServer;
    private InputStream fileInputStream = null;
    private Body body;
    private Header header;
    private String response;

    public MultipartPost(String url, DadosParaEnvio dadosParaEnvio, Body body, Header header) {
        try {
            connectURL = new URL(url);
            this.descricao = dadosParaEnvio.getNameInput();
            this.nomeArquivo = dadosParaEnvio.getNameFile();
            this.body = body;
            this.header = header;
            this.fileInputStream = new ByteArrayInputStream(dadosParaEnvio.getData());
        } catch (Exception ex) {
            Log.i("HttpFileUpload", "data == null? " + (dadosParaEnvio.getData() == null));
            ex.printStackTrace();
        }
    }

    public int enviar() {
        if (fileInputStream == null)
            return 400;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";
        try {
            Log.e(Tag, "Starting Http File enviar to URL");

            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "close");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            for (String key : header.keySet()) {
                conn.setRequestProperty(key, header.get(key));
            }

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"title\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(nomeArquivo);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"description\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(descricao);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            for (String key : body.keySet()) {
                dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(body.get(key));
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
            }


            dos.writeBytes("Content-Disposition: form-data; name=\"" + descricao + "\"; filename=\"" + nomeArquivo + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.e(Tag, "Headers are written");

            // create a buffer of maximum size
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Logger.i("Bytes in file to upload: %d", bytesRead);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();

            dos.flush();
            int responseCode = conn.getResponseCode();
            Log.v(Tag, "File Sent, Response:\n" + String.valueOf(responseCode) + "\n" + conn.getResponseMessage()+"\n"+(conn.getContent()!=null?conn.getContent().toString():"null content"));

            InputStream is = conn.getInputStream();

            // retrieve the response from server
            int ch;

            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            response = b.toString();
            Log.i("Response", response);
            dos.close();
            return responseCode;
        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
            ex.printStackTrace();
        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
            ioe.printStackTrace();
        }
        return 0;
    }

    public String getResponse() {
        return response;
    }
}
