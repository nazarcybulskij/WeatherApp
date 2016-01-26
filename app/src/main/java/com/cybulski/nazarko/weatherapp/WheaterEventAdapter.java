package com.cybulski.nazarko.weatherapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.cybulski.nazarko.weatherapp.model.HashLocation;
import com.cybulski.nazarko.weatherapp.model.WheaterEvent;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by Nazarko on 1/21/2016.
 */
public class WheaterEventAdapter  extends RealmBaseAdapter<WheaterEvent> implements ListAdapter {


    private static class ViewHolder {
        TextView name;
    }

    public WheaterEventAdapter(Context context, RealmResults<WheaterEvent> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
        int size = realmResults.size();
        Log.v("SIZE",size+"");
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1,
                    parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        WheaterEvent item = realmResults.get(position);
        viewHolder.name.setText(item.getTitle());
        return convertView;
    }

    public RealmResults<WheaterEvent> getRealmResults() {
        return realmResults;
    }
}
