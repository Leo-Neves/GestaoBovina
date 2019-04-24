package br.agr.terras.materialdroid.utils.charts.interfaces.dataprovider;

import br.agr.terras.materialdroid.utils.charts.data.BarData;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BarData getBarData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isHighlightFullBarEnabled();
}
