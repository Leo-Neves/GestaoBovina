package br.agr.terras.materialdroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import br.agr.terras.materialdroid.childs.GalleryCard;

/**
 * Created by leo on 17/03/17.
 */

public class GalleryView extends RelativeLayout implements
        GalleryCard.OnPageChangeListener {

    private Context context;
    private GalleryInterface galleryInterface;

    private int count = 0;

    private float nextRotation ;

    private int rootId ;
    private int layoutId ;

    private int cardItemHeight ;
    private int cardItemMargin ;

    private ViewGroup scrollableGroups[];

    public void setScrollableGroups(ViewGroup...args) {
        this.scrollableGroups = args;
    }

    public GalleryView(Context context) {
        super(context);
        this.context = context ;
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context ;
        TypedArray types = context.obtainStyledAttributes(attrs,
                R.styleable.GalleryView);
        final int count = types.getIndexCount();
        for (int i = 0; i < count; ++i) {
            int attr = types.getIndex(i);
            if (attr == R.styleable.GalleryView_gl_itemHeight) {
                cardItemHeight = types.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.GalleryView_gl_itemMargin) {
                cardItemMargin = types.getDimensionPixelSize(attr, 10);

            }
        }
        types.recycle();
    }


    public void addToView(View child) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(child, 0, layoutParams);
    }

    public void initGalleryView(final GalleryInterface galleryInterface, int layoutId, int rootId) {
        this.galleryInterface = galleryInterface;
        this.rootId = rootId ;
        this.layoutId = layoutId ;
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        for (int i = 0; i < 3; i++) {
            GalleryCard mGalleryCard = new GalleryCard(context);
            mGalleryCard.setContent(layoutId);
            mGalleryCard.setCardHeight(cardItemHeight);
            galleryInterface.initCard(mGalleryCard, i) ;
            View contentView = mGalleryCard.findViewById(rootId);
            if (i == 1) {
                contentView.setRotation(4);
            }
            if (i == 2) {
                contentView.setRotation(-3);
            }
            mGalleryCard.setCurrentItem(1, false);
            mGalleryCard.setOnPageChangeListener(this);
            addToView(mGalleryCard);
        }
    }

    public GalleryCard getNextView() {
        if (getChildCount() - 1 > 0) {
            return (GalleryCard) getChildAt(getChildCount() - 2);
        }
        return null;
    }

    @Override
    public synchronized void onPageScrolled(GalleryCard v, int position,
                                            float positionOffset, int positionOffsetPixels) {
//        Log.e("test", "onPageScrolled:" + position + "," +positionOffset +","
//                + positionOffsetPixels);
        GalleryCard galleryCard = getNextView();
        if (galleryCard != null) {
            if (Math.abs(positionOffsetPixels) != 0) {
                View contentView = galleryCard.findViewById(rootId);
                LayoutParams params = new LayoutParams(
                        contentView.getLayoutParams());
                params.topMargin = (int) ( Math.abs(positionOffset) * cardItemMargin);
                params.leftMargin = (int) (Math.abs(positionOffset) * cardItemMargin);
                params.rightMargin = (int) ( Math.abs(positionOffset) * cardItemMargin);
                contentView.setLayoutParams(params);
                contentView.setRotation((int) ( (1 - Math.abs(positionOffset)) * nextRotation));
                postInvalidate();
            }
        }
    }

    @Override
    public synchronized void onPageSelectedAfterAnimation(GalleryCard v, int prevPosition,
                                                          int curPosition) {
        if (context != null) {
            removeViewAt(getChildCount() - 1);
            galleryInterface.reorderList();
            GalleryCard mGalleryCard = new GalleryCard(context);
            mGalleryCard.setContent(layoutId);
            mGalleryCard.setCardHeight(cardItemHeight);
            galleryInterface.initCard(mGalleryCard, 2) ;
            View contentView = mGalleryCard.findViewById(rootId);
            setRotation(contentView);
            mGalleryCard.setCurrentItem(1, false);
            mGalleryCard.setOnPageChangeListener(this);
            addToView(mGalleryCard);
//            Log.e("test", "onPageSelectedAfterAnimation:" + curPosition + ","
//                    + getChildCount());
        }
    }

    @Override
    public synchronized void onPageSelected(GalleryCard v, int prevPosition, int curPosition) {
        // Log.e("test", "onPageSelected:" + curPosition);
    }

    @Override
    public synchronized void onPageScrollStateChanged(GalleryCard v, int state) {
        //   Log.e("test", "state change:" + state);
        if(state==1){
            GalleryCard galleryCard = getNextView();
            if (galleryCard != null) {
                View contentView = galleryCard.findViewById(rootId) ;
                nextRotation = contentView.getRotation() ;
            }
        }else if(state==0){
            GalleryCard.sScrolling = false ;
        }
    }

    private void setRotation(View v) {
        if (count % 3 == 1) {
            v.setRotation(4);
        } else if (count % 3 == 2) {
            v.setRotation(-3);
        }
        postInvalidate();
        count++;
    }

    public interface GalleryInterface {
        void initCard(GalleryCard card, int index) ;
        void reorderList() ;

    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        if(scrollableGroups!=null && scrollableGroups.length>0)
            for(ViewGroup group: scrollableGroups){
                group.requestDisallowInterceptTouchEvent(true);
            }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(scrollableGroups!=null && scrollableGroups.length>0)
            for(ViewGroup group: scrollableGroups){
                group.requestDisallowInterceptTouchEvent(true);
            }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if(scrollableGroups!=null && scrollableGroups.length>0)
            for(ViewGroup group: scrollableGroups){
                group.requestDisallowInterceptTouchEvent(true);
            }
        return super.onTouchEvent(event);
    }

}
