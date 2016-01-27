package com.cybulski.nazarko.weatherapp.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nazarko on 1/21/2016.
 */
public class HashLocation extends RealmObject {


    @PrimaryKey
    private String hashcode;
    private String title;


    public String getHashcode() {
        return hashcode;
    }

    public void setHashcode(String hashcode) {
        this.hashcode = hashcode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



}
