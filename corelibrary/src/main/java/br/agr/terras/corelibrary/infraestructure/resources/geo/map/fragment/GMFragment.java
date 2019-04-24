package br.agr.terras.corelibrary.infraestructure.resources.geo.map.fragment;

/**
 * Created by leo on 30/08/16.
 */
@Deprecated
public class GMFragment /*extends GlobeMapFragment */{
    /*private MaplyBaseController controller;
    private ComponentObject meMarker;
    private Point2d meMarkerPoint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle inState) {
        super.onCreateView(inflater, container, inState);

        // Do app specific setup logic.

        return baseControl.getContentView();
    }

    @Override
    protected MapDisplayType chooseDisplayType() {
        return MapDisplayType.Map;
    }

    @Override
    protected void controlHasStarted() {
        controller = mapControl;
        // setup base layer tiles
        String cacheDirName = "ghybrid";
        File cacheDir = new File(getActivity().getCacheDir(), cacheDirName);
        cacheDir.mkdir();
        RemoteTileSource remoteTileSource = new RemoteTileSource(TileInfo.getGoogleHybrid());
        remoteTileSource.setCacheDir(cacheDir);
        //QuadImageTileLayer baseLayer = new QuadImageTileLayer(globeControl, coordSystem, remoteTileSource);

*//*        File storageDir = Environment.getExternalStorageDirectory();
        File mbtilesDir = new File(storageDir, "mbtiles");
        File mbtilesFile = new File(mbtilesDir, "control-room.mbtiles");
        if (!mbtilesFile.exists()) {
            new Dialog.Builder(getActivity())
                    .title("Missing MBTiles")
                    .content("Could not find MBTiles file.")
                    .onPositive(new Dialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull Dialog dialog, @NonNull DialogAction which) {

                        }
                    }).show();
        }
        MBTiles mbtiles = new MBTiles(mbtilesFile);
        MBTilesImageSource localTileSource = new MBTilesImageSource(mbtiles);*//*
        SphericalMercatorCoordSystem coordSystem = new SphericalMercatorCoordSystem();
        QuadImageTileLayer baseLayer = new QuadImageTileLayer(controller, coordSystem, remoteTileSource);


        // globeControl is the controller when using MapDisplayType.Globe
        // mapControl is the controller when using MapDisplayType.Map

        baseLayer.setImageDepth(1);
        baseLayer.setSingleLevelLoading(false);
        baseLayer.setUseTargetZoomLevel(false);
        baseLayer.setCoverPoles(true);
        baseLayer.setHandleEdges(true);

        // add layer and position
        controller.addLayer(baseLayer);
        if (controller instanceof GlobeController){
            ((GlobeController)controller).animatePositionGeo(-1.6704803, -48.5023056, 12, 2.0);
            globeControl.gestureDelegate = this;
        }
        if (controller instanceof MapController){
            ((MapController)controller).animatePositionGeo(-1.6704803, -48.5023056, 12, 2.0);
            ((MapController)controller).setAllowRotateGesture(true);
            mapControl.gestureDelegate = this;
        }
        insertMarkers();
        insertSpheres();
        insertVectors();
    }

    private void insertMarkers() {
        List<ScreenMarker> markers = new ArrayList<>();

        MarkerInfo markerInfo = new MarkerInfo();
        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.memarker_green);
        Point2d markerSize = new Point2d(20, 20);

        // Moskow - Москва
        ScreenMarker moskow = new ScreenMarker();
        moskow.loc = Point2d.FromDegrees(37.616667, 55.75); // Longitude, Latitude
        moskow.image = icon;
        moskow.size = markerSize;
        markers.add(moskow);

        //  Saint Petersburg - Санкт-Петербург
        ScreenMarker stPetersburg = new ScreenMarker();
        stPetersburg.loc = Point2d.FromDegrees(30.3, 59.95);
        stPetersburg.image = icon;
        stPetersburg.size = markerSize;
        markers.add(stPetersburg);

        // Novosibirsk - Новосибирск
        ScreenMarker novosibirsk = new ScreenMarker();
        novosibirsk.loc = Point2d.FromDegrees(82.95, 55.05);
        novosibirsk.image = icon;
        novosibirsk.size = markerSize;
        markers.add(novosibirsk);

        // Yekaterinburg - Екатеринбург
        ScreenMarker yekaterinburg = new ScreenMarker();
        yekaterinburg.loc = Point2d.FromDegrees(60.583333, 56.833333);
        yekaterinburg.image = icon;
        yekaterinburg.size = markerSize;
        markers.add(yekaterinburg);

        // Nizhny Novgorod - Нижний Новгород
        ScreenMarker nizhnyNovgorod = new ScreenMarker();
        nizhnyNovgorod.loc = Point2d.FromDegrees(44.0075, 56.326944);
        nizhnyNovgorod.image = icon;
        nizhnyNovgorod.size = markerSize;
        nizhnyNovgorod.rotation = Math.PI;
        markers.add(nizhnyNovgorod);

        // Add your markers to the map controller.
        ComponentObject markersComponentObject = controller.addScreenMarkers(markers, markerInfo, MaplyBaseController.ThreadMode.ThreadAny);
    }
    private void insertSpheres() {
        List<Shape> shapes = new ArrayList<>();

        // Kansas City
        ShapeSphere shape = new ShapeSphere();
        shape.setLoc(Point2d.FromDegrees(-94.58, 39.1));
        shape.setRadius(0.04f); // 1.0 is the radius of the Earth
        shapes.add(shape);

        // Washington D.C.
        shape = new ShapeSphere();
        shape.setLoc(Point2d.FromDegrees(-77.036667, 38.895111));
        shape.setRadius(0.1f);
        shapes.add(shape);

        // McMurdo Station
        shape = new ShapeSphere();
        shape.setLoc(Point2d.FromDegrees(166.666667, -77.85));
        shape.setRadius(0.2f);
        shapes.add(shape);

        // Windhoek
        shape = new ShapeSphere();
        shape.setLoc(Point2d.FromDegrees(17.083611, -22.57));
        shape.setRadius(0.08f);
        shapes.add(shape);

        ShapeInfo shapeInfo = new ShapeInfo();
        shapeInfo.setColor(0.7f, 0.2f, 0.7f, 0.8f); // R,G,B,A - values [0.0 => 1.0]
        shapeInfo.setDrawPriority(1000000);

        ComponentObject componentObject = controller.addShapes(shapes, shapeInfo, MaplyBaseController.ThreadMode.ThreadAny);
    }

    @Override
    public void userDidSelect(MapController mapControl, SelectedObject[] selObjs, Point2d loc, Point2d screenLoc) {
        String msg = "Selected feature count: " + selObjs.length;
        for (SelectedObject obj : selObjs) {
            // GeoJSON
            if (obj.selObj instanceof VectorObject) {
                VectorObject vectorObject = (VectorObject) obj.selObj;
            }
        }
        Log.i(getClass().getSimpleName(), "userDidSelect");
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }



    @Override
    public void userDidTap(MapController mapControl, Point2d loc, Point2d screenLoc) {
        super.userDidTap(mapControl, loc, screenLoc);
        Log.i(getClass().getSimpleName(),"userDidTap");
        if (meMarker!=null)
            controller.removeObject(meMarker, MaplyBaseController.ThreadMode.ThreadAny);
        MarkerInfo markerInfo = new MarkerInfo();
        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.memarker_green);
        Point2d markerSize = new Point2d(20, 20);
        ScreenMarker me = new ScreenMarker();
        me.loc = loc;
        me.image = icon;
        me.size = markerSize;
        me.selectable = true;
        meMarker = controller.addScreenMarker(me, markerInfo, MaplyBaseController.ThreadMode.ThreadAny);
        mapControl.setPositionGeo(loc.getX(),loc.getY(), controller.currentMapZoom(loc));
    }

    @Override
    public void userDidLongPress(MapController mapController, SelectedObject[] selObjs, Point2d loc, Point2d screenLoc) {
        super.userDidLongPress(mapController, selObjs, loc, screenLoc);
        Log.i(getClass().getSimpleName(),"userDidLongPress");
    }

    @Override
    public void mapDidStartMoving(MapController mapControl, boolean userMotion) {
        super.mapDidStartMoving(mapControl, userMotion);
        Log.i(getClass().getSimpleName(),"mapDidStartMoving");


    }

    @Override
    public void mapDidStopMoving(MapController mapControl, Point3d[] corners, boolean userMotion) {
        super.mapDidStopMoving(mapControl, corners, userMotion);
        Log.i(getClass().getSimpleName(),"mapDidStopMoving");
    }

    @Override
    public void mapDidMove(MapController mapControl, Point3d[] corners, boolean userMotion) {
        super.mapDidMove(mapControl, corners, userMotion);
        Log.i(getClass().getSimpleName(),"mapDidMove "+corners.length);
        for (int i=0;i<corners.length;i++){
            Log.i(getClass().getSimpleName(),"----- "+i+" x:"+corners[i].getX()+" y: "+corners[i].getY());
        }
    }

    private void insertVectors(){

        String theGeom = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[-47.3768795626899,-3.43161154320577],[-47.3781984764266,-3.4316928397117],[-47.3715097290924,-3.40403403320194],[-47.3738019651232,-3.4021918711323],[-47.3721602885602,-3.39729391920383],[-47.3667972647538,-3.39913075535905],[-47.3620146505711,-3.40076502388698],[-47.3603366865217,-3.4013412742143],[-47.3606547407858,-3.40404748822165],[-47.3638185130298,-3.43075880115138],[-47.3768795626899,-3.43161154320577]]]]}";
        String geoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{},\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[-47.3768795626899,-3.43161154320577],[-47.3781984764266,-3.4316928397117],[-47.3715097290924,-3.40403403320194],[-47.3738019651232,-3.4021918711323],[-47.3721602885602,-3.39729391920383],[-47.3667972647538,-3.39913075535905],[-47.3620146505711,-3.40076502388698],[-47.3603366865217,-3.4013412742143],[-47.3606547407858,-3.40404748822165],[-47.3638185130298,-3.43075880115138],[-47.3768795626899,-3.43161154320577]]]]}}]}";
        ComponentObject poligono;
        VectorInfo vectorInfo = new VectorInfo();
        vectorInfo.setColor(MapSetup.COR_AREA_LARANJA);
        vectorInfo.setFilled(true);
        vectorInfo.setLineWidth(7);
        VectorObject object = new VectorObject();

        if (object.fromGeoJSON(geoJson)) {
            poligono = controller.addVector(object, vectorInfo, MaplyBaseController.ThreadMode.ThreadAny);
        }else{
            Log.e(getClass().getSimpleName(),"Não é geoJSON válido");
        }
        VectorInfo vectorInfo2 = new VectorInfo();
        vectorInfo2.setColor(MapSetup.COR_AREA_AMARELA);
        vectorInfo2.setFilled(false);
        vectorInfo2.setLineWidth(11);
        VectorObject object2 = new VectorObject();
        if (object2.fromGeoJSON(geoJson)) {
            controller.addVector(object2, vectorInfo2, MaplyBaseController.ThreadMode.ThreadAny);
        }else{
            Log.e(getClass().getSimpleName(),"Não é geoJSON válido");
        }


    }*/

}