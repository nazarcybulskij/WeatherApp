package com.cybulski.nazarko.weatherapp;

import android.content.Context;
import android.renderscript.Element;
import android.util.Log;
import android.widget.Toast;

import com.cybulski.nazarko.weatherapp.event.FinishEvent;
import com.cybulski.nazarko.weatherapp.event.StartEvent;
import com.cybulski.nazarko.weatherapp.model.HashLocation;
import com.cybulski.nazarko.weatherapp.model.WheaterEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;
import java.util.ArrayList;
import java.util.HashMap;

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
                    client = new OkHttpClient();
                    String resultstr = run("http://ip.jsontest.com/");
                    realm.createAllFromJson(HashLocation.class,new JSONArray(resultstr));


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
                            HashLocation hashLocation = realm.where(HashLocation.class).equalTo("hashcode",geocode.text()).findFirst();
                            if (hashLocation!=null){
                                list.add(hashLocation);
                            }

                        }
                        wheaterEvent.setLocations(list);

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(wheaterEvent);
                        realm.commitTransaction();
                    }
                    realmevent.close();
                    RealmResults<HashLocation> result = realm.where(HashLocation.class).findAll();







                    if (result.size()>6000){
                        ArrayList<HashMap> wordList;
                        wordList = new ArrayList<>();
                        for (HashLocation temphash:result){
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("title", temphash.getTitle());
                            map.put("hashcode",temphash.getHashcode());
                            wordList.add(map);
                        }
                        Gson gson = new GsonBuilder().create();
                        String json = gson.toJson(wordList);
                        realm.close();
                        EventBus.getDefault().post(new FinishEvent());
                        return;
                    }


                    doc = Jsoup.connect("http://alerts.weather.gov/").get();
                    String title = doc.title();
                    Elements links = doc.select("a");

                    for (org.jsoup.nodes.Element temp:links) {
                        String url = temp.attr("href");
                        if ( url.indexOf("http://alerts.weather.gov/cap/")!=-1){

//                             if (url.indexOf("?x=1")!=-1){
//                                 //Log.v(LOG_NETWORK, url);
//                                 //list  event  state
//                                 Document doc1 = Jsoup.connect(url).get();
//                                 Elements links1 =  doc1.select("link");
//                                 Realm realmevent = Realm.getInstance(mContext);
//                                 for (org.jsoup.nodes.Element temp1:links1) {
//                                     String url1 = temp1.attr("href");
//                                     if (!(url1.indexOf(".atom")!=-1)){
//                                         Document docevent = Jsoup.connect(url1).parser(Parser.xmlParser()).get();
//                                         WheaterEvent wheaterEvent = new WheaterEvent();
//                                         wheaterEvent.setIdentifier(docevent.select("identifier").text());
//                                         Log.v(LOG_NETWORK,docevent.select("identifier").text());
//                                         realm.beginTransaction();
//                                         realm.copyToRealmOrUpdate(wheaterEvent);
//                                         realm.commitTransaction();
//                                     }
//                                 }
//                             }



                            if (url.indexOf("?x=2")!=-1){
                                //Log.v(LOG_NETWORK,url);
                                //list  zone
                                Document doc1 = Jsoup.connect(url).get();
                                Elements a =  doc1.select("a");
                                for (org.jsoup.nodes.Element temp1:a) {
                                    String url1 = temp1.attr("href");
                                    if (url1.indexOf("y=1")!=-1){
                                        //Log.v(LOG_NETWORK, url1);
                                        org.jsoup.nodes.Element titleElement = temp1.parent();
                                        HashLocation hashLocation = new HashLocation();
                                        hashLocation.setTitle(titleElement.nextElementSibling().text());
                                        hashLocation.setHashcode(temp1.text());
                                        realm.beginTransaction();
                                        realm.copyToRealmOrUpdate(hashLocation);
                                        realm.commitTransaction();
                                    }

                                }

                            }
                            if (url.indexOf("?x=3")!=-1){
                               //Log.v(LOG_NETWORK,url );
                                //list  county
                                Document doc1 = Jsoup.connect(url).get();
                                Elements a =  doc1.select("a");
                                for (org.jsoup.nodes.Element temp1:a) {
                                    String url1 = temp1.attr("href");
                                    if (url1.indexOf("y=1")!=-1){
                                        org.jsoup.nodes.Element titleElement = temp1.parent();
                                        HashLocation hashLocation = new HashLocation();
                                        hashLocation.setTitle(titleElement.nextElementSibling().text());
                                        hashLocation.setHashcode(temp1.text());
                                        realm.beginTransaction();
                                        realm.copyToRealmOrUpdate(hashLocation);
                                        realm.commitTransaction();
                                    }

                                }
                            }
                        }



                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
