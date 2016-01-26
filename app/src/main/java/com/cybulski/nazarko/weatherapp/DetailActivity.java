package com.cybulski.nazarko.weatherapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cybulski.nazarko.weatherapp.model.WheaterEvent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

public class DetailActivity extends AppCompatActivity {

    private Realm realm;
    TextView  tvId;
    TextView tvTitle;
    TextView tvArea;
    SupportMapFragment mapFragment;
    GoogleMap map;
    KmlLayer layer;

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
                if (((App)getApplication()).getLayer()!=null){
                    layer=  ((App)getApplication()).getLayer();
                    //layer.setMap(map);
                    Iterable<KmlContainer> containers = layer.getContainers();
                    for (KmlContainer container : containers ) {
                        Iterable<KmlContainer>  countrycointeriner = container.getContainers();
                        for (KmlContainer innercointeriner:countrycointeriner){
                            Iterable <KmlPlacemark> country = innercointeriner.getPlacemarks();
                            for (KmlPlacemark placemark:country){
                                if (placemark.hasProperty("name")){
                                    Log.v("TAG", placemark.getProperty("name"));
                                    if (placemark.getGeometry().getGeometryType().equals("Polygon")){
                                        ArrayList <ArrayList<LatLng>>  poligon =(ArrayList <ArrayList<LatLng>>)placemark.getGeometry().getGeometryObject();
                                        Log.v("TAG",poligon.toString());

                                    }
                                }
                            }
                        }
                    }
                }






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

    public void accessContainers(Iterable<KmlContainer> containers) {
        if (containers==null){
            return;
        }
        for (KmlContainer container : containers ) {
            // Do something to container
            if (container.hasProperty("name")) {
                Log.v("TAG", container.getProperty("name"));
            }
            accessContainers(container.getContainers());

        }
    }


        @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

}
