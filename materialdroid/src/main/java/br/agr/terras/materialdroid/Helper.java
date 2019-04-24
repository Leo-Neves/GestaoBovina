package br.agr.terras.materialdroid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by edson on 26/10/16.
 */
public class Helper extends ImageView{
    private Context context;


    public Helper(Context context){
        super(context);
        init(context);
    }

    public Helper(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }

    private void init(Context context){
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs){
        this.context = context;
        setOnClickListener(onClickHelper);
//        setImageResource(R.drawable.ic_help_white_24dp);
        setImageResource(R.drawable.ic_clear_black_24dp);
    }

    private OnClickListener onClickHelper = new OnClickListener() {
        @Override
        public void onClick(View v) {
            new Dialog.Builder(context)
                    .content("Helper Default")
                    .autoDismiss(false)
                    .canceledOnTouchOutside(true)
                    .show();
        }
    };
}
