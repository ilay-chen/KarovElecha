package com.flower.karovelecha;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flower.karovelecha.wheelViewLabary.OnWheelChangedListener;
import com.flower.karovelecha.wheelViewLabary.OnWheelScrollListener;
import com.flower.karovelecha.wheelViewLabary.WheelView;
import com.flower.karovelecha.wheelViewLabary.adapters.ArrayWheelAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class shiurSearch extends AppCompatActivity implements GeoTask.Geo , LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ProgressDialog pd;
    private DatabaseReference mDatabase;
    CustomShiurimList Data, sortedData, MoreData;
    private ImageButton add, searchOne, searchDateButton;
    private ListView data, moredata;
    private List settlements = new ArrayList();
    List kinds = new ArrayList();
    Location location; // location
    double latitude = DataClass.latitude; // latitude
    double longitude = DataClass.longitude; // longitude
    private String MyLocation = DataClass.MyLocation;
    Boolean hasDataCalled = false;
    //Spinner searchOne[] = new Spinner[2];
    // Scrolling flag
    private boolean scrolling = false;
    WheelView settlementspinner, kindspinner;
    GoogleApiClient client;
    private String searchDate;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiur_search);

        WheelView.color = 1;

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER )
                && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            buildAlertMessageNoGps();

        initialize();

        pd=new ProgressDialog(this);
        pd.setMessage("מחפש מיקום..");
        pd.setCancelable(false);
        pd.show();


        if(latitude!=0&&longitude!=0)
            hasLocation();
        else
        {
            buildGoogleApiClient();
            client.connect();
        }

    }

    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    public void buildAlertMessageNoGps() {
        Toast.makeText(this, "בבקשה הדלק gps", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
    }

    public void hasLocation() {
        //get List of Events
        if(client != null && client.isConnected())
            stopLocationUpdates();

        DataClass appState = (DataClass) getApplicationContext();
        Data = appState.getShiurtarr();
        if(Data != null)
            hasData(true);
        else {
            mDatabase.child("allData").child("synagoga").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<SynagogaObject> Eventarr = null;
                            // Get user value
                            //List<Event> ev = dataSnapshot.getValue(Event.class);
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //<Event> ev = dataSnapshot.getValue();
                                GenericTypeIndicator<List<SynagogaObject>> t = new GenericTypeIndicator<List<SynagogaObject>>() {
                                };
                                Eventarr = dataSnapshot.getValue(t);
                            }
                            Data = new CustomSynagogaList(Eventarr).getAllShiurim(shiurSearch.this);
                            if (!hasDataCalled)
                                if (Eventarr == null)
                                    hasData(false);
                                else
                                    hasData(true);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            // ...
                        }
                    });
        }
    }

    public void hasData(boolean isData) {
        CustomList adapter = null;
        hasDataCalled = true;
        String [] empty = {"אין מידע, שנה חיפוש"}, none = {""};
        sortedData = this.Data;
        List settlements = this.settlements;
        if(MyLocation == null) MyLocation = "empty";

        if (isData && !MyLocation.equals("empty")) {
            if(MyLocation.equals("searched") || !sortedData.needToSearch(MyLocation)) {
                sortedData = new CustomShiurimList(sortedData.SortByDistance(), this);

                if(sortedData.getEvents().size()!=0) {
                    adapter = new CustomList(shiurSearch.this, sortedData.tostring(), sortedData.Oclock(),
                            2, sortedData.getAllIds());
                    data.setAdapter(adapter);
                }
                else noResults();

                pd.dismiss();
            }
            else {
                MyLocation = latitude + "," + longitude;
                sortedData.FindClosest(shiurSearch.this, MyLocation);
            }
        }
        else {
            noResults();
            pd.dismiss();
        }
    }

    public void noResults()
    {
        String[] empty = {"אין מידע, שנה חיפוש"}, none = {""};
        CustomList adapter = new CustomList(this, empty, none, 2, null);
        data.setAdapter(adapter);
    }

    public void initialize()
    {
        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        add= (ImageButton) findViewById(R.id.menu);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        searchOne = (ImageButton) findViewById(R.id.searchOne);
        searchOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Data != null) searchMore(Data);
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int height = metrics.heightPixels;

        kinds.add("הכל");
        kinds.add("חסידות");
        kinds.add("דף יומי");
        kinds.add("הלכה");
        kinds.add("עיון");
        kinds.add("מדרש ואגדה");
        kinds.add("לנשים בלבד");

        searchDateButton = (ImageButton) findViewById(R.id.searchdate);
        int month = Calendar.getInstance().get(Calendar.MONTH)+1;
        searchDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)+"-"+month+"-"+Calendar.getInstance().get(Calendar.YEAR);

        searchDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour;
                int minute;
                if(false) {
                    //get the data and show it
                    //hour = Integer.parseInt(from.getText().toString().split(":")[0]);
                    //minute = Integer.parseInt(from.getText().toString().split(":")[1]);
                }
                else {
                    hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    minute = mcurrentTime.get(Calendar.MINUTE);
                }
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(shiurSearch.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        searchDate = dateFormatter.format(newDate.getTime());
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle("בחר תאריכים");
                mDatePicker.show();
            }
        });

        RelativeLayout.LayoutParams lp;

        data= (ListView) findViewById(R.id.data);


        data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String address = data.getItemAtPosition(position).toString();
                if (!address.equals("אין מידע, שנה חיפוש")) {
                    ShiurObject shiur = sortedData.getEvent(position);
                    Intent i = new Intent(getBaseContext(), SynagogaPage.class);
                    //i.putExtra("Id", address.split(", ")[2] +"+"+ address.split(", ")[1]);
                    i.putExtra("Id", shiur.getLocation() +"+"+ shiur.getSynagogaName());
                    startActivity(i);
                    //shiurSearch.this.finish();
                }
            }
        });
        settlements =Arrays.asList(getResources().getStringArray(R.array.settlements));
        List settlements = Arrays.asList(getResources().getStringArray(R.array.settlements));

        List kinds = new ArrayList();
        //kinds.add("הכל");
        kinds.add("הכל");
        kinds.add("חסידות");
        kinds.add("דף יומי");
        kinds.add("הלכה");
        kinds.add("עיון");
        kinds.add("מדרש ואגדה");
        kinds.add("לנשים בלבד");

        settlementspinner = (WheelView) findViewById(R.id.country);
        kindspinner = (WheelView) findViewById(R.id.city);


        settlementspinner.setVisibleItems(5);
        kindspinner.setVisibleItems(5);
        //country.setViewAdapter(new CountryAdapter(this));

        ArrayWheelAdapter<String> adapter =
                new ArrayWheelAdapter<String>(this, returnarray(settlements));
        adapter.setTextSize(18);
        settlementspinner.setViewAdapter(adapter);

        ArrayWheelAdapter<String> adapter2 =
                new ArrayWheelAdapter<String>(this, returnarray(kinds));
        adapter2.setTextSize(18);
        kindspinner.setViewAdapter(adapter2);

        settlementspinner.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    //updateCities(city, cities, newValue);
//                    if(Data!=null)searchMore(Data);
                }
            }
        });

        settlementspinner.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }
            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                //updateCities(city, cities, country.getCurrentItem());
//                if(Data!=null)searchMore(Data);
            }
        });

        kindspinner.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    //updateCities(city, cities, newValue);
//                    if(Data!=null)searchMore(Data);
                }
            }
        });

        kindspinner.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }
            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                //updateCities(city, cities, country.getCurrentItem());
//                if(Data!=null)searchMore(Data);
            }
        });

        //settlementspinner.setCurrentItem(1);
/*
        ArrayAdapter<String> dataAdapter;
        searchOne[0] = (Spinner)findViewById(R.id.location);
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, settlements);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchOne[0].setAdapter(dataAdapter);
        lp = (RelativeLayout.LayoutParams) searchOne[0].getLayoutParams();
        lp.width = width/3;
        searchOne[0].setLayoutParams(lp);

        searchOne[1] = (Spinner)findViewById(R.id.kind);
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, kinds);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchOne[1].setAdapter(dataAdapter);
        lp = (RelativeLayout.LayoutParams) searchOne[1].getLayoutParams();
        lp.width = width/3;
        searchOne[1].setLayoutParams(lp);

        for(int i = 0; i < searchOne.length; i++)
            searchOne[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(Data!=null)searchMore(Data);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // sometimes you need nothing here
                }
            });
            */

        moredata = (ListView)findViewById(R.id.moredata);

        moredata.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String address = moredata.getItemAtPosition(position).toString();
                if (!address.equals("אין מידע")) {
                    ShiurObject shiur = MoreData.getEvent(position);
                    Intent i = new Intent(getBaseContext(), SynagogaPage.class);
                    //i.putExtra("Id", address.split(", ")[2] +"+"+ address.split(", ")[1]);
                    i.putExtra("Id", shiur.getLocation() +"+"+ shiur.getSynagogaName());
                    startActivity(i);
                }
            }
        });
    }

    public void refreshLists()
    {
        settlements =Arrays.asList(getResources().getStringArray(R.array.settlements));

        kinds.add("הכל");
        kinds.add("חסידות");
        kinds.add("דף יומי");
        kinds.add("הלכה");
        kinds.add("עיון");
        kinds.add("מדרש ואגדה");
        kinds.add("לנשים בלבד");
    }

    public String[] returnarray(List<String> list)
    {
        String newarray[] = new String[list.size()];
        for(int i = 0; i < list.size(); i++)
        {
            newarray[i] = list.get(i).toString();
        }

        return newarray;
    }

    public void searchMore(CustomShiurimList Data) {
        //Data = new CustomTfilotList(Eventarr, settlements);
        CustomList adapter;
        MoreData = Data;
        if(MoreData.getEvents() == null) {
            String []empty = {"אין מידע"}, none = {""};
            adapter = new CustomList(this, empty, none, 2, null);
            moredata.setAdapter(adapter);
            return;
        }
        refreshLists();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar newDate = Calendar.getInstance();

        MoreData = new CustomShiurimList(MoreData.allToday(kinds.get(kindspinner.getCurrentItem()).toString(),
                settlements.get(settlementspinner.getCurrentItem()).toString(),
                searchDate), this);

        adapter = new CustomList(shiurSearch.this, MoreData.tostring(), MoreData.Oclock(),
                2, MoreData.getAllIds());
        moredata.setAdapter(adapter);
    }

    @Override
    public void onLocationChanged(Location location) {
        String cityName = "empty";
        this.location = location;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(latitude!=0&&longitude!=0) {
            if (cityName == null) {
                MyLocation = latitude + "," + longitude;
            } else if (cityName.equals("empty")) {
                MyLocation = latitude + "," + longitude;
            } else {
                MyLocation = cityName;
            }

            hasLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(client != null && client.isConnected())
            stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                client, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (client != null && client.isConnected()) {
            //getLocation();
            buildGoogleApiClient();
            client.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void GetDuration(List<String> min) {
        if (min != null) {
            for (int i = 0; i < min.size(); i++)
            {
                Double time = Double.parseDouble(min.get(i)) / 60;
                Data.AddDuration(time);
            }
        } else
        {
            //error
            Data.AddDuration(1000.0);
            Toast.makeText(shiurSearch.this, "שגיאה בחיפוש מקום קרוב, אנא נסה מאוחר יותר.", Toast.LENGTH_LONG).show();
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
        if (Data.isfull()) {
            MyLocation = "searched";
            hasData(true);
        }
    }
}

