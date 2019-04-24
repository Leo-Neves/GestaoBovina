package br.agr.terras.aurora.rest.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;

import br.agr.terras.aurora.log.Logger;
import br.agr.terras.aurora.rest.EnvioGSON;
import br.agr.terras.aurora.rest.Header;

/**
 * Created by leo on 22/10/15.
 */
public class RestSync {
    private URL url;
    final static int MAX_RETRIES = 3;
    int numTries = 0;

    public RestSync (String url){
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String post(EnvioGSON envioGSON) throws  IOException, JSONException{
        InputStream inputStream;
        OutputStream outputStream;
        boolean ativarLogs = true;
        while (numTries<MAX_RETRIES){
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Gson serializer = new GsonBuilder().setDateFormat("dd.MM.yyyy HH:mm:ss z").create();
                conn.setFixedLengthStreamingMode(serializer.toJson(envioGSON).getBytes().length);
                conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                conn.connect();
                outputStream = new BufferedOutputStream(conn.getOutputStream());
                if (ativarLogs){
                    Logger.init().methodCount(0).hideThreadInfo();
                    Logger.d("Envio de " + url);
                    Logger.json(serializer.toJson(envioGSON));
                }
                outputStream.write(serializer.toJson(envioGSON).getBytes());
                outputStream.flush();
                int status;
                try{
                    status = conn.getResponseCode();
                    if (status >= 400 ) {
                        inputStream = conn.getErrorStream();
                    } else {
                        inputStream = conn.getInputStream();
                    }

                    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                    String line = "";
                    String result = "";
                    while((line = bufferedReader.readLine()) != null)
                        result += line;
                    outputStream.close();
                    inputStream.close();
                    conn.disconnect();
                    if (ativarLogs)
                    try{
                        new JSONObject(result);
                        Logger.d("Resposta de "+url);
                        Logger.json(result);
                    }catch (JSONException e){
                        Logger.e("Resposta de "+url);
                        Logger.e(result);
                    }
                    Logger.init().methodCount(2).hideThreadInfo();
                    return result;
                }catch (EOFException e){
                    e.printStackTrace();
                }

            numTries++;
        }
        return null;
    }

    public int uploadFile(Header header, String path) throws IOException{
        boolean ativarLogs = true;
        Logger.init().methodCount(0).hideThreadInfo();
        if (ativarLogs)
            Logger.d("Envio de arquivo %s para url %s", path, url);
        File file = new File(path);
        String boundary = "===" + System.currentTimeMillis() + "===";
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
        httpConn.setRequestProperty("Bom", "Dia");
        OutputStream outputStream = httpConn.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
        writer.append("--" + boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"" + "id" + "\"").append("\r\n");
        writer.append("Content-Type: text/plain; charset=" + "UTF-8").append("\r\n");
        writer.append("\r\n");
        //writer.append(id).append("\r\n");
        writer.flush();
        writer.append("--" + boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"" + "uploadfile"+ "\"; filename=\"" + file.getName() + "\"").append("\r\n");
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append("\r\n");
        writer.append("Content-Transfer-Encoding: binary").append("\r\n");
        Iterator it = header.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            writer.append(pair.getKey()+": "+pair.getValue()).append("\r\n");
        }
        writer.append("\r\n");
        writer.flush();
        FileInputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1)
            outputStream.write(buffer, 0, bytesRead);
        outputStream.flush();
        inputStream.close();
        writer.append("\r\n");
        writer.flush();
        writer.append("\r\n").flush();
        writer.append("--" + boundary + "--").append("\r\n");
        writer.close();
        int responseCode = httpConn.getResponseCode();
        String resposta = httpConn.getResponseMessage()+"\n"+httpConn.getContentEncoding();//+"\n"+httpConn.getContent().toString();
        httpConn.disconnect();
        Logger.i("Resposta de "+url+":\n"+responseCode+"\n"+resposta);
        Logger.init().methodCount(2).hideThreadInfo();
        return responseCode;
    }

}
