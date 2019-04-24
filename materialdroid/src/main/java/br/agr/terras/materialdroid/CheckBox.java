package br.agr.terras.materialdroid;

import android.content.Context;
import android.util.AttributeSet;

import br.agr.terras.materialdroid.parents.CompoundButton;
import br.agr.terras.materialdroid.utils.drawable.CheckBoxDrawable;

/**
 * Created by leo on 18/05/16.
 */
public class CheckBox extends CompoundButton{
    private Context context;
    private AttributeSet attrs;
    private int defStyleAttr;
    private int defStyleRes;
    private int position;

    public CheckBox(Context context) {
        super(context);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void applyStyle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
        super.applyStyle(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.attrs = attrs;
        this.defStyleAttr = defStyleAttr;
        this.defStyleRes = defStyleRes;
        CheckBoxDrawable drawable = new CheckBoxDrawable.Builder(context, attrs, defStyleAttr, defStyleRes).build();
        drawable.setInEditMode(isInEditMode());
        drawable.setAnimEnable(false);
        setButtonDrawable(drawable);
        drawable.setAnimEnable(true);
    }

    public void setToggleColor(int color){
        CheckBoxDrawable drawable = new CheckBoxDrawable.Builder(context, attrs, defStyleAttr, defStyleRes).strokeColor(color).build();
        drawable.setInEditMode(isInEditMode());
        drawable.setAnimEnable(false);
        setButtonDrawable(drawable);
        drawable.setAnimEnable(true);
    }

    /**
     * Change the checked state of this button immediately without showing animation.
     * @param checked The checked state.
     */
    public void setCheckedImmediately(boolean checked){
        if(getButtonDrawable() instanceof CheckBoxDrawable){
            CheckBoxDrawable drawable = (CheckBoxDrawable)getButtonDrawable();
            drawable.setAnimEnable(false);
            setChecked(checked);
            drawable.setAnimEnable(true);
        }
        else
            setChecked(checked);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
