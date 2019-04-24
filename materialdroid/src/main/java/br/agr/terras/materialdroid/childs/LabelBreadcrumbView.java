package br.agr.terras.materialdroid.childs;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import br.agr.terras.materialdroid.utils.drawable.LabelDrawable;

/**
 * Created by leo on 28/09/16.
 */
public class LabelBreadcrumbView extends TextView {
    private int colorBorder = Color.GRAY;
    private int colorSelected = Color.BLUE;
    private int colorUnselected = Color.GRAY;
    private boolean isSelected;
    private int textUnselectedColor = Color.WHITE;
    private int textSelectedColor = Color.WHITE;

    public LabelBreadcrumbView(Context context) {
        super(context);
        init(context);
    }

    public LabelBreadcrumbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(p);
        setSingleLine(true);
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        setEllipsize(TextUtils.TruncateAt.END);
        setSelected(false);
    }

    public int getColorBorder() {
        return colorBorder;
    }

    public void setColorBorder(int colorBorder) {
        this.colorBorder = colorBorder;
        refreshColors();
    }

    public int getColorSelected() {
        return colorSelected;
    }

    public void setColorSelected(int colorSelected) {
        this.colorSelected = colorSelected;
        refreshColors();
    }

    public int getColorUnselected() {
        return colorUnselected;
    }

    public void setColorUnselected(int colorUnselected) {
        this.colorUnselected = colorUnselected;
        refreshColors();
    }

    public void setSelected(boolean selected){
        super.setSelected(selected);
        this.isSelected = selected;
        refreshColors();
    }

    private void refreshColors(){
        if (getParent()!=null){
            LabelDrawable label = new LabelDrawable();
            label.setColorExternal(colorBorder);
            label.setColorInternal(isSelected?colorSelected:colorUnselected);
            setBackgroundDrawable(label);
            setTextColor(isSelected?textSelectedColor:textUnselectedColor);
        }
    }

    public boolean isSelected(){
        return isSelected;
    }

    public void setTextUnselectedColor(int textUnselectedColor) {
        this.textUnselectedColor = textUnselectedColor;
        refreshColors();
    }

    public void setTextSelectedColor(int textSelectedColor) {
        this.textSelectedColor = textSelectedColor;
        refreshColors();
    }
}
