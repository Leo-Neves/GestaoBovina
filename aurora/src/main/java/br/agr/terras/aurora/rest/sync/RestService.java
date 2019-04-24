package br.agr.terras.aurora.rest.sync;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import br.agr.terras.aurora.rest.EnvioGSON;
import br.agr.terras.aurora.rest.RespostaGSON;

public interface RestService<A> {
	RespostaGSON<A> save(EnvioGSON<A> envioGson) throws JSONException, IOException;
	RespostaGSON<List<A>> list(EnvioGSON<A> envioGSON) throws JSONException, IOException;
	int uploadFile(String id, String path) throws IOException;
}
