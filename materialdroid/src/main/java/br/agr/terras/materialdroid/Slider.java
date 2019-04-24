package br.agr.terras.materialdroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import br.agr.terras.materialdroid.enums.SliderModo;

/**
 * Created by leo on 22/02/16.
 */
public class Slider extends LinearLayout{
    private TypedArray typedArray;
    private View view;
    private TextView titulo;
    private TextView quantidade;
    private SeekBar slider;
    private br.agr.terras.materialdroid.childs.SeekBar sliderInteiro;
    private br.agr.terras.materialdroid.childs.SeekBar sliderDecimal;
    private LinearLayout containerMaterialSlider;
    private String unidade = "";
    private int minimo = 0;
    private int maximo = 100;
    int tipo = 0;
    private boolean primeiroAcesso = true;

    private OnValueChangedListener onValueChangedListener;

    public Slider(Context context){
        super(context);
        init(context);
    }

    public Slider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttrs(context, attrs);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.component_slider, this);
        titulo = (TextView)view.findViewById(R.id.textViewTituloSeekBarMaterial);
        quantidade = (TextView)view.findViewById(R.id.texViewQuantidadeSeekBarMaterial);
        slider = (SeekBar)view.findViewById(R.id.sliderMaterial);
        sliderInteiro = (br.agr.terras.materialdroid.childs.SeekBar) view.findViewById(R.id.sliderMaterialInteiro);
        sliderDecimal = (br.agr.terras.materialdroid.childs.SeekBar) view.findViewById(R.id.sliderMaterialDecimal);
        containerMaterialSlider = (LinearLayout)view.findViewById(R.id.linearLayoutSliderMaterial);
        containerMaterialSlider.setEnabled(false);
        containerMaterialSlider.setOnTouchListener(null);
    }

    private void initAttrs(Context context, AttributeSet attrs){
        typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SeekBar, 0, 0);
        configureModoDiscreto();
        configureTitulo();
        configureSliders();
        configureColors();
    }

    private void configureModoDiscreto() {
        sliderInteiro.setDiscreteMode(typedArray.getBoolean(R.styleable.SeekBar_sb_discreteMode, true));
    }

    private void configureTitulo(){
        String tituloTexto = typedArray.getString(R.styleable.SeekBar_sb_titulo);
        if (tituloTexto!=null)
            titulo.setText(tituloTexto);
        else
            titulo.setVisibility(GONE);
    }

    private void configureSliders(){
        minimo = typedArray.getInt(R.styleable.SeekBar_sb_minimo, 0);
        maximo = typedArray.getInt(R.styleable.SeekBar_sb_maximo, 100);
        unidade = typedArray.getString(R.styleable.SeekBar_sb_unidade);
        tipo = typedArray.getInteger(R.styleable.SeekBar_sb_tipo, 0);
        refreshSliders();
        //slider.setProgress(0);
    }

    private void refreshSliders(){
        if (tipo==0){
            sliderInteiro.setVisibility(VISIBLE);
            sliderDecimal.setVisibility(GONE);
            slider.setOnSeekBarChangeListener(listenerInteiros);
            sliderInteiro.setValueRange(minimo, maximo, true);
            sliderInteiro.setValue(minimo, true);
            quantidade.setText(minimo + "" + unidade);
        }else{
            sliderInteiro.setVisibility(GONE);
            sliderDecimal.setVisibility(VISIBLE);
            quantidade.setText(minimo+""+unidade);
            minimo *= 10;
            maximo *= 10;
            slider.setOnSeekBarChangeListener(listenerDecimais);
            sliderDecimal.setValueRange(minimo, maximo, true);
            sliderDecimal.setValue(minimo, true);
        }
        slider.setMax(maximo - minimo);
    }

    private void configureColors(){
        int color = typedArray.getColor(R.styleable.SeekBar_sb_cor, getResources().getColor(R.color.MaterialPrimary));
        sliderDecimal.setPrimaryColor(color);
        sliderInteiro.setPrimaryColor(color);
    }

    private SeekBar.OnSeekBarChangeListener listenerInteiros = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            sliderInteiro.setValue(i + minimo, true);
            quantidade.setText(i+minimo+""+unidade);
            if (onValueChangedListener!=null)
                onValueChangedListener.onValueChanged(i+minimo);
            if (!primeiroAcesso)
                salvar(i+minimo);
            primeiroAcesso = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private SeekBar.OnSeekBarChangeListener listenerDecimais = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            double progresso = ((i+minimo)*0.1);
            sliderDecimal.setValue(i + minimo, true);
            DecimalFormat df = new DecimalFormat("#.#");
            quantidade.setText(df.format(progresso)+unidade);
            if (onValueChangedListener!=null)
                onValueChangedListener.onValueChanged(progresso);
            if (!primeiroAcesso)
                salvar(progresso);
            primeiroAcesso = false;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void salvar(double progresso){
    }

    public void setOnValueChangeListener(OnValueChangedListener onValueChangeListener){
        this.onValueChangedListener = onValueChangeListener;
    }

    public void setValue(Double quantidade) {
        if (tipo==0){
            listenerInteiros.onProgressChanged(slider, quantidade.intValue()-minimo,false);
        }else{
            listenerDecimais.onProgressChanged(slider, (int)((quantidade-minimo)/0.1), false);
        }
    }

    public Double getValue(){
        if (tipo==0){
            return (double)sliderInteiro.getValue()+minimo;
        }else{
            return (sliderDecimal.getValue()+minimo)*0.1;
        }
    }

    public void setText(String text){
        quantidade.setText(text);
    }

    public void setEnabled(boolean enabled){
        slider.setEnabled(enabled);
        sliderDecimal.setEnabled(enabled);
        sliderInteiro.setEnabled(enabled);
    }

    public void setUnidade(String unidade){
        this.unidade = unidade;
        setValue(getValue());
    }

    public void setTitulo(String titulo){
        this.titulo.setText(titulo);
        this.titulo.setVisibility(VISIBLE);
    }

    public String getTitulo(){
        return titulo.getText().toString();
    }

    public void setTextSize(float textSize){
        this.titulo.setTextSize(textSize);
    }

    public void setPrimaryColor(int color){
        sliderInteiro.setPrimaryColor(color);
        sliderDecimal.setPrimaryColor(color);
    }

    public void setMinimo(int minimo){
        this.minimo = minimo;
        refreshSliders();
    }

    public void setMaximo(int maximo){
        this.maximo = maximo;
        refreshSliders();
    }

    public void setModo(SliderModo mode){
        tipo = mode.equals(SliderModo.DECIMAL)?1:0;
        refreshSliders();
    }

    public void setDiscreteMode(boolean discrete){
        sliderInteiro.setDiscreteMode(discrete);
    }

    public int getMinimo(){
        return minimo;
    }

    public int getMaximo(){
        return maximo;
    }

    public void setQuantidadeVisible(boolean isVisible){
        quantidade.setVisibility(isVisible?VISIBLE:GONE);
    }

    public interface OnValueChangedListener{
        void onValueChanged(double value);
    }

}
