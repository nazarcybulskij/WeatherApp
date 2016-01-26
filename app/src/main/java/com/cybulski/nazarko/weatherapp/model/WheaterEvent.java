package com.cybulski.nazarko.weatherapp.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nazarko on 1/21/2016.
 */
public class WheaterEvent extends RealmObject {

    @PrimaryKey
    private String identifier;
    private String title;
    private String AreaDesc;
    private RealmList<HashLocation> locations;



    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAreaDesc() {
        return AreaDesc;
    }

    public void setAreaDesc(String areaDesc) {
        AreaDesc = areaDesc;
    }


    public RealmList<HashLocation> getLocations() {
        return locations;
    }

    public void setLocations(RealmList<HashLocation> locations) {
        this.locations = locations;
    }

}
