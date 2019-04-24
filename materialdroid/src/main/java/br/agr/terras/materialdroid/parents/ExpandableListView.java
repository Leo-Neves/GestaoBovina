package br.agr.terras.materialdroid.parents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import br.agr.terras.materialdroid.utils.listview.ExpandableStickyListHeadersAdapter;
import br.agr.terras.materialdroid.utils.listview.StickyListHeadersAdapter;

/**
 * Created by leo on 15/06/16.
 */
public class ExpandableListView extends ListView {
    public interface IAnimationExecutor{
        public void executeAnim(View target, int animType);
    }

    public final static int ANIMATION_COLLAPSE = 1;
    public final static int ANIMATION_EXPAND = 0;

    br.agr.terras.materialdroid.utils.listview.ExpandableStickyListHeadersAdapter mExpandableStickyListHeadersAdapter;



    IAnimationExecutor mDefaultAnimExecutor = new IAnimationExecutor() {
        @Override
        public void executeAnim(View target, int animType) {
            if(animType==ANIMATION_EXPAND){
                target.setVisibility(VISIBLE);
            }else if(animType==ANIMATION_COLLAPSE){
                target.setVisibility(GONE);
            }
        }
    };


    public ExpandableListView(Context context) {
        super(context);
    }

    public ExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ExpandableStickyListHeadersAdapter getAdapter() {
        return mExpandableStickyListHeadersAdapter;
    }

    @Override
    public void setAdapter(StickyListHeadersAdapter adapter) {
        mExpandableStickyListHeadersAdapter = new ExpandableStickyListHeadersAdapter(adapter);
        super.setAdapter(mExpandableStickyListHeadersAdapter);
    }

    public View findViewByItemId(long itemId){
        return mExpandableStickyListHeadersAdapter.findViewByItemId(itemId);
    }

    public long findItemIdByView(View view){
        return mExpandableStickyListHeadersAdapter.findItemIdByView(view);
    }

    public void expand(long headerId) {
        if(!mExpandableStickyListHeadersAdapter.isHeaderCollapsed(headerId)){
            return;
        }
        mExpandableStickyListHeadersAdapter.expand(headerId);
        //find and expand views in group
        List<View> itemViews = mExpandableStickyListHeadersAdapter.getItemViewsByHeaderId(headerId);
        if(itemViews==null){
            return;
        }
        for (View view : itemViews) {
            animateView(view, ANIMATION_EXPAND);
        }
    }

    public void collapse(long headerId) {
        if(mExpandableStickyListHeadersAdapter.isHeaderCollapsed(headerId)){
            return;
        }
        mExpandableStickyListHeadersAdapter.collapse(headerId);
        //find and hide views com the same header
        List<View> itemViews = mExpandableStickyListHeadersAdapter.getItemViewsByHeaderId(headerId);
        if(itemViews==null){
            return;
        }
        for (View view : itemViews) {
            animateView(view, ANIMATION_COLLAPSE);
        }
    }

    public boolean isHeaderCollapsed(long headerId){
        return  mExpandableStickyListHeadersAdapter.isHeaderCollapsed(headerId);
    }

    public void setAnimExecutor(IAnimationExecutor animExecutor) {
        this.mDefaultAnimExecutor = animExecutor;
    }

    /**
     * Performs either COLLAPSE or EXPAND animation on the target view
     *
     * @param target the view to animate
     * @param type   the animation type, either ExpandCollapseAnimation.COLLAPSE
     *               or ExpandCollapseAnimation.EXPAND
     */
    private void animateView(final View target, final int type) {
        if(ANIMATION_EXPAND==type&&target.getVisibility()==VISIBLE){
            return;
        }
        if(ANIMATION_COLLAPSE==type&&target.getVisibility()!=VISIBLE){
            return;
        }
        if(mDefaultAnimExecutor !=null){
            mDefaultAnimExecutor.executeAnim(target,type);
        }

    }

}