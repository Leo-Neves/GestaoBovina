package br.agr.terras.aurora.rest.sync;

import br.agr.terras.aurora.entities.EntityAndroid;
import br.agr.terras.aurora.entities.EntityWeb;
import br.agr.terras.aurora.rest.EnvioGSON;

public interface RestParser<W extends EntityWeb,A extends EntityAndroid> {
	void putWebValuesIntoAndroid(W entityWeb, A entityAndroid);
	A getNewInstanceEntityAndroid();
	W parseAndroidToWeb(A entityAndroid);
	EnvioGSON<W> parseEnvioGSON(W entityWeb);
}
