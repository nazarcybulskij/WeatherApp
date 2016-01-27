package com.cybulski.nazarko.weatherapp;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cybulski.nazarko.weatherapp.model.Country;
import com.cybulski.nazarko.weatherapp.model.CountryPoligonPoint;
import com.cybulski.nazarko.weatherapp.model.HashLocation;
import com.cybulski.nazarko.weatherapp.model.WheaterEvent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlGroundOverlay;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;
import com.google.maps.android.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;

public class DetailActivity extends AppCompatActivity {

    private Realm realm;
    TextView  tvId;
    TextView tvTitle;
    TextView tvArea;
    SupportMapFragment mapFragment;
    GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tvId =(TextView)findViewById(R.id.id);
        tvTitle =(TextView)findViewById(R.id.title);
        tvArea =(TextView)findViewById(R.id.area);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        realm  = Realm.getInstance(this);
        WheaterEvent wheaterEvent = realm.where(WheaterEvent.class).equalTo("identifier", getIntent().getStringExtra(MainActivity.EXTRA_ID)).findFirst();
        if (wheaterEvent!=null){
           // tvId.setText(wheaterEvent.getIdentifier());
            tvTitle.setText(wheaterEvent.getTitle());
            tvArea.setText(wheaterEvent.getAreaDesc());




        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();
        if (map == null) {
            finish();
            return;
        }else{
            drawZone(wheaterEvent);





//                for (KmlContainer container : layer.getContainers()) {
//                    if (container.hasContainers()) {
//                        for (KmlContainer containercounty : container.getContainers()) {
//                            Log.v("TAG",containercounty.getProperty("name"));
//                        }
//
//                    }
//                }


        }





    }

    private void drawZone(WheaterEvent wheaterEvent) {

        RealmList<HashLocation> locations = wheaterEvent.getLocations();

        if (locations==null || locations.size()==0){
            Toast.makeText(this,"don't find",Toast.LENGTH_SHORT).show();
        }else {

            for (HashLocation country:locations){
                Country point = realm.where(Country.class).equalTo("title",country.getTitle()).findFirst();
                if (point==null){
                    break;
                }
                ArrayList<LatLng> list  = new ArrayList<>();
                RealmList<CountryPoligonPoint> poligon = point.getPoligon();
                if (poligon==null){
                    break;
                }
                for (CountryPoligonPoint tempPoint:poligon){
                    list.add(new LatLng(tempPoint.getLat(),tempPoint.getLon()));
                }
                if (list.size()!=0){
                    Polygon polygon = map.addPolygon(new PolygonOptions()
                            .addAll(list)
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE));

                }

            }

        }


    }



        @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

}
