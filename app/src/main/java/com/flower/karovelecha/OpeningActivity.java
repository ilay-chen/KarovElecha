package com.flower.karovelecha;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class OpeningActivity extends AppCompatActivity {

    private List<TfilaObject> Tfilaarr;
    private List<ShiurObject> Shiurtarr;
    private List<MikveObject> mikvearr;
    private List<SynagogaObject> synagogaarr;
    private DatabaseReference mDatabase;
    private Boolean isfinish = false, openMassege = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getSynagoga();

        Intent notification = getIntent();
        saveNotification(notification);
    }

    public void saveNotification(Intent notification)
    {
        String data = notification.getStringExtra("data");

        if(data!=null) {
            SharedPreferences sharedPref = this.getSharedPreferences(
                    getString(R.string.notification),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            //SharedPreferences.Editor editor2 = preferences.edit();

            String oldData = sharedPref.getString(getString(R.string.notificationData), "");
            editor.putString(getString(R.string.notificationData), oldData + data + "D");
            //editor.putString(getString(R.string.silence), "true,10:30,1-2-3,true=1,false,true|");
            editor.commit();
            openMassege = true;
        }
    }

    public void getSynagoga() {
        mDatabase.child("allData").child("synagoga").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        //List<Event> ev = dataSnapshot.getValue(Event.class);
                     //   for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            //<Event> ev = dataSnapshot.getValue();
                            GenericTypeIndicator<List<SynagogaObject>> sy = new GenericTypeIndicator<List<SynagogaObject>>() {};
                            synagogaarr = dataSnapshot.getValue(sy);
                            getMikve();

                    //    }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // ...
                    }
                });

    }

    public void getMikve() {
        mDatabase.child("allData").child("mikve").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        //List<Event> ev = dataSnapshot.getValue(Event.class);
                    //    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            //<Event> ev = dataSnapshot.getValue();
                            GenericTypeIndicator<List<MikveObject>> mi = new GenericTypeIndicator<List<MikveObject>>() {
                            };
                            mikvearr = dataSnapshot.getValue(mi);
                            checkFinish();
                    //    }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // ...
                    }
                });
    }

    public void checkFinish()
    {
        CustomTfilotList Tfilaarr = null;
        CustomShiurimList Shiurtarr = null;
        CustomMikveList mikvearr = null;
        CustomSynagogaList synagogaarr = null;

        synagogaarr =  new CustomSynagogaList(this.synagogaarr);

        if(synagogaarr.getAllTfilot(this)!=null)
        Tfilaarr = synagogaarr.getAllTfilot(this);

        if(synagogaarr.getAllShiurim(this)!=null)
        Shiurtarr = synagogaarr.getAllShiurim(this);

        mikvearr = new CustomMikveList(this.mikvearr, this);

        DataClass appState = ((DataClass)getApplicationContext());
        appState.Initialize(Tfilaarr, Shiurtarr, mikvearr, synagogaarr);
        this.finish();

        if(!isfinish) {
            isfinish = true;
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.putExtra("massege", openMassege);
            startActivity(i);
        }
    }
}
