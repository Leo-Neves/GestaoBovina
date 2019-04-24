package br.agr.terras.materialdroid;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import br.agr.terras.materialdroid.childs.SnackBarLayout;

/**
 * Created by leo on 13/04/16.
 */
public class SnackBar {

    private SnackBarLayout snackBar;
    private View view;
    private String mensagem;
    public static final String COLOR_BACKGROUND_RED = "#8f0011";
    public static final String COLOR_BACKGROUND_GREEN = "#316431";
    public static final String COLOR_BACKGROUND_BLUE = "#4198d9";
    public static final String FECHAR = "Fechar";
    private float size = 18;
    private Integer time = 2000;
    boolean error = false;

    public SnackBar(View view) {
        init(view, null, COLOR_BACKGROUND_BLUE, null, null);
    }

    public SnackBar(View view, String mensagem, String color) {
        init(view, mensagem, color, null, null);
    }

    public SnackBar(View view, String mensagem, String color, View.OnClickListener onClickListener, String buttonText) {
        init(view, mensagem, color, onClickListener, buttonText);
    }

    public void show() {
        snackBar.show();
    }

    public void dismiss(){
        snackBar.dismiss();
    }

    private void init(View view, String mensagem, String color, View.OnClickListener onClickListener, String buttonText) {
        this.view = view;
        this.mensagem = mensagem;
        snackBar = SnackBarLayout.make(view, mensagem, SnackBarLayout.LENGTH_LONG);
        if (onClickListener != null) {
            snackBar.setAction(buttonText != null ? buttonText : FECHAR, onClickListener);
            snackBar.setActionTextColor(Color.WHITE);
        }
        snackBar.setTextSize(15);
        if (color.equals(COLOR_BACKGROUND_RED))
            snackBar.getView().setBackgroundColor(Color.parseColor(COLOR_BACKGROUND_RED));
        else if (color.equals(COLOR_BACKGROUND_GREEN))
            snackBar.getView().setBackgroundColor(Color.parseColor(COLOR_BACKGROUND_GREEN));
        else if (color.equals(COLOR_BACKGROUND_BLUE))
            snackBar.getView().setBackgroundColor(Color.parseColor(COLOR_BACKGROUND_BLUE));
    }

    public SnackBar setDuration(int miliseconds) {
        snackBar.setDuration(miliseconds);
        return this;
    }

    public SnackBar setMensagem(String mensagem) {
        snackBar.setText(mensagem);
        return this;
    }

    public SnackBar setColor(String color) {
        if (color.equals(COLOR_BACKGROUND_RED))
            snackBar.getView().setBackgroundColor(Color.parseColor(COLOR_BACKGROUND_RED));
        else if (color.equals(COLOR_BACKGROUND_GREEN))
            snackBar.getView().setBackgroundColor(Color.parseColor(COLOR_BACKGROUND_GREEN));
        else if (color.equals(COLOR_BACKGROUND_BLUE))
            snackBar.getView().setBackgroundColor(Color.parseColor(COLOR_BACKGROUND_BLUE));
        return this;
    }

    public SnackBar setAction(final OnActionListener onActionListener, String buttonText) {
        snackBar.setAction(buttonText != null ? buttonText : FECHAR, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionListener.onClick(SnackBar.this);
            }
        });
        snackBar.setActionTextColor(Color.WHITE);
        return this;
    }

    public SnackBarLayout getLayout(){
        return snackBar;
    }

    private class SnackBarView extends android.app.Dialog {

        String text;
        float textSize = 14;//Roboto Regular 14sp
        String buttonText;
        View.OnClickListener onClickListener;
        Activity activity;
        View view;
        ButtonFlat button;
        int backgroundSnackBar = Color.parseColor("#333333");
        int backgroundButton = Color.parseColor("#1E88E5");

        OnHideListener onHideListener;
        // Timer
        private boolean mIndeterminate = false;
        private int mTimer = 7 * 1000;

        // With action button
        public SnackBarView(Activity activity, String text, String buttonText, View.OnClickListener onClickListener) {
            super(activity, android.R.style.Theme_Translucent);
            this.activity = activity;
            this.text = text;
            this.buttonText = buttonText;
            this.onClickListener = onClickListener;
        }

        // Only text
        public SnackBarView(Activity activity, String text) {
            super(activity, android.R.style.Theme_Translucent);
            this.activity = activity;
            this.text = text;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.snackbar);
            setCanceledOnTouchOutside(false);
            ((TextView) findViewById(R.id.text)).setText(text);
            ((TextView) findViewById(R.id.text)).setTextSize(textSize); //set textSize
            button = (ButtonFlat) findViewById(R.id.buttonflat);
            if (text == null || onClickListener == null) {
                button.setVisibility(View.GONE);
            } else {
                button.setText(buttonText);
                button.setBackgroundColor(backgroundButton);

                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dismiss();
                        onClickListener.onClick(v);
                    }
                });
            }
            view = findViewById(R.id.snackbar);
            view.setBackgroundColor(backgroundSnackBar);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return activity.dispatchTouchEvent(event);
        }

        @Override
        public void onBackPressed() {
        }

        @Override
        public void show() {
            super.show();
            view.setVisibility(View.VISIBLE);
            view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.snackbar_show_animation));
            if (!mIndeterminate) {
                dismissTimer.start();
            }
        }

        // Dismiss timer
        Thread dismissTimer = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(mTimer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(new Message());
            }
        });

        Handler handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                if (onHideListener != null) {
                    onHideListener.onHide();
                }
                dismiss();
                return false;
            }
        });

        /**
         * @author Jack Tony
         */
        @Override
        public void dismiss() {
            Animation anim = AnimationUtils.loadAnimation(activity, R.anim.snackbar_hide_animation);
            anim.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    SnackBarView.super.dismiss();
                }
            });
            view.startAnimation(anim);
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            // TODO 自动生成的方法存根
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss();
            }
            return super.onKeyDown(keyCode, event);
        }

        public void setMessageTextSize(float size) {
            textSize = size;
        }

        public void setIndeterminate(boolean indeterminate) {
            mIndeterminate = indeterminate;
        }

        public boolean isIndeterminate() {
            return mIndeterminate;
        }

        public void setDismissTimer(int time) {
            mTimer = time;
        }

        public int getDismissTimer() {
            return mTimer;
        }

        /**
         * Change background color of SnackBar
         *
         * @param color
         */
        public void setBackgroundSnackBar(int color) {
            backgroundSnackBar = color;
            if (view != null)
                view.setBackgroundColor(color);
        }

        /**
         * Chage color of FlatButton in Snackbar
         *
         * @param color
         */
        public void setColorButton(int color) {
            backgroundButton = color;
            if (button != null)
                button.setBackgroundColor(color);
        }

        /**
         * This event start when snackbar dismish without push the button
         *
         * @author Navas
         */


        public void setOnhideListener(OnHideListener onHideListener) {
            this.onHideListener = onHideListener;
        }
    }

    public interface OnActionListener{
        void onClick(SnackBar snackBar);
    }

    public interface OnHideListener {
        void onHide();
    }
}
