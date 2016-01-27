package com.cybulski.nazarko.weatherapp.model;

import io.realm.RealmObject;

/**
 * Created by Nazarko on 1/26/2016.
 */
public class CountryPoligonPoint extends RealmObject {
    private  double lat;
    private  double lon;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
