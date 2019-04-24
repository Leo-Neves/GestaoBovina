package br.agr.terras.materialdroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

import br.agr.terras.materialdroid.charts.PieChart;
import br.agr.terras.materialdroid.utils.charts.animation.Easing;
import br.agr.terras.materialdroid.utils.charts.components.Description;
import br.agr.terras.materialdroid.utils.charts.data.PieData;
import br.agr.terras.materialdroid.utils.charts.data.PieDataSet;
import br.agr.terras.materialdroid.utils.charts.data.PieEntry;
import br.agr.terras.materialdroid.utils.charts.formatter.PercentFormatter;

/**
 * Created by leo on 18/11/15.
 */
public class ProgressChart extends PieChart {
    private Context context;
    private TypedArray typedArray;
    private int transparentColor;
    private int progressColor;
    private int completedColor;
    private int progress;
    private int radius;
    private float textSize;

    public ProgressChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        float scale = this.context.getResources().getDisplayMetrics().density;
        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressChart, 0, 0);
        transparentColor = typedArray.getColor(R.styleable.ProgressChart_transparentColor, context.getResources().getColor(R.color.cinza));
        progressColor = typedArray.getColor(R.styleable.ProgressChart_progressColor, context.getResources().getColor(R.color.TerrasBlue));
        completedColor = typedArray.getColor(R.styleable.ProgressChart_completedColor, context.getResources().getColor(R.color.TerrasGreen));
        progress = typedArray.getInteger(R.styleable.ProgressChart_progressValue, 0);
        radius = typedArray.getInteger(R.styleable.ProgressChart_radius, 90);
        checkProgress(progress);
        textSize = typedArray.getDimension(R.styleable.ProgressChart_textSize, 50f) / scale;
        Log.i(getClass().getSimpleName(), ""+scale);
        configureLayout();
        configureData();
    }

    private void configureLayout(){
        setTouchEnabled(false);
        setUsePercentValues(false);
        setDescription(new Description());
        setClickable(false);
        setCenterTextTypeface(Typeface.createFromAsset(context.getResources().getAssets(), "fonts/Roboto-Medium.ttf"));
        setDrawHoleEnabled(true);
        setHoleColor(Color.TRANSPARENT);
        setTransparentCircleColor(Color.WHITE);
        setTransparentCircleAlpha(0);
        setHoleRadius(radius);
        setTransparentCircleRadius(radius);
        setRotationAngle(0);
        setRotationEnabled(true);
        setHighlightPerTapEnabled(false);
        setCenterText(progress + "%");
        setDrawCenterText(true);
        setCenterTextColor(getProgressColor());
        setCenterTextSize(textSize);
        animateY(1500, Easing.EasingOption.EaseInOutQuad);
        spin(2000, 0, 360, Easing.EasingOption.EaseInBounce);
        getLegend().setEnabled(false);
        Description description = new Description();
        description.setText("");
        setDescription(description);
    }

    private void configureData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        PieEntry valor = new PieEntry(progress,0);
        PieEntry vazio = new PieEntry(100-progress,1);
        entries.add(valor);
        entries.add(vazio);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("");
        strings.add("");

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(0);
        dataSet.setSelectionShift(5f);


        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getProgressColor());
        //colors.add(Color.rgb(145, 147, 148));
        colors.add(getTransparentColor());
        dataSet.setColors(colors);


        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(13);
        data.setDrawValues(false);
        setData(data);
        highlightValues(null);
        invalidate();
    }

    private Integer getProgressColor(){
        if (progress==100){
            return Color.rgb(Color.red(completedColor), Color.green(completedColor), Color.blue(completedColor));
        }else{
            return Color.rgb(Color.red(progressColor), Color.green(progressColor), Color.blue(progressColor));
        }
    }

    private Integer getTransparentColor(){
        return Color.rgb(Color.red(transparentColor), Color.green(transparentColor), Color.blue(transparentColor));
    }

    public void setProgress(int progress){
        checkProgress(progress);
        setCenterText(this.progress + "%");
        setCenterTextColor(getProgressColor());
        configureData();
    }

    private void checkProgress(int progress){
        if (progress>100)
            progress = 100;
        if (progress<0)
            progress = 0;
        this.progress = progress;
    }
}
