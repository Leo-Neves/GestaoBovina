package br.agr.terras.materialdroid.utils.charts.interfaces.dataprovider;

import br.agr.terras.materialdroid.utils.charts.components.YAxis.AxisDependency;
import br.agr.terras.materialdroid.utils.charts.data.BarLineScatterCandleBubbleData;
import br.agr.terras.materialdroid.utils.charts.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
