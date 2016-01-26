package com.cybulski.nazarko.weatherapp;

import android.app.Application;

import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Nazarko on 1/26/2016.
 */
public class App extends Application {


    KmlLayer layer;

    @Override
    public void onCreate() {
        super.onCreate();


     try {
         layer = new KmlLayer(null,R.raw.cb_2014_us_county_20m, getApplicationContext());
    } catch (XmlPullParserException e) {
         e.printStackTrace();
    } catch (IOException e) {
         e.printStackTrace();
    }


    }

    public KmlLayer getLayer() {
        return layer;
    }

    public void setLayer(KmlLayer layer) {
        this.layer = layer;
    }
}
