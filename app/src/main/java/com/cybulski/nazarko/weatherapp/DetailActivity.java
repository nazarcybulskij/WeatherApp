package com.cybulski.nazarko.weatherapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cybulski.nazarko.weatherapp.model.WheaterEvent;

import io.realm.Realm;

public class DetailActivity extends AppCompatActivity {
    private Realm realm;

    TextView  tvId;
    TextView tvTitle;
    TextView tvArea;

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



    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

}
