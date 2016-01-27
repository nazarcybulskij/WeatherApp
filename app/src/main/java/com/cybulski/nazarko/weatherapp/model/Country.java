package com.cybulski.nazarko.weatherapp.model;

import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nazarko on 1/26/2016.
 */
public class Country extends RealmObject {

    @PrimaryKey
    private int id;
    private String title;
    private RealmList<CountryPoligonPoint> poligon;

    public int getId() {


        return id;


    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<CountryPoligonPoint> getPoligon() {
        return poligon;
    }

    public void setPoligon(RealmList<CountryPoligonPoint> poligon) {
        this.poligon = poligon;
    }
}
