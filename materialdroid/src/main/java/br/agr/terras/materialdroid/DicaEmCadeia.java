package br.agr.terras.materialdroid;

import android.app.Activity;
import android.view.View;

import br.agr.terras.materialdroid.childs.tourguide.Ponteiro;
import br.agr.terras.materialdroid.childs.tourguide.Sequencia;
import br.agr.terras.materialdroid.childs.tourguide.Sobreposicao;
import br.agr.terras.materialdroid.childs.tourguide.TextoPopup;

/**
 * Created by leo on 27/09/16.
 */
public class DicaEmCadeia extends Dica {
    private Sequencia mSequencia;

    public DicaEmCadeia(Activity activity) {
        super(activity, activity.getWindow());
    }

    /* Static builder */
    public static DicaEmCadeia init(Activity activity){
        return new DicaEmCadeia(activity);
    }

    @Override
    public DicaEmCadeia executarEm(View targetView) {
        throw new RuntimeException("executarEm() não deve ser chamado por DicaEmCadeia, DicaEmCadeia deve ser usado somente com Sequencia. Use a classe Dica para executarEm() uma simples Dica. Apenas use DicaEmCadeia se você pretende executar Dica em sequencia.");
    }

    public DicaEmCadeia executarDepois(View view){
        mHighlightedView = view;
        return this;
    }

    @Override
    public DicaEmCadeia com(Tecnica Tecnica) {
        return (DicaEmCadeia)super.com(Tecnica);
    }

    @Override
    public DicaEmCadeia motionType(MotionType motionType) {
        return (DicaEmCadeia)super.motionType(motionType);
    }

    @Override
    public DicaEmCadeia setSobreposicao(Sobreposicao Sobreposicao) {
        return (DicaEmCadeia)super.setSobreposicao(Sobreposicao);
    }

    @Override
    public DicaEmCadeia setTextoPopup(TextoPopup TextoPopup) {
        return (DicaEmCadeia)super.setTextoPopup(TextoPopup);
    }

    @Override
    public DicaEmCadeia setPonteiro(Ponteiro Ponteiro) {
        return (DicaEmCadeia)super.setPonteiro(Ponteiro);
    }

    public DicaEmCadeia proxima(){
        if (mFrameLayout!=null) {
            limpar();
        }

        if (mSequencia.mCurrentSequence< mSequencia.mDicaArray.length) {
            setTextoPopup(mSequencia.getToolTip());
            setPonteiro(mSequencia.getPointer());
            setSobreposicao(mSequencia.getOverlay());

            mHighlightedView = mSequencia.getNextTourGuide().mHighlightedView;

            setupView();
            mSequencia.mCurrentSequence++;
        }
        return this;
    }

    /**************************
     * Sequencia related method
     **************************/

    public DicaEmCadeia executarEmSequencia(Sequencia Sequencia){
        setSequencia(Sequencia);
        proxima();
        return this;
    }

    public DicaEmCadeia setSequencia(Sequencia Sequencia){
        mSequencia = Sequencia;
        mSequencia.setParentTourGuide(this);
        for (Dica dica : Sequencia.mDicaArray){
            if (dica.mHighlightedView == null) {
                throw new NullPointerException("Especifique uma view usando o método 'executarDepois'");
            }
        }
        return this;
    }
}