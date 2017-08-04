package com.example.kubra.sessizeal;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
public class CustomAdapter extends BaseAdapter {
    ArrayList<Mekan>places;
    LayoutInflater layoutInflater;

    public CustomAdapter(Activity activity, ArrayList<Mekan>places){
    this.places=places;
    layoutInflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);}
    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Object getItem(int position) {
        return places.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View satir=layoutInflater.inflate(R.layout.custom_satir,null);

        Mekan place=places.get(position);
        TextView adTv= (TextView) satir.findViewById(R.id.textViewYerAdi);
        adTv.setText(place.getIsim());
        TextView adresTv= (TextView) satir.findViewById(R.id.textViewAdres);
        adresTv.setText(place.getAdres());

        return satir;
    }
}
