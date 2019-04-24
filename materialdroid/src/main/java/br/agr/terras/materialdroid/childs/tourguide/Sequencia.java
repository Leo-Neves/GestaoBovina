package br.agr.terras.materialdroid.childs.tourguide;

import android.util.Log;
import android.view.View;

import br.agr.terras.materialdroid.Dica;

/**
 * Created by leo on 27/09/16.
 */
public class Sequencia {
    public Dica[] mDicaArray;
    public Sobreposicao mDefaultSobreposicao;
    public TextoPopup mDefaultTextoPopup;
    public Ponteiro mDefaultPonteiro;

    ContinueMethod mContinueMethod;
    boolean mDisableTargetButton;
    public int mCurrentSequence;
    Dica mParentDica;
    public enum ContinueMethod {
        Overlay, OverlayListener
    }
    private Sequencia(SequenciaBuilder builder){
        this.mDicaArray = builder.mDicaArray;
        this.mDefaultSobreposicao = builder.mDefaultSobreposicao;
        this.mDefaultTextoPopup = builder.mDefaultTextoPopup;
        this.mDefaultPonteiro = builder.mDefaultPonteiro;
        this.mContinueMethod = builder.mContinueMethod;
        this.mCurrentSequence = builder.mCurrentSequence;

        // TODO: to be implemented
        this.mDisableTargetButton = builder.mDisableTargetButton;
    }

    /**
     * sets the parent Dica that will run this Sequencia
     */
    public void setParentTourGuide(Dica parentDica){
        mParentDica = parentDica;

        if(mContinueMethod == ContinueMethod.Overlay) {
            for (final Dica dica : mDicaArray) {
                dica.mSobreposicao.mOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mParentDica.proxima();
                    }
                };
            }
        }
    }

    public Dica getNextTourGuide() {
        return mDicaArray[mCurrentSequence];
    }

    public ContinueMethod getContinueMethod() {
        return mContinueMethod;
    }

    public Dica[] getTourGuideArray() {
        return mDicaArray;
    }

    public Sobreposicao getDefaultOverlay() {
        return mDefaultSobreposicao;
    }

    public TextoPopup getDefaultToolTip() {
        return mDefaultTextoPopup;
    }

    public TextoPopup getToolTip() {
        // individual tour guide has higher priority
        if (mDicaArray[mCurrentSequence].mTextoPopup != null){
            return mDicaArray[mCurrentSequence].mTextoPopup;
        } else {
            return mDefaultTextoPopup;
        }
    }

    public Sobreposicao getOverlay() {
        // Sobreposicao is used as a method to proceed to proxima Dica, so the default overlay is already assigned appropriately if needed
        return mDicaArray[mCurrentSequence].mSobreposicao;
    }

    public Ponteiro getPointer() {
        // individual tour guide has higher priority
        if (mDicaArray[mCurrentSequence].mPonteiro != null){
            return mDicaArray[mCurrentSequence].mPonteiro;
        } else {
            return mDefaultPonteiro;
        }
    }

    public static class SequenciaBuilder {
        Dica[] mDicaArray;
        Sobreposicao mDefaultSobreposicao;
        TextoPopup mDefaultTextoPopup;
        Ponteiro mDefaultPonteiro;
        ContinueMethod mContinueMethod;
        int mCurrentSequence;
        boolean mDisableTargetButton;

        public SequenciaBuilder add(Dica... dicaArray){
            mDicaArray = dicaArray;
            return this;
        }

        public SequenciaBuilder setDefaultOverlay(Sobreposicao defaultSobreposicao){
            mDefaultSobreposicao = defaultSobreposicao;
            return this;
        }

        // This might not be useful, but who knows.. maybe someone needs it
        public SequenciaBuilder setDefaultToolTip(TextoPopup defaultTextoPopup){
            mDefaultTextoPopup = defaultTextoPopup;
            return this;
        }

        public SequenciaBuilder setDefaultPointer(Ponteiro defaultPonteiro){
            mDefaultPonteiro = defaultPonteiro;
            return this;
        }

        // TODO: this is an uncompleted feature, make it private first
        // This is intended to be used to disable the button, so people cannot click on in during a Tour, instead, people can only click on Next button or Sobreposicao to proceed
        private SequenciaBuilder setDisableButton(boolean disableTargetButton){
            mDisableTargetButton = disableTargetButton;
            return this;
        }

        /**
         * @param continueMethod ContinueMethod.Sobreposicao or ContinueMethod.OverlayListener
         *                       ContnueMethod.Sobreposicao - clicking on Sobreposicao will make Dica proceed to the proxima one.
         *                       ContinueMethod.OverlayListener - you need to provide OverlayListeners, and call tourGuideHandler.proxima() in the listener to proceed to the proxima one.
         */
        public SequenciaBuilder setContinueMethod(ContinueMethod continueMethod){
            mContinueMethod = continueMethod;
            return this;
        }

        public Sequencia build(){
            mCurrentSequence = 0;
            checkIfContinueMethodNull();
            checkAtLeastTwoTourGuideSupplied();
            checkOverlayListener(mContinueMethod);

            return new Sequencia(this);
        }
        private void checkIfContinueMethodNull(){
            if (mContinueMethod == null){
                throw new IllegalArgumentException("Continue Method is not set. Please provide ContinueMethod in setContinueMethod");
            }
        }
        private void checkAtLeastTwoTourGuideSupplied() {
            if (mDicaArray == null || mDicaArray.length <= 1){
                throw new IllegalArgumentException("In order to run a sequence, you must at least supply 2 Dica into Sequencia using add()");
            }
        }
        private void checkOverlayListener(ContinueMethod continueMethod) {
            if(continueMethod == ContinueMethod.OverlayListener){
                boolean pass = true;
                if (mDefaultSobreposicao != null && mDefaultSobreposicao.mOnClickListener != null) {
                    pass = true;
                    // when default listener is available, we loop through individual tour guide, and
                    // assign default listener to individual tour guide
                    for (Dica dica : mDicaArray) {
                        if (dica.mSobreposicao == null) {
                            dica.mSobreposicao = mDefaultSobreposicao;
                        }
                        if (dica.mSobreposicao != null && dica.mSobreposicao.mOnClickListener == null) {
                            dica.mSobreposicao.mOnClickListener = mDefaultSobreposicao.mOnClickListener;
                        }
                    }
                } else { // case where: default listener is not available

                    for (Dica dica : mDicaArray) {
                        //Both of the overlay and default listener is not null, throw the error
                        if (dica.mSobreposicao != null && dica.mSobreposicao.mOnClickListener == null) {
                            pass = false;
                            break;
                        } else if (dica.mSobreposicao == null){
                            pass = false;
                            break;
                        }
                    }

                }

                if (!pass){
                    throw new IllegalArgumentException("ContinueMethod.OverlayListener is chosen as the ContinueMethod, but no Default Sobreposicao Listener is set, or not all Sobreposicao.mListener is set for all the Dica passed in.");
                }
            } else if(continueMethod == ContinueMethod.Overlay){
                // when Sobreposicao ContinueMethod is used, listener must not be supplied (to avoid unexpected result)
                boolean pass = true;
                if (mDefaultSobreposicao != null && mDefaultSobreposicao.mOnClickListener != null) {
                    Log.e(getClass().getSimpleName(),"mDefaultSobreposicao != null && mDefaultSobreposicao.mOnClickListener != null");
                    pass = false;
                } else {
                    for (Dica dica : mDicaArray) {
                        if (dica.mSobreposicao != null && dica.mSobreposicao.mOnClickListener != null ) {
                            Log.e(getClass().getSimpleName(),"dica.mSobreposicao != null && dica.mSobreposicao.mOnClickListener != null");
                            pass = false;
                            break;
                        }
                    }
                }
                if (mDefaultSobreposicao != null) {
                    for (Dica dica : mDicaArray) {
                        if (dica.mSobreposicao == null) {
                            dica.mSobreposicao = mDefaultSobreposicao;
                        }
                    }
                }

                if (!pass) {
                    throw new IllegalArgumentException("ContinueMethod.Sobreposicao is chosen as the ContinueMethod, but either default overlay listener is still set OR individual overlay listener is still set, make sure to clear all Sobreposicao listener");
                }
            }
        }
    }
}
