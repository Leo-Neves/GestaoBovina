package br.agr.terras.corelibrary.infraestructure.resources.geo.map;

/**
 * Created by leo on 01/09/16.
 */
public class MapBoxSetup/* implements MapSetup */{
    /*private final String TAG = getClass().getSimpleName();
    private Context context;
    private MapboxMap map;
    private ClusterManager<ClusterMarker> clusterManager;
    private double defaultLat = -3.002;
    private double defaultLong = -47.500;
    private Polygon blackPolygon;
    private Polygon whitePolygon;
    private Polygon meCircle;
    private Marker meMarker;
    private Subscription subscription;

    private boolean enableMyLocation = false;
    private boolean isModeDebug = false;

    HashMap<String, List<PlotPolygon>> hashMapPlotPolygons;
    HashMap<String, DrawPolygon> hashMapDrawPolygons;
    HashMap<String, List<Marker>> hashMapMarkers;
    HashMap<String, List<ClusterMarker>> hashMapClusters;
    private HashMap<String, List<List<LngLatAlt>>> allPolygonsLatLngs;
    private HashMap<String, List<ScaleDependenceMarker>> hashMapScaleDependence;

    private OnMapMarkerClick onMapMarkerClick;
    private CameraChangeListenerGoogle cameraChangeListenerGoogle;
    private MarkerClickListenerGoogle markerClickListenerGoogle;
    private MarkerDragListenerGoogle markerDragListenerGoogle;

    private int currentMode = MODE_SATTELITE;

    private Location myLocation;

    public MapBoxSetup(MapboxMap map) {
        context = CORE.getContext();
        allPolygonsLatLngs = new HashMap<>();
        hashMapPlotPolygons = new HashMap<>();
        hashMapDrawPolygons = new HashMap<>();
        hashMapMarkers = new HashMap<>();
        hashMapClusters = new HashMap<>();
        hashMapScaleDependence = new HashMap<>();
        setup(map);
    }


    private void setup(MapboxMap map) {
        this.map = map;
        clusterManager = new ClusterManager<>(context, map);
        clusterManager.setRenderer(new ClusterRenderer(map, clusterManager));
        clusterManager.setAlgorithm(new GridBasedAlgorithm<ClusterMarker>());
        cameraChangeListenerGoogle = new CameraChangeListenerGoogle(clusterManager);
        markerClickListenerGoogle = new MarkerClickListenerGoogle(clusterManager);
        markerDragListenerGoogle = new MarkerDragListenerGoogle();
        map.setOnCameraChangeListener(cameraChangeListenerGoogle);
//        map.setOnMarkerClickListener(markerClickListener);
        map.setOnMarkerClickListener(markerClickListenerGoogle);
        map.setOnMarkerDragListener(markerDragListenerGoogle);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setMyLocationEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        moveMap(new LatLng(defaultLat, defaultLong));
        zoomMap(12);
        createBlackBackground();
        createWhiteBackground();
        configureFirstMode();
        configureMarkerClickListener();
    }

    public void setMyLocationEnabled(boolean enabled) {
        if (enabled) {
            createMeMarker();
            configureFirstLocation();
        } else {
            if (meMarker != null)
                meMarker.remove();
            if (meCircle != null)
                meCircle.remove();
            if (subscription != null)
                subscription.unsubscribe();
        }
        Marker marker = map.addMarker(null);
    }


    private void configureFirstMode() {
        switch (currentMode) {
            case MODE_BLACK:
                modeBlack();
                break;
            case MODE_SATTELITE:
                modeSattelite();
                break;
            case MODE_TERRAIN:
                modeTerrain();
                break;
            case MODE_WHITE:
                modeWhite();
                break;
        }
    }

    public void modeSattelite() {
        currentMode = MODE_SATTELITE;
        blackPolygon.setVisible(false);
        whitePolygon.setVisible(false);
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void modeTerrain() {
        currentMode = MODE_TERRAIN;
        blackPolygon.setVisible(false);
        whitePolygon.setVisible(false);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void modeBlack() {
        currentMode = MODE_BLACK;
        blackPolygon.setVisible(true);
        whitePolygon.setVisible(false);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    public void modeWhite() {
        currentMode = MODE_WHITE;
        blackPolygon.setVisible(false);
        whitePolygon.setVisible(true);
        map.setMapType(GoogleMap.MAP_TYPE_NONE);
    }

    private void configureFirstLocation() {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        subscription = locationProvider.getLastKnownLocation().subscribe(new Action1<Location>() {

            public void call(Location location) {
                myLocation = location;
                refreshMeMarker(location);
                moveMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
                zoomMap(16);
            }
        });
    }


    public void configureLastKnownLocation() {
        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
        subscription = locationProvider.getLastKnownLocation().subscribe(new Action1<Location>() {

            public void call(Location location) {
                myLocation = location;
                refreshMeMarker(location);
            }
        });
    }

    private void createBlackBackground() {
        float delta = 0.1f;
        List points = Arrays.asList(new LatLng(90, -180),
                new LatLng(-90 + delta, -180 + delta),
                new LatLng(-90 + delta, 0),
                new LatLng(-90 + delta, 180 - delta),
                new LatLng(0, 180 - delta),
                new LatLng(90 - delta, 180 - delta),
                new LatLng(90 - delta, 0),
                new LatLng(90 - delta, -180 + delta),
                new LatLng(0, -180 + delta));
        PolygonOptions options = new PolygonOptions();
        options.addAll(points);
        options.fillColor(context.getResources().getColor(R.color.black_80));
        options.strokeColor(context.getResources().getColor(R.color.black_80));
        options.strokeWidth(5);
        options.zIndex(1);
        blackPolygon = map.addPolygon(options);
    }

    private void createWhiteBackground() {
        float delta = 0.1f;
        List points = Arrays.asList(new LatLng(90, -180),
                new LatLng(-90 + delta, -180 + delta),
                new LatLng(-90 + delta, 0),
                new LatLng(-90 + delta, 180 - delta),
                new LatLng(0, 180 - delta),
                new LatLng(90 - delta, 180 - delta),
                new LatLng(90 - delta, 0),
                new LatLng(90 - delta, -180 + delta),
                new LatLng(0, -180 + delta));
        PolygonOptions options = new PolygonOptions();
        options.addAll(points);
        options.fillColor(Color.WHITE);
        options.zIndex(1);
        whitePolygon = map.addPolygon(options);
    }

    private void configureMarkerClickListener() {
        markerClickListenerGoogle.setCustomListener(new GoogleMap.OnMarkerClickListener() {

            public boolean onMarkerClick(Marker markerClicked) {
                if (onMapMarkerClick != null && markerClicked.getId() != null) {
                    for (String string : hashMapMarkers.keySet()) {
                        for (int i = 0; i < hashMapMarkers.get(string).size(); i++) {
                            if (hashMapMarkers.get(string).get(i).getId().equals(markerClicked.getId())) {
                                onMapMarkerClick.markerClicked(hashMapMarkers.get(string).get(i).getSnippet(), string, i);
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    private void createMeMarker() {
        Location location = new Location("33");
        location.setLatitude(defaultLat);
        location.setLongitude(defaultLong);
        location.setAccuracy(200);
        PolygonOptions co = new PolygonOptions();
        float raio = location.getAccuracy();
        LatLng centro = new LatLng(location.getLatitude(), location.getLongitude());
        ArrayList<LatLng> pontos =CoordinatesUtils.getCirclePoints(centro,raio);
        co.addAll(pontos);
        co.fillColor(COR_MARKER_AZUL);
        co.strokeColor(COR_MARKER_AZUL_ESCURO);
        meCircle = map.addPolygon(co);
        MarkerOptions mo = new MarkerOptions();
        mo.setIcons(IconFactory.getInstance(context).fromBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.memarker_blue)));
        mo.position(new com.mapbox.mapboxsdk.geometry.LatLng(location.getLatitude(), location.getLongitude()));
        meMarker = map.addMarker(mo);
    }


    private void refreshMeMarker(Location location) {
        LatLng centro = new LatLng(location.getLatitude(), location.getLongitude());
        float raio = location.getAccuracy();
        meCircle.setPoints(CoordinatesUtils.getCirclePoints(centro, raio));
        meMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }


    public void setModeDebug(boolean enable) {
        isModeDebug = enable;
        if (!enable) {
            zoomMap(19);
        }
    }



    public void setMapClickListener(GoogleMap.OnMapClickListener onMapClickListener) {
        map.setOnMapClickListener(onMapClickListener);
    }


    public void setMapMarkerClick(OnMapMarkerClick onMapMarkerClick) {
        this.onMapMarkerClick = onMapMarkerClick;
    }


    public void moveMap(com.google.android.gms.maps.model.LatLng location) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(location);
        map.moveCamera(center);
    }


    public void zoomMap(float level) {
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(level);
        map.animateCamera(zoom);
    }


    public void clearMap() {
        defaultLat = meMarker.getPosition().latitude;
        defaultLong = meMarker.getPosition().longitude;
        map.clear();
        hashMapMarkers = new HashMap<>();
        hashMapClusters = new HashMap<>();
        hashMapPlotPolygons = new HashMap<>();
        createBlackBackground();
        createWhiteBackground();
        if (!(currentMode == MODE_BLACK)) {
            blackPolygon.setVisible(false);
        }
        if (!(currentMode == MODE_WHITE)) {
            whitePolygon.setVisible(false);
        }
        if (enableMyLocation)
            createMeMarker();
    }


    public boolean isGpsOn(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else {
            return false;
        }
    }


    public LngLatAlt getMapPosition() {
        if (myLocation != null) {
            return new LngLatAlt(myLocation.getLongitude(), myLocation.getLatitude(), myLocation.getAltitude()).accuracy(myLocation.getAccuracy());
        }
        return null;
    }


    public boolean isModeDebug() {
        return isModeDebug;
    }


    public void setPosition(Location location) {
        myLocation = location;
        refreshMeMarker(myLocation);
    }


    public void deletePolygons(String key) {
        if (hashMapPlotPolygons.containsKey(key))
            for (PlotPolygon polygon : hashMapPlotPolygons.get(key))
                polygon.remove();
        hashMapPlotPolygons.remove(key);
    }


    public Boolean hasPolygon(String key) {
        return getMarkersLatLngs(key).size() > 2;
    }


    public void showPolygons(String key, List<String> the_geoms, int strokeColor, int strokeSize, int polygonColor, boolean zoom) {
        for (String the_geom : the_geoms) {
            try {
                addPolygon(key, the_geom, strokeColor, strokeSize, polygonColor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (zoom)
            zoom(key);
    }

    private void addPolygon(String key, String the_geom, int strokeColor, int strokeSize, int polygonColor) throws IOException {
        addPolygon(key, ConvertTypes.convertTheGeomToLatLngs(the_geom), strokeColor, strokeSize, polygonColor);
    }

    private void addPolygon(String key, List<LatLng> latLngs, int strokeColor, int strokeSize, int polygonColor) throws IOException {
        List<PlotPolygon> polygons = hashMapPlotPolygons.get(key);
        if (polygons == null)
            polygons = new ArrayList<>();
        if (latLngs.size() > 0) {
            PlotPolygon polygon = new PlotPolygon(map, strokeColor, strokeSize, polygonColor, 2, latLngs);
            polygons.add(polygon);
            hashMapPlotPolygons.put(key, polygons);
        } else {
            hashMapPlotPolygons.put(key, null);
        }
    }


    public List<LatLng> getMarkersLatLngs(String key) {
        List<LatLng> listaLatLngs = new ArrayList<>();
        if (hashMapMarkers.get(key) != null)
            for (Marker marker : hashMapMarkers.get(key)) {
                listaLatLngs.add(marker.getPosition());
            }
        return listaLatLngs;
    }

    private void clearPolygonsFromMap(){
        for (String string : hashMapPlotPolygons.keySet()) {
            if(hashMapPlotPolygons.get(string) != null){
                int size = hashMapPlotPolygons.get(string).size();
                while(size > 0){
                    hashMapPlotPolygons.get(string).get(size-1).remove();
                    size--;
                }
            }
        }
    }


    public void deleteMarkerFromPolygon(String key, int position) {
        List<Marker> markerList = hashMapMarkers.get(key);
        Marker marker = markerList.get(position);
        marker.remove();
        markerList.remove(position);
        hashMapMarkers.put(key, markerList);
        List<LatLng> listaLatLongs = new ArrayList<>();
        for (String string : hashMapMarkers.keySet()) {
            for (int i = 0; i < hashMapMarkers.get(string).size(); i++) {
                listaLatLongs.add(hashMapMarkers.get(string).get(i).getPosition());
            }
        }
        clearPolygonsFromMap();
        try {
            addPolygon("", listaLatLongs, COR_AREA_LARANJA, 3, COR_AREA_AMARELA);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void deleteMarker(String key, int position) {
        List<Marker> markerList = hashMapMarkers.get(key);
        try {
            Marker marker = markerList.get(position);
            marker.remove();
            markerList.remove(position);
            hashMapMarkers.put(key, markerList);
        } catch (IndexOutOfBoundsException e) {
            Log.e("MapSetup", "No markers to delete on position " + position);
        } catch (NullPointerException e) {
            Log.e("MapSetup", "List \"" + key + "\" do not exist!");
        }
    }

    public void deleteVerticeFromPolygon(String key, Integer position){
        DrawPolygon drawPolygon = hashMapDrawPolygons.get(key);
        drawPolygon.deleteSelectedMarker(position);
    }

    public void drawPolygon(final String key, final com.google.android.gms.maps.model.LatLng latLng){
        DrawPolygon drawPolygon = hashMapDrawPolygons.get(key);
        if (drawPolygon==null)
            drawPolygon = new DrawPolygon(map, key);
        drawPolygon.addPoint(latLng);
        markerDragListenerGoogle.setDrawDragListener(drawPolygon.getOnMarkerDragListener);
        markerClickListenerGoogle.setDrawListener(drawPolygon.getOnMarkerClickListener(onMapMarkerClick));
        hashMapDrawPolygons.put(key, drawPolygon);
        hashMapMarkers.put(key, drawPolygon.getMarkers());
    }

    public void drawPolygon(final String key, int strokeColor, int strokeSize, int polygonColor, String titulo, int cor, com.google.android.gms.maps.model.LatLng latLng) {

        final Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).draggable(true).anchor(0.4f, 0.4f));
        marker.setIcons(BitmapDescriptorFactory.fromResource(R.drawable.memarker_green));

        *//*map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            public void onMapLongClick(LatLng latLng) {
                if (onMapMarkerClick!=null){
                    Integer markerPosition = null;
                    for(int i = 0; i<hashMapMarkers.get(key).size();i++){
                        if ()
                    }
                    onMapMarkerClick.markerClicked(marker.getSnippet(),"",m);
                }
            }
        });*//*
        List<Marker> markers = hashMapMarkers.get(key);
        if (markers == null)
            markers = new ArrayList<>();
        marker.setSnippet(UUID.randomUUID().toString());
        markers.add(marker);
        hashMapMarkers.put(key, markers);
        if (hashMapMarkers.get(key).size() > 2) {
            List<LatLng> listaLatLongs = new ArrayList<>();
            for (String string : hashMapMarkers.keySet()) {
                for (int i = 0; i < hashMapMarkers.get(string).size(); i++) {
                    listaLatLongs.add(hashMapMarkers.get(string).get(i).getPosition());
                }
            }
            clearPolygonsFromMap();
            try {
                addPolygon(key, listaLatLongs, strokeColor, strokeSize, polygonColor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }




    public void zoom(String key, int padding) {
        List<Double> longitudes = new ArrayList<Double>();
        List<Double> latitudes = new ArrayList<Double>();

        for (PlotPolygon polygon : hashMapPlotPolygons.get(key)) {
            for (LatLng point : polygon.getLatLngs()) {
                longitudes.add(point.longitude);
                latitudes.add(point.latitude);
            }
        }

        LatLng northeast = new LatLng(Collections.max(latitudes), Collections.max(longitudes));
        LatLng southwest = new LatLng(Collections.min(latitudes), Collections.min(longitudes));
        LatLngBounds latLngBounds = new LatLngBounds(southwest, northeast);
        getMap().moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 500, 500, padding));

    }


    public void zoom(String key) {
        zoom(key, 50);
    }


    public void zoomAnimated(String key, int padding) {
        List<Double> longitudes = new ArrayList<Double>();
        List<Double> latitudes = new ArrayList<Double>();

        for (PlotPolygon polygon : hashMapPlotPolygons.get(key)) {
            for (LatLng point : polygon.getLatLngs()) {
                longitudes.add(point.longitude);
                latitudes.add(point.latitude);

            }
        }

        LatLng northeast = new LatLng(Collections.max(latitudes), Collections.max(longitudes));
        LatLng southwest = new LatLng(Collections.min(latitudes), Collections.min(longitudes));
        LatLngBounds latLngBounds = new LatLngBounds(southwest, northeast);
        getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 500, 500, padding));

    }


    public void zoomAnimated(String key) {
        zoomAnimated(key, 50);
    }


    public Integer checkPositionInPoligono(String key) {
        if (hashMapPlotPolygons.get(key) == null) {
            return null;
        }
        for (int i = 0; i < hashMapPlotPolygons.get(key).size(); i++) {
            if (verificarSeEstaNoPoligono(hashMapPlotPolygons.get(key).get(i), meMarker.getPosition())) {
                return i;
            }
        }
        return null;
    }

    private boolean verificarSeEstaNoPoligono(PlotPolygon poligono, LatLng position) {

        List<PointFunctions> listPointFunctions = new ArrayList<PointFunctions>();

        double x = position.longitude;
        double y = position.latitude;
        PointFunctions pointFunctions = new PointFunctions(x, y);


        for (LatLng point : poligono.getLatLngs()) {
            double a = point.longitude;
            double b = point.latitude;
            listPointFunctions.add(new PointFunctions(a, b));

        }

        for (String key : allPolygonsLatLngs.keySet()) {
            for (List<LngLatAlt> lngLatAltList : allPolygonsLatLngs.get(key)) {
                for (LngLatAlt lngLatAlt : lngLatAltList) {
                    double lon = lngLatAlt.getLongitude();
                    double lat = lngLatAlt.getLatitude();
                    // listPointFunctions.add(new PointFunctions(lon, lat));
                }
            }
        }

        PolygonFunctions polygonFunctions = PolygonFunctions.Builder().addVertexes(listPointFunctions).build();

        boolean inPoligono = polygonFunctions.contains(pointFunctions);
        return inPoligono;

    }


    public Polygon addPolyline(String key, int position) {
        if (hashMapPlotPolygons.get(key) != null)
            return hashMapPlotPolygons.get(key).get(position).addPolyline();
        return null;
    }

    public void addMarker(String id, String key, String titulo, int cor, LngLatAlt lngLatAlt) {
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setColor(cor);
        iconGenerator.setTextAppearance(R.style.iconGenText);
        Bitmap bitmapBackground = iconGenerator.makeIcon(titulo);
        addMarker(id, key, bitmapBackground, lngLatAlt);
    }

    public void addMarker(String key, String titulo, int cor, LngLatAlt lngLatAlt) {
        addMarker(null, key, titulo, cor, lngLatAlt);
    }

    public void addMarker(final String id, final String key, final LngLatAlt lngLatAlt, int drawable) {
        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lngLatAlt.getLatitude(), lngLatAlt.getLongitude())));
        marker.setIcons(BitmapDescriptorFactory.fromResource(drawable));
        addMarker(marker, id, key);

    }

    public void addMarker(final String id, final String key, String title, final LngLatAlt lngLatAlt, int drawable, int cor) {
        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lngLatAlt.getLatitude(), lngLatAlt.getLongitude())));
        Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth(), sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(cor, 1);
        p.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        *//*View view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.component_slider,null);
        IconGenerator iconGenerator = new IconGenerator(context);
        iconGenerator.setColor(android.R.color.transparent);
        iconGenerator.setTextAppearance(R.style.iconGenText);
        iconGenerator.setContentView(view);
        Bitmap resultBitmap = iconGenerator.makeIcon("Teste da armadilha");*//*
        marker.setIcons(BitmapDescriptorFactory.fromBitmap(drawTextToBitmap(context, resultBitmap, title)));
        marker.setAnchor(0.5F, 1);
        addMarker(marker, id, key);

    }

    public void addMarker(String id, String key, Bitmap bitmap, LngLatAlt lngLatAlt) {
        Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(lngLatAlt.getLatitude(), lngLatAlt.getLongitude())));
        marker.setIcons(BitmapDescriptorFactory.fromBitmap(bitmap));
        marker.setAnchor(0.5F, 1);
        addMarker(marker, id, key);
    }

    private void addMarker(Marker marker, String id, String key) {
        List<Marker> markers = hashMapMarkers.get(key);
        if (markers == null)
            markers = new ArrayList<>();
        if (id != null)
            marker.setSnippet(id);
        markers.add(marker);
        hashMapMarkers.put(key, markers);
    }

    public void setEnabledMarkers(String key, boolean enabled){
        if (hashMapMarkers.containsKey(key) && hashMapMarkers.get(key).size()>0){
            boolean status = hashMapMarkers.get(key).get(0).isVisible();
            if (status!=enabled)
                for (Marker marker : hashMapMarkers.get(key))
                    marker.setVisible(enabled);
        }
    }

    public void addCluster(String key, String id, LngLatAlt lngLatAlt, String nome, int cor) {
        ClusterMarker markerInfo = new ClusterMarker(key, id, lngLatAlt, nome, cor);
        List<ClusterMarker> list = hashMapClusters.get(key);
        if (list == null)
            list = new ArrayList<>();
        list.add(markerInfo);
        hashMapClusters.put(key, list);
        clusterManager.addItem(markerInfo);
    }


    public void centerMeMarker() {
        moveMap(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
        zoomMap(19);
    }

    public List<Double> getDistancesBetwwenMarkersAndMe(String key) {
        List<Double> distances = new ArrayList<>();
        if (hashMapMarkers.containsKey(key))
            for (Marker marker : hashMapMarkers.get(key)) {
                double distance = SphericalUtil.computeDistanceBetween(marker.getPosition(), meMarker.getPosition());
                distances.add(distance);
                Log.i("MapSetup", "Distance betwween me and " + key + " - " + marker.getSnippet() + ": " + distance);
            }
        return distances;
    }

    public void remove(String key) {
        if (hashMapPlotPolygons.containsKey(key))
            for (PlotPolygon polygon : hashMapPlotPolygons.get(key)) {
                polygon.remove();
            }
        if (hashMapMarkers.containsKey(key))
            for (Marker marker : hashMapMarkers.get(key))
                marker.remove();
        if (hashMapClusters.containsKey(key))
            for (ClusterMarker markerInfo : hashMapClusters.get(key))
                clusterManager.removeItem(markerInfo);
        hashMapPlotPolygons.remove(key);
        hashMapMarkers.remove(key);
        hashMapClusters.remove(key);
    }


    public void setCameraChangeListenerGoogle(GoogleMap.OnCameraChangeListener cameraChangeListenerGoogle) {
        this.cameraChangeListenerGoogle.setCustomListener(cameraChangeListenerGoogle);
    }


    public void setZIndex(String key, int zIndex) {
        for (PlotPolygon polygon : hashMapPlotPolygons.get(key))
            polygon.setZIndex(zIndex);
    }

    public String checkPositionInMarker(String key, int radius) {
        if (hashMapMarkers.get(key) == null) {
            return null;
        }
        for (int i = 0; i < hashMapMarkers.get(key).size(); i++) {
            if (SphericalUtil.computeDistanceBetween(meMarker.getPosition(), hashMapMarkers.get(key).get(i).getPosition()) < radius) {
                return hashMapMarkers.get(key).get(i).getSnippet();
            }
        }
        return null;
    }

    public Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap, String mText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap transparentBitmap = Bitmap.createBitmap(bitmap.getWidth(), (int) (bitmap.getHeight() * 1), Bitmap.Config.ARGB_8888);
            //Canvas c = new Canvas(transparentBitmap);
            //c.drawARGB(0,0,0,0);
            Bitmap markerBitmap = ImageUtils.mergeBitmaps(transparentBitmap, bitmap);
            Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if (bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(255, 255, 255));
            // text size in pixels
            paint.setTextSize((int) (12 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (int) ((bitmap.getWidth() - bounds.width()) / 3.3);
            int y = (int) ((bitmap.getHeight() + bounds.height()) / 4.5);
            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }

    }*/
}
