package br.agr.terras.materialdroid.utils.charts.interfaces.dataprovider;

import br.agr.terras.materialdroid.utils.charts.data.CombinedData;

/**
 * Created by philipp on 11/06/16.
 */
public interface CombinedDataProvider extends LineDataProvider, BarDataProvider, BubbleDataProvider, CandleDataProvider, ScatterDataProvider {

    CombinedData getCombinedData();
}
