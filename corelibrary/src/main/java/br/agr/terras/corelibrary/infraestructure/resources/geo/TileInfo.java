package br.agr.terras.corelibrary.infraestructure.resources.geo;

/**
 * Created by leo on 31/08/16.
 */
@Deprecated
public class TileInfo {
    private static final String googleHybrid = "http://mt2.google.com/vt/lyrs=s,h&x={x}&y={y}&z={z}";
    private static final String googleSattelite = "http://mt2.google.com/vt/lyrs=s&x={x}&y={y}&z={z}";
    private static final String googleTerrain = "http://mt2.google.com/vt/lyrs=p&x={x}&y={y}&z={z}";
    private static final String googleStreets = "http://mt2.google.com/vt/lyrs=m&x={x}&y={y}&z={z}";
    private static final String openStreetMap = "http://a.tile.openstreetmap.org/{z}/{x}/{y}.png";
    private static final String landsat8 = "https://tile-a.urthecast.com/v1/rgb/{z}/{x}/{y}?sensor_platform=landsat-8&cloud_coverage_lte=30&api_key=9A10429F5F99456BA010&api_secret=C201B812882E4EFEB9BC2D4B927C4D3C";
    private static final int maxGoogleZoomLevel = 22;
    private static final int maxOpenStreetMapZoomLevel = 19;
    private static final int maxLandsat8ZoomLevel = 11;

/*    public static RemoteTileInfo getGoogleHybrid() {
        return new RemoteTileInfo(googleHybrid, null, 0, maxGoogleZoomLevel);
    }

    public static RemoteTileInfo getGoogleSattelite() {
        return new RemoteTileInfo(googleSattelite, null, 0, maxGoogleZoomLevel);
    }

    public static RemoteTileInfo getGoogleTerrain() {
        return new RemoteTileInfo(googleTerrain, null, 0, maxGoogleZoomLevel);
    }

    public static RemoteTileInfo getGoogleStreets() {
        return new RemoteTileInfo(googleStreets, null, 0, maxGoogleZoomLevel);
    }

    public static RemoteTileInfo getOpenStreetMap() {
        return new RemoteTileInfo(openStreetMap, null, 0, maxOpenStreetMapZoomLevel);
    }

    public static RemoteTileInfo getLandsat8() {
        return new RemoteTileInfo(landsat8, null, 0, maxLandsat8ZoomLevel);
    }*/
}
