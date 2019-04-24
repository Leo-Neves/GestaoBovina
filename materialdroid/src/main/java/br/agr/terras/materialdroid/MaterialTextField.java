package br.agr.terras.materialdroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by florentchampigny on 27/08/15.
 */
public class MaterialTextField extends FrameLayout {


    protected TextView label;
    protected View card;
    protected ImageView image;
    protected EditText editText;
    protected ViewGroup editTextLayout;

    protected int labelTopMargin = -1;
    protected boolean expanded = false;

    protected int ANIMATION_DURATION = -1;
    protected boolean OPEN_KEYBOARD_ON_FOCUS = true;
    protected int labelColor = -1;
    protected int imageDrawableId = -1;
    protected int cardCollapsedHeight = -1;

    public MaterialTextField(Context context) {
        super(context);
    }

    public MaterialTextField(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttributes(context,attrs);
    }

    public MaterialTextField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleAttributes(context, attrs);
    }

    public void toggle() {
        if (expanded) {
            reduce();
        } else {
            expand();
        }
    }

    public void reduce() {
        if (expanded) {

            final int heightInitial = getContext().getResources().getDimensionPixelOffset(R.dimen.mtf_cardHeight_final);
            ViewCompat.animate(label)
                    .alpha(1)
                    .scaleX(1)
                    .scaleY(1)
                    .translationY(0)
                    .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(image)
                    .alpha(0)
                    .scaleX(0.4f)
                    .scaleY(0.4f)
                    .setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(View view) {
                            float value = ViewCompat.getAlpha(view); //percentage
                            card.getLayoutParams().height = (int) (value * (heightInitial - cardCollapsedHeight) + cardCollapsedHeight);
                            card.requestLayout();
                        }
                    })
                    .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(editText)
                    .alpha(0)
                    .setDuration(ANIMATION_DURATION);

            if (OPEN_KEYBOARD_ON_FOCUS) {
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
            editText.clearFocus();

            expanded = false;
        }
    }

    public void expand() {
        if (!expanded) {
            final int height = getContext().getResources().getDimensionPixelOffset(R.dimen.mtf_cardHeight_final);

            ViewCompat.animate(editText)
                    .alpha(1f)
                    .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(label)
                    .alpha(0.4f)
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .translationY(-labelTopMargin)
                    .setDuration(ANIMATION_DURATION);

            ViewCompat.animate(image)
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(View view) {
                            float value = ViewCompat.getAlpha(view); //percentage
                            card.getLayoutParams().height = (int) (value * height);
                            card.requestLayout();
                        }
                    })
                    .setDuration(ANIMATION_DURATION);

            editText.requestFocus();
            if (OPEN_KEYBOARD_ON_FOCUS) {
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }

            expanded = true;
        }
    }

    public View getCard() {
        return card;
    }

    public TextView getLabel() {
        return label;
    }

    public ImageView getImage() {
        return image;
    }

    public EditText getEditText() {
        return editText;
    }

    public ViewGroup getEditTextLayout() {
        return editTextLayout;
    }

    public boolean isExpanded() {
        return expanded;
    }

    protected void handleAttributes(Context context, AttributeSet attrs) {
        try {
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaterialTextField);

            {
                ANIMATION_DURATION = styledAttrs.getInteger(R.styleable.MaterialTextField_mtf_animationDuration, 400);
            }
            {
                OPEN_KEYBOARD_ON_FOCUS = styledAttrs.getBoolean(R.styleable.MaterialTextField_mtf_openKeyboardOnFocus, false);
            }
            {
                labelColor = styledAttrs.getColor(R.styleable.MaterialTextField_mtf_labelColor, -1);
            }
            {
                imageDrawableId = styledAttrs.getResourceId(R.styleable.MaterialTextField_mtf_image, -1);
            }
            {
                cardCollapsedHeight = styledAttrs.getDimensionPixelOffset(R.styleable.MaterialTextField_mtf_cardCollapsedHeight, context.getResources().getDimensionPixelOffset(R.dimen.mtf_cardHeight_initial));
            }

            styledAttrs.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected EditText findEditTextChild() {
        if (getChildCount() > 0 && getChildAt(0) instanceof EditText) {
            return (EditText) getChildAt(0);
        }
        return null;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        editText = findEditTextChild();
        if (editText == null) {
            return;
        }

        addView(LayoutInflater.from(getContext()).inflate(R.layout.mtf_layout, this, false));

        editTextLayout = (ViewGroup) findViewById(R.id.mtf_editTextLayout);
        removeView(editText);
        editTextLayout.addView(editText);

        label = (TextView) findViewById(R.id.mtf_label);
        ViewCompat.setPivotX(label, 0);
        ViewCompat.setPivotY(label, 0);

        if (editText.getHint() != null) {
            label.setText(editText.getHint());
            editText.setHint("");
        }

        card = findViewById(R.id.mtf_card);
        card.getLayoutParams().height = cardCollapsedHeight;
        card.requestLayout();

        image = (ImageView) findViewById(R.id.mtf_image);
        ViewCompat.setAlpha((View) image, 0);
        ViewCompat.setScaleX(image, 0.4f);
        ViewCompat.setScaleY(image, 0.4f);

        ViewCompat.setAlpha(editText, 0f);
        editText.setBackgroundColor(Color.TRANSPARENT);

        labelTopMargin = LayoutParams.class.cast(label.getLayoutParams()).topMargin;

        customizeFromAttributes();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

    }

    protected void customizeFromAttributes() {
        if (labelColor != -1) {
            this.label.setTextColor(labelColor);
        }
        if (imageDrawableId != -1) {
            this.image.setImageDrawable(getContext().getResources().getDrawable(imageDrawableId));
        }
    }

}
