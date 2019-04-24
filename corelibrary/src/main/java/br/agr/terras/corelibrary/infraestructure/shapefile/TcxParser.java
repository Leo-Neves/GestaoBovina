package br.agr.terras.corelibrary.infraestructure.shapefile;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 14/02/17.
 */

public class TcxParser {
    private XmlPullParser parser;
    private List<Trackpoint> points;
    private int eventType;
    private static final String namespace = null;

    public TcxParser(InputStream stream) throws XmlPullParserException, IOException {
        points = new ArrayList<>();
        parser = createXmlParser(stream);
        parseTcx();
    }

    private static XmlPullParser createXmlParser(InputStream stream) throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(stream, null);
        return parser;
    }

    private void parseTcx() throws XmlPullParserException, IOException {
        eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("Trackpoint"))
                points.add(parsePoint());
            else
                eventType = parser.next();
        }
    }

    private double parserElevation(XmlPullParser parser) throws XmlPullParserException, IOException {
        String eleString = "";
        while (eventType != XmlPullParser.END_TAG) {
            if (eventType == XmlPullParser.TEXT) {
                eleString = parser.getText();
            }
            eventType = parser.next();
        }
        return Double.parseDouble(eleString);
    }

    private Trackpoint parsePoint() throws XmlPullParserException, IOException {
        double lat = 0;
        double lon = 0;
        double ele = 0;
        String text = null;
        while (eventType != XmlPullParser.END_TAG || !parser.getName().equals("Trackpoint")) {
            if (parser.getEventType() == XmlPullParser.TEXT)
                text = parser.getText();
            if (parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals("LatitudeDegrees"))
                lat = text != null ? Double.parseDouble(text) : 0;
            if (parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals("LongitudeDegrees"))
                lon = text != null ? Double.parseDouble(text) : 0;
            if (parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals("AltitudeMeters"))
                ele = text != null ? Double.parseDouble(text) : 0;
            eventType = parser.next();
        }
        Log.i(getClass().getSimpleName(), "tagName: "+parser.getName());
        return new Trackpoint(lat, lon, ele);
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    public List<Trackpoint> getPoints() {
        return points;
    }

    public class Trackpoint {
        private double lat;
        private double lon;
        private double alt;

        Trackpoint(double lat, double lon, double alt) {
            this.lat = lat;
            this.lon = lon;
            this.alt = alt;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

        public double getAlt() {
            return alt;
        }

        public String toString() {
            double var1 = this.lat;
            double var2 = this.lon;
            double var3 = this.alt;
            return (new StringBuilder(60)).append("lat/lng/alt: (").append(var1).append(",").append(var2).append(",").append(var3).append(")").toString();
        }
    }
}
