package br.agr.terras.materialdroid;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.agr.terras.materialdroid.childs.tourguide.FloatingButton;
import br.agr.terras.materialdroid.childs.tourguide.FrameLayoutWithHole;
import br.agr.terras.materialdroid.childs.tourguide.Ponteiro;
import br.agr.terras.materialdroid.childs.tourguide.Sequencia;
import br.agr.terras.materialdroid.childs.tourguide.Sobreposicao;
import br.agr.terras.materialdroid.childs.tourguide.TextoPopup;
import br.agr.terras.materialdroid.utils.ThemeUtil;

/**
 * Created by leo on 27/09/16.
 */
public class Dica {
    /**
     * This describes the animation techniques
     */
    public enum Tecnica {
        Click, HorizontalLeft, HorizontalRight, VerticalUpward, VerticalDownward
    }

    /**
     * This describes the allowable motion, for example if you want the users to learn about clicking, but want to stop them from swiping, then use ClickOnly
     */
    public enum MotionType {
        AllowAll, ClickOnly, SwipeOnly
    }

    public Tecnica mTecnica;
    public View mHighlightedView;
    public Context mContext;
    public Window mWindow;
    public MotionType mMotionType;
    public FrameLayoutWithHole mFrameLayout;
    public View mToolTipViewGroup;
    public TextoPopup mTextoPopup;
    public Ponteiro mPonteiro;
    public Sobreposicao mSobreposicao;

    private Sequencia mSequencia;

    public static final String TAG = Dica.class.getSimpleName();
    public static boolean DEBUG = true;

    /*************
     * Public API
     *************/

    /* Static builder */
    public static Dica init(Activity activity) {
        return new Dica(activity, activity.getWindow());
    }

    /* Constructor */
    public Dica(Context context, Window window) {
        mContext = context;
        mWindow = window;
    }

    /**
     * Setter for the animation to be used
     *
     * @param tecnica Animation to be used
     * @return return Dica instance for chaining purpose
     */
    public Dica com(Tecnica tecnica) {
        mTecnica = tecnica;
        return this;
    }

    /**
     * Sets which motion type is motionType
     *
     * @param motionType
     * @return return Dica instance for chaining purpose
     */
    public Dica motionType(MotionType motionType) {
        mMotionType = motionType;
        return this;
    }

    /**
     * Sets the duration
     *
     * @param view the view in which the tutorial button will be placed on top of
     * @return return Dica instance for chaining purpose
     */
    public Dica executarEm(View view) {
        mHighlightedView = view;
        setupView();
        return this;
    }

    /**
     * Sets the sobreposicao
     *
     * @param sobreposicao this sobreposicao object should contain the attributes of the sobreposicao, such as background color, animation, Style, etc
     * @return return Dica instance for chaining purpose
     */
    public Dica setSobreposicao(Sobreposicao sobreposicao) {
        mSobreposicao = sobreposicao;
        return this;
    }

    /**
     * Set the textoPopup
     *
     * @param textoPopup this textoPopup object should contain the attributes of the TextoPopup, such as, the title text, and the description text, background color, etc
     * @return return Dica instance for chaining purpose
     */
    public Dica setTextoPopup(TextoPopup textoPopup) {
        mTextoPopup = textoPopup;
        return this;
    }

    /**
     * Set the Ponteiro
     *
     * @param ponteiro this ponteiro object should contain the attributes of the Ponteiro, such as the ponteiro color, ponteiro gravity, etc, refer to @Link{ponteiro}
     * @return return Dica instance for chaining purpose
     */
    public Dica setPonteiro(Ponteiro ponteiro) {
        mPonteiro = ponteiro;
        return this;
    }

    /**
     * Clean up the tutorial that is added to the activity
     */
    public void limpar() {
        if (mFrameLayout != null)
            mFrameLayout.cleanUp();

        if (mToolTipViewGroup != null && mWindow != null)
            ((ViewGroup) mWindow.getDecorView()).removeView(mToolTipViewGroup);
    }

    public Dica executarDepois(View view) {
        mHighlightedView = view;
        return this;
    }

    /**************************
     * Sequencia related method
     **************************/

    public Dica executarEmSequencia(Sequencia sequencia) {
        setSequencia(sequencia);
        proxima();
        return this;
    }

    public Dica setSequencia(Sequencia sequencia) {
        mSequencia = sequencia;
        mSequencia.setParentTourGuide(this);
        for (Dica dica : sequencia.mDicaArray) {
            if (dica.mHighlightedView == null) {
                throw new NullPointerException("Especifique uma view usando o método 'executarDepois'");
            }
        }
        return this;
    }

    public Dica proxima() {
        if (mFrameLayout != null) {
            limpar();
        }

        if (mSequencia.mCurrentSequence < mSequencia.mDicaArray.length) {
            setTextoPopup(mSequencia.getToolTip());
            setPonteiro(mSequencia.getPointer());
            setSobreposicao(mSequencia.getOverlay());

            mHighlightedView = mSequencia.getNextTourGuide().mHighlightedView;

            setupView();
            mSequencia.mCurrentSequence++;
        }
        return this;
    }

    /**
     * @return FrameLayoutWithHole that is used as overlay
     */
    public FrameLayoutWithHole getOverlay() {
        return mFrameLayout;
    }

    /**
     * @return the TextoPopup container View
     */
    public View getToolTip() {
        return mToolTipViewGroup;
    }

    /******
     * Private methods
     *******/
    //TODO: move into Ponteiro
    private int getXBasedOnGravity(int width) {
        int[] pos = new int[2];
        mHighlightedView.getLocationOnScreen(pos);
        int x = pos[0];
        if ((mPonteiro.gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            return x + mHighlightedView.getWidth() - width;
        } else if ((mPonteiro.gravity & Gravity.LEFT) == Gravity.LEFT) {
            return x;
        } else { // this is center
            return x + mHighlightedView.getWidth() / 2 - width / 2;
        }
    }

    //TODO: move into Ponteiro
    private int getYBasedOnGravity(int height) {
        int[] pos = new int[2];
        mHighlightedView.getLocationInWindow(pos);
        int y = pos[1];
        if ((mPonteiro.gravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
            return y + mHighlightedView.getHeight() - height;
        } else if ((mPonteiro.gravity & Gravity.TOP) == Gravity.TOP) {
            return y;
        } else { // this is center
            return y + mHighlightedView.getHeight() / 2 - height / 2;
        }
    }

    protected void setupView() {
//        TODO: throw exception if either mActivity, mDuration, mHighlightedView is null
        checking();
        final ViewTreeObserver viewTreeObserver = mHighlightedView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // make sure this only run once
                mHighlightedView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                /* Initialize a frame layout com a hole */
                mFrameLayout = new FrameLayoutWithHole(mContext, mHighlightedView, mMotionType, mSobreposicao);
                /* handle click disable */
                handleDisableClicking(mFrameLayout);

                /* setup floating action button */
                if (mPonteiro != null) {
                    View fab = setupAndAddFABToFrameLayout(mFrameLayout);
                    performAnimationOn(fab);
                }
                setupFrameLayout();
                /* setup tooltip view */
                setupToolTip();
            }
        });
    }

    private void checking() {
        // There is not check for tooltip because tooltip can be null, it means there no tooltip will be shown

    }

    private void handleDisableClicking(FrameLayoutWithHole frameLayoutWithHole) {
        // 1. if user provides an overlay listener, use that as 1st priority
        if (mSobreposicao != null && mSobreposicao.mOnClickListener != null) {
            frameLayoutWithHole.setViewHole(mHighlightedView);
            frameLayoutWithHole.setClickable(true);
            frameLayoutWithHole.setOnClickListener(mSobreposicao.mOnClickListener);
        }
        // 2. if overlay listener is not provided, check if it's disabled
        else if (mSobreposicao != null && mSobreposicao.mDisableClick) {
            Log.w(getClass().getSimpleName(), "O OnClickListener de 'Sobreposicao' é nulo, portanto quando clicar na dica irá executar o método 'proxima'");
            frameLayoutWithHole.setViewHole(mHighlightedView);
            frameLayoutWithHole.setSoundEffectsEnabled(false);
            frameLayoutWithHole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                } // do nothing, disabled.
            });
        }
    }

    private void setupToolTip() {
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        if (mTextoPopup != null) {
            /* inflate and get views */
            ViewGroup parent = (ViewGroup) mWindow.getDecorView();
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            mToolTipViewGroup = layoutInflater.inflate(R.layout.tourguide_tooltip, null);
            View toolTipContainer = mToolTipViewGroup.findViewById(R.id.tourguide_ll_container);
            TextView toolTipTitleTV = (TextView) mToolTipViewGroup.findViewById(R.id.tourguide_tv_title);
            TextView toolTipDescriptionTV = (TextView) mToolTipViewGroup.findViewById(R.id.tourguide_tv_desc);

            /* set tooltip attributes */
            toolTipContainer.setBackgroundColor(mTextoPopup.mBackgroundColor);
            if (mTextoPopup.mTitle == null) {
                toolTipTitleTV.setVisibility(View.GONE);
            } else {
                toolTipTitleTV.setText(mTextoPopup.mTitle);
            }
            if (mTextoPopup.mDescription == null) {
                toolTipDescriptionTV.setVisibility(View.GONE);
            } else {
                toolTipDescriptionTV.setText(mTextoPopup.mDescription);
            }


            mToolTipViewGroup.startAnimation(mTextoPopup.mEnterAnimation);

            /* add setShadow if it's turned on */
            if (mTextoPopup.mShadow) {
                mToolTipViewGroup.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.drop_shadow));
            }

            /* position and size calculation */
            int[] pos = new int[2];
            mHighlightedView.getLocationOnScreen(pos);
            int targetViewX = pos[0];
            final int targetViewY = pos[1];

            // get measured size of tooltip
            mToolTipViewGroup.measure(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            int toolTipMeasuredWidth = mToolTipViewGroup.getMeasuredWidth();
            int toolTipMeasuredHeight = mToolTipViewGroup.getMeasuredHeight();

            Point resultPoint = new Point(); // this holds the final position of tooltip
            float density = mContext.getResources().getDisplayMetrics().density;
            final float adjustment = 10 * density; //adjustment is that little overlapping area of tooltip and targeted button

            // calculate x position, based on gravity, tooltipMeasuredWidth, parent max width, x position of target view, adjustment
            if (toolTipMeasuredWidth > parent.getWidth()) {
                resultPoint.x = getXForTooTip(mTextoPopup.mGravity, parent.getWidth(), targetViewX, adjustment);
            } else {
                resultPoint.x = getXForTooTip(mTextoPopup.mGravity, toolTipMeasuredWidth, targetViewX, adjustment);
            }

            resultPoint.y = getYForTooTip(mTextoPopup.mGravity, toolTipMeasuredHeight, targetViewY, adjustment);

            // add view to parent
//            ((ViewGroup) mActivity.getWindow().getDecorView().findViewById(android.R.id.content)).addView(mToolTipViewGroup, layoutParams);
            parent.addView(mToolTipViewGroup, layoutParams);

            // 1. width < screen check
            if (toolTipMeasuredWidth > parent.getWidth()) {
                mToolTipViewGroup.getLayoutParams().width = parent.getWidth();
                toolTipMeasuredWidth = parent.getWidth();
            }
            // 2. x left boundary check
            if (resultPoint.x < 0) {
                mToolTipViewGroup.getLayoutParams().width = toolTipMeasuredWidth + resultPoint.x; //since point.x is negative, use plus
                resultPoint.x = 0;
            }
            // 3. x right boundary check
            int tempRightX = resultPoint.x + toolTipMeasuredWidth;
            if (tempRightX > parent.getWidth()) {
                mToolTipViewGroup.getLayoutParams().width = parent.getWidth() - resultPoint.x; //since point.x is negative, use plus
            }

            // pass toolTip onClickListener into toolTipViewGroup
            if (mTextoPopup.mOnClickListener != null) {
                mToolTipViewGroup.setOnClickListener(mTextoPopup.mOnClickListener);
            }
            if (mTextoPopup.dimensionUnitWidth != null && mTextoPopup.width != -1) {
                Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                if (mTextoPopup.dimensionUnitWidth.equals(TextoPopup.DimensionUnit.PERCENT))
                    width = (int) (width * (mTextoPopup.width / 100.0));
                if (mTextoPopup.dimensionUnitWidth.equals(TextoPopup.DimensionUnit.DP))
                    width = (mTextoPopup.width);
                mToolTipViewGroup.getLayoutParams().width = width;

            }
            if (mTextoPopup.dimensionUnitHeight != null && mTextoPopup.height != -1) {
                Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height = size.y;
                if (mTextoPopup.dimensionUnitHeight.equals(TextoPopup.DimensionUnit.PERCENT))
                    height = (int) (height * (mTextoPopup.height / 100.0));
                if (mTextoPopup.dimensionUnitHeight.equals(TextoPopup.DimensionUnit.DP))
                    height = (mTextoPopup.height);
                mToolTipViewGroup.getLayoutParams().height = height;

            }
            // TODO: no boundary check for height yet, this is a unlikely case though
            // height boundary can be fixed by user changing the gravity to the other size, since there are plenty of space vertically compared to horizontally

            // this needs an viewTreeObserver, that's because TextView measurement of it's vertical height is not accurate (didn't take into account of multiple lines yet) before it's rendered
            // re-calculate height again once it's rendered
            final ViewTreeObserver viewTreeObserver = mToolTipViewGroup.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mToolTipViewGroup.getViewTreeObserver().removeGlobalOnLayoutListener(this);// make sure this only run once

                    int fixedY;
                    int toolTipHeightAfterLayouted = mToolTipViewGroup.getHeight();
                    fixedY = getYForTooTip(mTextoPopup.mGravity, toolTipHeightAfterLayouted, targetViewY, adjustment);
                    layoutParams.setMargins((int) mToolTipViewGroup.getX(), fixedY, 0, 0);
                }
            });

            // set the position using setMargins on the left and top
            layoutParams.setMargins(resultPoint.x, resultPoint.y, 0, 0);
        }

    }

    private int getXForTooTip(int gravity, int toolTipMeasuredWidth, int targetViewX, float adjustment) {
        int x;
        if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            x = targetViewX - toolTipMeasuredWidth + (int) adjustment;
        } else if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            x = targetViewX + mHighlightedView.getWidth() - (int) adjustment;
        } else {
            x = targetViewX + mHighlightedView.getWidth() / 2 - toolTipMeasuredWidth / 2;
        }
        return x;
    }

    private int getYForTooTip(int gravity, int toolTipMeasuredHeight, int targetViewY, float adjustment) {
        int y;
        if ((gravity & Gravity.TOP) == Gravity.TOP) {

            if (((gravity & Gravity.LEFT) == Gravity.LEFT) || ((gravity & Gravity.RIGHT) == Gravity.RIGHT)) {
                y = targetViewY - toolTipMeasuredHeight + (int) adjustment;
            } else {
                y = targetViewY - toolTipMeasuredHeight - (int) adjustment;
            }
        } else { // this is center
            if (((gravity & Gravity.LEFT) == Gravity.LEFT) || ((gravity & Gravity.RIGHT) == Gravity.RIGHT)) {
                y = targetViewY + mHighlightedView.getHeight() - (int) adjustment;
            } else {
                y = targetViewY + mHighlightedView.getHeight() + (int) adjustment;
            }
        }
        return y;
    }

    private View setupAndAddFABToFrameLayout(final FrameLayoutWithHole frameLayoutWithHole) {
        final FloatingButton fab = new FloatingButton(mContext);
        final int radius = ThemeUtil.dpToPx(mContext, 20);
        fab.setBackgroundColor(mPonteiro.color);
        fab.setRadius(radius);
        fab.setIcon(new ColorDrawable(0x00000000), false);
        fab.setClickable(false);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        frameLayoutWithHole.addView(fab, params);

        final ViewTreeObserver viewTreeObserver = fab.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // make sure this only run once
                fab.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                fab.updateLocation(getXBasedOnGravity(radius * 2), getYBasedOnGravity(radius * 2), Gravity.TOP | Gravity.LEFT);
            }
        });

        return fab;
    }

    private void setupFrameLayout() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        ViewGroup contentArea = (ViewGroup) mWindow.getDecorView().findViewById(android.R.id.content);
        int[] pos = new int[2];
        contentArea.getLocationOnScreen(pos);
        // frameLayoutWithHole's coordinates are calculated taking full screen height into account
        // but we're adding it to the content area only, so we need to offset it to the same Y value of contentArea

        layoutParams.setMargins(0, -pos[1], 0, 0);
        contentArea.addView(mFrameLayout, layoutParams);
    }

    private void performAnimationOn(final View view) {

        if (mTecnica != null && mTecnica == Tecnica.HorizontalLeft) {

            final AnimatorSet animatorSet = new AnimatorSet();
            final AnimatorSet animatorSet2 = new AnimatorSet();
            Animator.AnimatorListener lis1 = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    view.setScaleX(1f);
                    view.setScaleY(1f);
                    view.setTranslationX(0);
                    animatorSet2.start();
                }
            };
            Animator.AnimatorListener lis2 = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    view.setScaleX(1f);
                    view.setScaleY(1f);
                    view.setTranslationX(0);
                    animatorSet.start();
                }
            };

            long fadeInDuration = 800;
            long scaleDownDuration = 800;
            long goLeftXDuration = 2000;
            long fadeOutDuration = goLeftXDuration;
            float translationX = getScreenWidth() / 2;

            final ValueAnimator fadeInAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            fadeInAnim.setDuration(fadeInDuration);
            final ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.85f);
            scaleDownX.setDuration(scaleDownDuration);
            final ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.85f);
            scaleDownY.setDuration(scaleDownDuration);
            final ObjectAnimator goLeftX = ObjectAnimator.ofFloat(view, "translationX", -translationX);
            goLeftX.setDuration(goLeftXDuration);
            final ValueAnimator fadeOutAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            fadeOutAnim.setDuration(fadeOutDuration);

            final ValueAnimator fadeInAnim2 = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            fadeInAnim2.setDuration(fadeInDuration);
            final ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.85f);
            scaleDownX2.setDuration(scaleDownDuration);
            final ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.85f);
            scaleDownY2.setDuration(scaleDownDuration);
            final ObjectAnimator goLeftX2 = ObjectAnimator.ofFloat(view, "translationX", -translationX);
            goLeftX2.setDuration(goLeftXDuration);
            final ValueAnimator fadeOutAnim2 = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            fadeOutAnim2.setDuration(fadeOutDuration);

            animatorSet.play(fadeInAnim);
            animatorSet.play(scaleDownX).with(scaleDownY).after(fadeInAnim);
            animatorSet.play(goLeftX).with(fadeOutAnim).after(scaleDownY);

            animatorSet2.play(fadeInAnim2);
            animatorSet2.play(scaleDownX2).with(scaleDownY2).after(fadeInAnim2);
            animatorSet2.play(goLeftX2).with(fadeOutAnim2).after(scaleDownY2);

            animatorSet.addListener(lis1);
            animatorSet2.addListener(lis2);
            animatorSet.start();

            /* these animatorSets are kept track in FrameLayout, so that they can be cleaned up when FrameLayout is detached from window */
            mFrameLayout.addAnimatorSet(animatorSet);
            mFrameLayout.addAnimatorSet(animatorSet2);
        } else if (mTecnica != null && mTecnica == Tecnica.HorizontalRight) {

        } else if (mTecnica != null && mTecnica == Tecnica.VerticalUpward) {

        } else if (mTecnica != null && mTecnica == Tecnica.VerticalDownward) {

        } else { // do click for default case
            final AnimatorSet animatorSet = new AnimatorSet();
            final AnimatorSet animatorSet2 = new AnimatorSet();
            Animator.AnimatorListener lis1 = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    view.setScaleX(1f);
                    view.setScaleY(1f);
                    view.setTranslationX(0);
                    animatorSet2.start();
                }
            };
            Animator.AnimatorListener lis2 = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    view.setScaleX(1f);
                    view.setScaleY(1f);
                    view.setTranslationX(0);
                    animatorSet.start();
                }
            };

            long fadeInDuration = 800;
            long scaleDownDuration = 800;
            long fadeOutDuration = 800;
            long delay = 1000;

            final ValueAnimator delayAnim = ObjectAnimator.ofFloat(view, "translationX", 0);
            delayAnim.setDuration(delay);
            final ValueAnimator fadeInAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            fadeInAnim.setDuration(fadeInDuration);
            final ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.85f);
            scaleDownX.setDuration(scaleDownDuration);
            final ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.85f);
            scaleDownY.setDuration(scaleDownDuration);
            final ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 0.85f, 1f);
            scaleUpX.setDuration(scaleDownDuration);
            final ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.85f, 1f);
            scaleUpY.setDuration(scaleDownDuration);
            final ValueAnimator fadeOutAnim = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            fadeOutAnim.setDuration(fadeOutDuration);

            final ValueAnimator delayAnim2 = ObjectAnimator.ofFloat(view, "translationX", 0);
            delayAnim2.setDuration(delay);
            final ValueAnimator fadeInAnim2 = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
            fadeInAnim2.setDuration(fadeInDuration);
            final ObjectAnimator scaleDownX2 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.85f);
            scaleDownX2.setDuration(scaleDownDuration);
            final ObjectAnimator scaleDownY2 = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.85f);
            scaleDownY2.setDuration(scaleDownDuration);
            final ObjectAnimator scaleUpX2 = ObjectAnimator.ofFloat(view, "scaleX", 0.85f, 1f);
            scaleUpX2.setDuration(scaleDownDuration);
            final ObjectAnimator scaleUpY2 = ObjectAnimator.ofFloat(view, "scaleY", 0.85f, 1f);
            scaleUpY2.setDuration(scaleDownDuration);
            final ValueAnimator fadeOutAnim2 = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            fadeOutAnim2.setDuration(fadeOutDuration);
            view.setAlpha(0);
            animatorSet.setStartDelay(mTextoPopup != null ? mTextoPopup.mEnterAnimation.getDuration() : 0);
            animatorSet.play(fadeInAnim);
            animatorSet.play(scaleDownX).with(scaleDownY).after(fadeInAnim);
            animatorSet.play(scaleUpX).with(scaleUpY).with(fadeOutAnim).after(scaleDownY);
            animatorSet.play(delayAnim).after(scaleUpY);

            animatorSet2.play(fadeInAnim2);
            animatorSet2.play(scaleDownX2).with(scaleDownY2).after(fadeInAnim2);
            animatorSet2.play(scaleUpX2).with(scaleUpY2).with(fadeOutAnim2).after(scaleDownY2);
            animatorSet2.play(delayAnim2).after(scaleUpY2);

            animatorSet.addListener(lis1);
            animatorSet2.addListener(lis2);
            animatorSet.start();

            /* these animatorSets are kept track in FrameLayout, so that they can be cleaned up when FrameLayout is detached from window */
            mFrameLayout.addAnimatorSet(animatorSet);
            mFrameLayout.addAnimatorSet(animatorSet2);
        }
    }

    private int getScreenWidth() {
        if (mContext != null) {
            return mContext.getResources().getDisplayMetrics().widthPixels;
        } else {
            return 0;
        }
    }

}
