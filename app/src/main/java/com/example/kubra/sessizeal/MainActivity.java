package com.example.kubra.sessizeal;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_ACCESS_FINE_LOCATION=1;
    private static final int RC_PLACE_PICKER=2;
    CustomAdapter adapter;

    GoogleApiClient mGoogleApiClient;
    ArrayList<Mekan> placeList=new ArrayList<Mekan>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

        ListView lv= (ListView) findViewById(R.id.listView);
        adapter=new CustomAdapter(MainActivity.this,placeList);
        lv.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        CheckBox checkBoxLoc = (CheckBox) findViewById(R.id.checkBoxLocation);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkBoxLoc.setChecked(false);
        } else {
            checkBoxLoc.setChecked(true);
            checkBoxLoc.setEnabled(false);
        }

    }

    public void locationPermissionClicked(View view){
    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},RC_ACCESS_FINE_LOCATION);}

public void lokasyonEkleButonuSecildi(View view){
    if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
        Toast.makeText(this,"Lokasyona Erişim İznini Vermeniz Gerekiyor",Toast.LENGTH_SHORT).show();
        return;}
    try {
        PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();
        Intent placePickerIntent=builder.build(this);
        startActivityForResult(placePickerIntent,RC_PLACE_PICKER);
    }
    catch (GooglePlayServicesRepairableException e) {
        Log.e("MainActivity", String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
    } catch (GooglePlayServicesNotAvailableException e) {
        Log.e("MainActivity", String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
    } catch (Exception e) {
        Log.e("MainActivity", String.format("PlacePicker Exception: %s", e.getMessage()));
    }
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==RC_PLACE_PICKER && resultCode==RESULT_OK){
            Place secilenLokasyon=PlacePicker.getPlace(this,data);
            if(secilenLokasyon==null){
                Toast.makeText(this,"Lokasyon Seçilmedi",Toast.LENGTH_SHORT).show();
                return;
            }
            String secilenLokasyonId=secilenLokasyon.getId();
            veritabanınaEkle(secilenLokasyonId);
            placeIdListele();
        }
    }
    private void veritabanınaEkle(String secilenLokasyonId) {
        Database db=new Database(this);
        db.lokasyonIdEkle(secilenLokasyonId);

    }

    private void placeIdListele(){

        placeList.clear();
        Database db=new Database(this);
        Cursor data=db.getPlaceData();
        while (data.moveToNext()){
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, data.getString(1))
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                              placeList.add(new Mekan(places.get(0).getName().toString(),places.get(0).getAddress().toString()));
                            } else {
                               Log.e("Main", "Lokasyon bulunamadı");
                            }
                            adapter.notifyDataSetChanged();
                            places.release();

                        }

                    });
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        placeIdListele();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Main","onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Main","onConnectionFailed");

    }
}
