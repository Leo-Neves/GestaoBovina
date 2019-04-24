package br.agr.terras.materialdroid.utils.charts.interfaces.dataprovider;

import br.agr.terras.materialdroid.utils.charts.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
