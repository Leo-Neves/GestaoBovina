package br.agr.terras.aurora.rest.sync;

import java.util.List;

import br.agr.terras.aurora.entities.IdList;
import br.agr.terras.aurora.rest.Auth;
import br.agr.terras.aurora.rest.EnvioGSON;
import br.agr.terras.aurora.rest.RespostaGSON;

public interface RestServiceStragety<W> {

	W executeUpdateRest(EnvioGSON<W> envioGson);

	W executeSaveRest(EnvioGSON<W> envioGson);

	RespostaGSON<List<W>> list(EnvioGSON<W> envioGSON);

	
	int executeDeleteRest(EnvioGSON<W> envioGSON);
	

	
}
