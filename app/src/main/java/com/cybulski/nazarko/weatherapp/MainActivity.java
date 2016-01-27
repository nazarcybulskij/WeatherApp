package com.cybulski.nazarko.weatherapp;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cybulski.nazarko.weatherapp.event.ErrorEvent;
import com.cybulski.nazarko.weatherapp.event.FinishEvent;
import com.cybulski.nazarko.weatherapp.event.StartEvent;
import com.cybulski.nazarko.weatherapp.model.Country;
import com.cybulski.nazarko.weatherapp.model.HashLocation;
import com.cybulski.nazarko.weatherapp.model.WheaterEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import de.greenrobot.event.EventBus;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity  {

    NetworkApi  mApi=new NetworkApi();
    private Realm realm;

    public  final static String   EXTRA_ID  = "extra:id";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        mApi.conect(MainActivity.this);

        findViewById(R.id.loading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApi.load();

            }
        });

//        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        realm = Realm.getInstance(this);

        RealmResults<Country> countries = realm.where(Country.class).findAll();
        RealmResults<WheaterEvent> wheaterEvents = realm.where(WheaterEvent.class).findAll();

//        for (WheaterEvent tempWheaterEvent:wheaterEvents){
//            if (tempWheaterEvent.getLocations().size()!=0) {
//                Country coiuntu =realm.where(Country.class).equalTo("title", tempWheaterEvent.getLocations().get(0).getTitle()).findFirst();
//                if (coiuntu!=null){
//                    Log.v("test","find");
//                }else{
//                    Log.v("test",tempWheaterEvent.getLocations().get(0).getTitle());
//                }
//            }else{
//                Log.v("test","dont find");
//            }
//        }





        final WheaterEventAdapter adapter = new WheaterEventAdapter(this, wheaterEvents, true);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startDetailActivity(adapter.getRealmResults().get(position).getIdentifier());
            }
        });

    }

    public void onEventMainThread(StartEvent  event ){
        findViewById(R.id.loading).setEnabled(false);
    }

    public void onEventMainThread(FinishEvent event){
        findViewById(R.id.loading).setEnabled(true);
    }

    public void onEventMainThread(ErrorEvent event){
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mApi.disconect();
        realm.close();
        super.onDestroy();
    }

    private  void   startDetailActivity(String id){
        Intent  intent =  new Intent(this,DetailActivity.class);
        intent.putExtra(EXTRA_ID,id);
        startActivity(intent);

    }


}
