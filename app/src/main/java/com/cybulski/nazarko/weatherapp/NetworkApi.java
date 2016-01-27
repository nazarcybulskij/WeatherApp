package com.cybulski.nazarko.weatherapp;

import android.content.Context;
import android.renderscript.Element;
import android.util.Log;
import android.widget.Toast;

import com.cybulski.nazarko.weatherapp.event.FinishEvent;
import com.cybulski.nazarko.weatherapp.event.StartEvent;
import com.cybulski.nazarko.weatherapp.model.Country;
import com.cybulski.nazarko.weatherapp.model.CountryPoligonPoint;
import com.cybulski.nazarko.weatherapp.model.HashLocation;
import com.cybulski.nazarko.weatherapp.model.WheaterEvent;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Nazarko on 1/21/2016.
 */
public class NetworkApi {
    Context mContext;
    Realm realm;
    OkHttpClient client;


    public static  final String  LOG_NETWORK = "NetworkApi";

    public NetworkApi() {
        this.mContext = null;
    }


    public  void conect(Context mContext){
        this.mContext = mContext;

    }

    public  void disconect(){
        this.mContext = null;
    }
    public void  load(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mContext!=null) try {
                    EventBus.getDefault().post(new StartEvent());
                    realm = Realm.getInstance(mContext);
                    //get  geocode;
                    //client = new OkHttpClient();
                   // String resultstr = run("http://ip.jsontest.com/");
                    InputStream ins = mContext.getResources().openRawResource(
                            mContext.getResources().getIdentifier("raw/geocode_usa",
                                    "raw", mContext.getPackageName()));
                    realm.beginTransaction();
                    realm.createOrUpdateAllFromJson(HashLocation.class, ins);
                    realm.commitTransaction();

                    //Gson gson = new GsonBuilder().create();
                    //gson.fromJson(new BufferedReader(new InputStreamReader(ins)), new TypeToken<List<HashLocation>>(){}.getType());

                    //Log.v(LOG_NETWORK, url);
                    //list  event  state
                    Document doc = Jsoup.connect("http://alerts.weather.gov/cap/us.php?x=1").parser(Parser.xmlParser()).get();
                    Elements entrys =  doc.select("entry");
                    Realm realmevent = Realm.getInstance(mContext);
                    for (org.jsoup.nodes.Element temp1:entrys) {
                        WheaterEvent wheaterEvent = new WheaterEvent();
                        wheaterEvent.setIdentifier(temp1.select("id").text());
                        wheaterEvent.setAreaDesc(temp1.select("cap|areaDesc").text());
                        wheaterEvent.setTitle(temp1.select("title").text());
                        Elements geocodes = temp1.select("cap|geocode");
                        RealmList<HashLocation> list = new RealmList<HashLocation>();
                        for (org.jsoup.nodes.Element geocode:geocodes.select("value")){
                            for (String txt:geocode.text().split(" ")){
                                HashLocation hashLocation = realm.where(HashLocation.class).equalTo("hashcode",txt).findFirst();
                                if (hashLocation!=null){
                                    list.add(hashLocation);
                                }
                            }

                        }

                        realm.beginTransaction();
                        wheaterEvent.setLocations(list);
                        realm.copyToRealmOrUpdate(wheaterEvent);
                        realm.commitTransaction();
                    }
                    realmevent.close();

                    if (((App)mContext.getApplicationContext()).getLayer()!=null){
                        KmlLayer layer=  ((App)mContext.getApplicationContext()).getLayer();
                        //layer.setMap(map);
                        Iterable<KmlContainer> containers = layer.getContainers();
                        for (KmlContainer container : containers ) {
                            Iterable<KmlContainer>  countrycointeriner = container.getContainers();
                            for (KmlContainer innercointeriner:countrycointeriner){
                                Iterable <KmlPlacemark> country = innercointeriner.getPlacemarks();
                                int id =1;
                                realmevent.beginTransaction();
                                for (KmlPlacemark placemark:country){
                                    if (placemark.hasProperty("name")){
                                        if (placemark.getGeometry().getGeometryType().equals("Polygon")){
                                            Country countryDB=new Country();
                                            countryDB.setId(id);
                                            id=id+1;
                                            countryDB.setTitle(placemark.getProperty("name"));
                                            ArrayList <ArrayList<LatLng>>  poligon =(ArrayList <ArrayList<LatLng>>)placemark.getGeometry().getGeometryObject();
                                            RealmList<CountryPoligonPoint> list =new RealmList<CountryPoligonPoint>();
                                            for (LatLng lanlng:poligon.get(0)){
                                                CountryPoligonPoint point = realmevent.createObject(CountryPoligonPoint.class);
                                                point.setLat(lanlng.latitude);
                                                point.setLon(lanlng.longitude);
                                                list.add(point);
                                            }
                                            countryDB.setPoligon(list);
                                            realmevent.copyToRealmOrUpdate(countryDB);

                                        }

                                    }
                                }
                                realmevent.commitTransaction();
                            }
                        }
                    }





                    RealmResults<HashLocation> result = realm.where(HashLocation.class).findAll();
                    if (result.size()>6000){
//                        ArrayList<HashMap> wordList;
//                        wordList = new ArrayList<>();
//                        for (HashLocation temphash:result){
//                            HashMap<String, String> map = new HashMap<String, String>();
//                            map.put("title", temphash.getTitle());
//                            map.put("hashcode",temphash.getHashcode());
//                            wordList.add(map);
//                        }
//                        Gson gson = new GsonBuilder().create();
//                        String json = gson.toJson(wordList);
                        realm.close();
                        EventBus.getDefault().post(new FinishEvent());
                        return;
                    }


//                    doc = Jsoup.connect("http://alerts.weather.gov/").get();
//                    String title = doc.title();
//                    Elements links = doc.select("a");
//
//                    for (org.jsoup.nodes.Element temp:links) {
//                        String url = temp.attr("href");
//                        if ( url.indexOf("http://alerts.weather.gov/cap/")!=-1){
//
//                            if (url.indexOf("?x=2")!=-1){
//                                //Log.v(LOG_NETWORK,url);
//                                //list  zone
//                                Document doc1 = Jsoup.connect(url).get();
//                                Elements a =  doc1.select("a");
//                                for (org.jsoup.nodes.Element temp1:a) {
//                                    String url1 = temp1.attr("href");
//                                    if (url1.indexOf("y=1")!=-1){
//                                        //Log.v(LOG_NETWORK, url1);
//                                        org.jsoup.nodes.Element titleElement = temp1.parent();
//                                        HashLocation hashLocation = new HashLocation();
//                                        hashLocation.setTitle(titleElement.nextElementSibling().text());
//                                        hashLocation.setHashcode(temp1.text());
//                                        realm.beginTransaction();
//                                        realm.copyToRealmOrUpdate(hashLocation);
//                                        realm.commitTransaction();
//                                    }
//
//                                }
//
//                            }
//                            if (url.indexOf("?x=3")!=-1){
//                               //Log.v(LOG_NETWORK,url );
//                                //list  county
//                                Document doc1 = Jsoup.connect(url).get();
//                                Elements a =  doc1.select("a");
//                                for (org.jsoup.nodes.Element temp1:a) {
//                                    String url1 = temp1.attr("href");
//                                    if (url1.indexOf("y=1")!=-1){
//                                        org.jsoup.nodes.Element titleElement = temp1.parent();
//                                        HashLocation hashLocation = new HashLocation();
//                                        hashLocation.setTitle(titleElement.nextElementSibling().text());
//                                        hashLocation.setHashcode(temp1.text());
//                                        realm.beginTransaction();
//                                        realm.copyToRealmOrUpdate(hashLocation);
//                                        realm.commitTransaction();
//                                    }
//
//                                }
//                            }
//                        }
//
//
//
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                realm.close();
                EventBus.getDefault().post(new FinishEvent());
            }

            private String run(String url) throws IOException {
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            }


        }).start();
    }








}
