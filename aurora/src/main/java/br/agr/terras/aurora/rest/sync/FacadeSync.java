package br.agr.terras.aurora.rest.sync;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import br.agr.terras.aurora.entities.EntityAndroid;
import br.agr.terras.aurora.entities.EntityWeb;
import br.agr.terras.aurora.repositories.RepositoryAndroidRest;
import br.agr.terras.aurora.rest.Auth;
import br.agr.terras.aurora.rest.EnvioGSON;
import br.agr.terras.aurora.rest.RespostaGSON;
import retrofit.RetrofitError;

public class FacadeSync<W extends EntityWeb, A extends EntityAndroid> {
    Context context;
    private RestParser<W, A> restParser;
    private RepositoryAndroidRest<W, A> repository;
    private RestServiceStragety<W> restServiceStrategy;

    public FacadeSync(RestParser<W, A> restParser, RestServiceStragety<W> restServiceStrategy, RepositoryAndroidRest<W, A> repository) {
        this.restParser = restParser;
        this.restServiceStrategy = restServiceStrategy;
        this.repository = repository;
    }

    public void executeAll(Context context, String appVersion, Auth auth, Boolean makeDownload) throws SocketException, JsonParseException, RetrofitError {
        this.context = context;
        saves(auth);
        edits(auth);
        deletes(appVersion, auth);
        if (makeDownload) {
            downloadData(context, appVersion, auth);
        }
    }

    public void executeAll(Context context, String appVersion, Auth auth) throws SocketException, JsonParseException, RetrofitError {
        executeAll(context, appVersion, auth, true);
    }


    public void saves(Auth auth) throws SocketException, JsonParseException, RetrofitError {
        repository.atualizarThreadAtual();
        if (repository.findAllNaoSincronizadosSave().isEmpty()) {
            Log.i("Aurora - FacadeSync", "não há dados novos");
        } else {
            Log.i("Aurora - FacadeSync", repository.findAllNaoSincronizadosSave().size() + " dados novos");
        }

        if (!repository.findAllNaoSincronizadosSave().isEmpty()) {
            repository.corrigeWebIDChaves();
        }

        for (A entityAndroid : repository.findAllNaoSincronizadosSave()) {
            W entityWeb = restParser.parseAndroidToWeb(entityAndroid);
            parseAndroidToWebGeneric(entityAndroid, entityWeb);
            EnvioGSON<W> envioGson = restParser.parseEnvioGSON(entityWeb);
            addAuth(envioGson, auth);
            entityWeb = restServiceStrategy.executeSaveRest(envioGson);
            if (entityWeb != null) {
                repository.updateRestCallback(entityAndroid);

            }
        }

    }

    private void parseAndroidToWebGeneric(A entityAndroid, W entityWeb) {
        entityWeb.setId(entityAndroid.getUuid());
        entityWeb.setCreated(entityAndroid.getCreated());
        entityWeb.setModified(entityAndroid.getModified());
    }


    public void edits(Auth auth) throws SocketException, JsonParseException, RetrofitError {
        repository.atualizarThreadAtual();
        if (repository.findAllNaoSincronizadosUpdate().isEmpty()) {
            Log.i("Aurora - FacadeSync", "não há dados modificados");
        } else {
            Log.i("Aurora - FacadeSync", repository.findAllNaoSincronizadosUpdate().size() + " dados modificados");
        }

        for (A entityAndroid : repository.findAllNaoSincronizadosUpdate()) {
            W entityWeb = restParser.parseAndroidToWeb(entityAndroid);
            parseAndroidToWebGeneric(entityAndroid, entityWeb);
            EnvioGSON<W> envioGson = restParser.parseEnvioGSON(entityWeb);
            envioGson = addAuth(envioGson, auth);
            entityWeb = restServiceStrategy.executeUpdateRest(envioGson);
            if (entityWeb != null) {
                repository.iniciarEdicaoDados();
                entityAndroid.setCreated(entityWeb.getCreated());
                entityAndroid.setModified(entityWeb.getModified());
                restParser.putWebValuesIntoAndroid(entityWeb, entityAndroid);
                entityAndroid.setSincronizado(true);
                entityAndroid.setPre_excluido(false);
                repository.salvarSemTransaction(entityAndroid);
                repository.finalizarEdicaoDados();
            }
        }

    }


    public void deletes(String appVersion, Auth auth) throws SocketException, JsonParseException, RetrofitError {
        repository.atualizarThreadAtual();
        for (A entityAndroid : repository.findAllExcluidos()) {
            W entityWeb = restParser.parseAndroidToWeb(entityAndroid);
            parseAndroidToWebGeneric(entityAndroid, entityWeb);
            EnvioGSON<W> envioGson = restParser.parseEnvioGSON(entityWeb);
            envioGson = addAuth(envioGson, auth);

            if (restServiceStrategy.executeDeleteRest(envioGson) == 1) {
                repository.deleteRestCallback(entityAndroid);
            }
        }
    }

    public void downloadDataNoUuid(Context context, Auth auth) throws SocketException, JsonParseException, RetrofitError {
        repository.atualizarThreadAtual();
        EnvioGSON<W> envioGson = new EnvioGSON<W>(context);
        envioGson = addAuth(envioGson, auth);

        RespostaGSON<List<W>> respostaGSON = restServiceStrategy.list(envioGson);

        if (respostaGSON.getStatusCodeHttp() != 304) {
            if (respostaGSON.getResult() == null) {
                throw new CustomException("Faça o logout e login novamente para atualizar seus dados");
            }
            repository.deleteAll();
            for (W entityWeb : respostaGSON.getResult()) {
                repository.iniciarEdicaoDados();
                A entityAndroid = repository.novo(UUID.randomUUID().toString());
                restParser.putWebValuesIntoAndroid(entityWeb, entityAndroid);
                entityAndroid.setSincronizado(true);
                entityAndroid.setPre_excluido(false);
                repository.salvarSemTransaction(entityAndroid);
                repository.finalizarEdicaoDados();
            }
        }
    }


    public void downloadData(Context context, String appVersion, Auth auth) throws SocketException, JsonParseException, RetrofitError {
        repository.atualizarThreadAtual();
        this.context = context;
        A entityAndroidLastModified = repository.getLastModified();
        String lastModifiedString = "";

        EnvioGSON<W> envioGson = new EnvioGSON<W>(context);
        envioGson = addAuth(envioGson, auth);


        if (entityAndroidLastModified != null) {
            Date date = entityAndroidLastModified.getModified();

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy [HH:mm:ss]");
            if (date != null) {
                lastModifiedString = sdf.format(date);
            }
        }

        RespostaGSON<List<W>> respostaGSON = restServiceStrategy.list(envioGson);
        if (respostaGSON.getStatusCodeHttp() != 304) {
            repository.updateAllToDeleteRest();
            if (respostaGSON.getResult() == null) {
                throw new CustomException("Falha na sincronização. Tente novamente. " + respostaGSON.getStatusCodeHttp() + " \n" + new Gson().toJson(respostaGSON));
            }
            for (W entityWeb : respostaGSON.getResult()) {
                A entityAndroid = repository.getByWeb(entityWeb);
                if (entityAndroid == null) {
                    entityAndroid = repository.findByUuid(entityWeb.getUuid());
                }

                repository.iniciarEdicaoDados();
                if (entityAndroid != null) {
                    entityAndroid.setCreated(entityWeb.getCreated());
                    entityAndroid.setModified(entityWeb.getModified());
                    restParser.putWebValuesIntoAndroid(entityWeb, entityAndroid);
                    entityAndroid.setSincronizado(true);
                    entityAndroid.setPre_excluido(false);
                } else {
                    entityAndroid = repository.novo(entityWeb.getUuid());
                    entityAndroid.setCreated(entityWeb.getCreated());
                    entityAndroid.setModified(entityWeb.getModified());
                    restParser.putWebValuesIntoAndroid(entityWeb, entityAndroid);
                    entityAndroid.setSincronizado(true);
                    entityAndroid.setPre_excluido(false);
                    repository.salvarSemTransaction(entityAndroid);
                }
                repository.finalizarEdicaoDados();
            }
            repository.corrigeAndroidIDChaves();
        }
    }

    private EnvioGSON<W> addAuth(EnvioGSON<W> envioGson, Auth auth) {
        envioGson.setAuth(auth);
        Log.i("FacadeSync", envioGson.getAuth().getToken() + " Token");
        Log.i("FacadeSync", envioGson.getAuth().getUsuario() + " Usuario");
        Log.i("FacadeSync", auth.getToken() + " Token");
        Log.i("FacadeSync", auth.getUsuario() + " Usuario");
        return envioGson;
    }


}
