package br.agr.terras.corelibrary.infraestructure.resources.geo.map.marker;

/**
 * Created by leo on 02/09/16.
 */
public class DraggableMarker /*extends Marker */{
    /*private boolean mIsDragged;
    private static final RectF mTempRect = new RectF();
    private static final PointF mTempPoint = new PointF();
    private float mDx, mDy;

    public DraggableMarker(MarkerOptions markerOptions) {
        super(markerOptions);
        mIsDragged = false;
    }*/

   /* public boolean drag(View v, MotionEvent event) {
        final int action = event.getActionMasked();
        if(action == MotionEvent.ACTION_DOWN) {
            Projection pj = getMapboxMap().getProjection();
            getInfoWindow().getView();
            RectF bound = getDrawingBounds(pj, mTempRect);
            if(bound.contains(event.getX(), event.getY())) {
                mIsDragged = true;
                PointF p = getPositionOnScreen(pj, mTempPoint);
                mDx = p.x - event.getX();
                mDy = p.y - event.getY();
            }
        }
        if(mIsDragged) {
            if((action == MotionEvent.ACTION_CANCEL) ||
                    (action == MotionEvent.ACTION_UP)) {
                mIsDragged = false;
            } else {
                Projection pj = ((MapView)v).getProjection();
                ILatLng pos = pj.fromPixels(event.getX() + mDx, event.getY() + mDy);
                setPoint(new LatLng(pos.getLatitude(), pos.getLongitude()));
            }
        }

        return mIsDragged;
    }*/
}