package br.agr.terras.materialdroid.utils.charts.interfaces.dataprovider;

import br.agr.terras.materialdroid.utils.charts.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
