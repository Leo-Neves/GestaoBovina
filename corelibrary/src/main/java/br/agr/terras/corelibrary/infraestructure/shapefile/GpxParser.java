package br.agr.terras.corelibrary.infraestructure.shapefile;

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

public class GpxParser {
    private XmlPullParser parser;
    private List<TrkPt> points;
    private int eventType;
    private static final String namespace = null;

    public GpxParser(InputStream stream) throws XmlPullParserException, IOException {
        points = new ArrayList<>();
        parser = createXmlParser(stream);
        parseGpx();
    }

    private static XmlPullParser createXmlParser(InputStream stream) throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(stream, null);
        return parser;
    }

    private void parseGpx() throws XmlPullParserException, IOException {
        eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("trkpt"))
                points.add(parsePoint(parser));
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

    private TrkPt parsePoint(XmlPullParser parser) throws XmlPullParserException, IOException {
        double lat;
        double lon;
        double ele = 0;
        String latString = parser.getAttributeValue(null, "lat");
        String lonString = parser.getAttributeValue(null, "lon");
        lat = latString!=null?Double.parseDouble(latString):0;
        lon = lonString!=null?Double.parseDouble(lonString):0;
        while (eventType != XmlPullParser.END_TAG) {
            if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("ele")) {
                ele = parserElevation(parser);
            }
            eventType = parser.next();
        }
        return new TrkPt(lat, lon, ele);
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

    public List<TrkPt> getPoints() {
        return points;
    }

    public class TrkPt {
        private double lat;
        private double lon;
        private double ele;

        TrkPt(double lat, double lon, double ele) {
            this.lat = lat;
            this.lon = lon;
            this.ele = ele;
        }

        public double getLat() {
            return lat;
        }

        public double getLon() {
            return lon;
        }

        public double getEle() {
            return ele;
        }

        public String toString() {
            double var1 = this.lat;
            double var2 = this.lon;
            double var3 = this.ele;
            return (new StringBuilder(60)).append("lat/lng: (").append(var1).append(",").append(var2).append(",").append(var3).append(")").toString();
        }
    }
}
