package com.flower.karovelecha;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class mikveSearch extends AppCompatActivity implements GeoTask.Geo , LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ProgressDialog pd;
    private DatabaseReference mDatabase;
    CustomMikveList Data, closestData, LocatedData;
    private ImageButton add, searchOne;
    private ListView data, moredata;
    private List<MikveObject> Eventarr = null;
    private List settlements = new ArrayList();
    Location location; // location
    double latitude = DataClass.latitude; // latitude
    double longitude = DataClass.longitude; // longitude
    private String MyLocation = DataClass.MyLocation;
    Boolean hasDataCalled = false;
    Spinner search;
    int height, width;
    private boolean scrolling = false;
    WheelView wheelspinner;
    GoogleApiClient client;

    @Override
    protected void attachBaseContext(Context newBase) {
        //super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mikve_search);

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

        //getLocation();
        //MyLocation = "אלון שבות";
        //hasLocation();

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
        Data = appState.getMikvearr();
        if(Eventarr != null)
            hasData(true);
        else {
            mDatabase.child("allData").child("mikve").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            //List<Event> ev = dataSnapshot.getValue(Event.class);
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //<Event> ev = dataSnapshot.getValue();
                                GenericTypeIndicator<List<MikveObject>> t = new GenericTypeIndicator<List<MikveObject>>() {
                                };
                                Eventarr = dataSnapshot.getValue(t);
                            }
                            List settlements = mikveSearch.this.settlements;
                            Data = new CustomMikveList(Eventarr, mikveSearch.this);
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
        CustomList adapter;
        hasDataCalled = true;
        String [] empty = {"אין מידע, שנה חיפוש"}, none = {""};
        CustomMikveList Data = this.Data;
        List settlements = this.settlements;
        if(MyLocation == null) MyLocation = "empty";

        if (isData && !MyLocation.equals("empty")) {
            if(MyLocation.equals("searched") || !Data.needToSearch(MyLocation)) {
                Data = new CustomMikveList(Data.SortByDistance(), mikveSearch.this);
                closestData = Data;

                if(Data.getEvents().size()!=0) {
                    adapter = new CustomList(mikveSearch.this, Data.tostring(), Data.Oclock(),
                            3, Data.getAllIds());
                    data.setAdapter(adapter);
                }
                else noResults();
//                searchMore(this.Data);
                pd.dismiss();
            }
            else {
                MyLocation = latitude + "," + longitude;
                Data.FindClosest(mikveSearch.this, MyLocation);
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
        CustomList adapter = new CustomList(this, empty, none,3, null);
        data.setAdapter(adapter);
    }

    public void initialize()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        height = metrics.heightPixels;
        width = metrics.widthPixels;
        width = metrics.widthPixels;

        settlements = Arrays.asList(getResources().getStringArray(R.array.settlements));

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

        RelativeLayout.LayoutParams lp;

        data= (ListView) findViewById(R.id.data);
        //lp = (RelativeLayout.LayoutParams) data.getLayoutParams();
        //lp.height = height/3;
        //data.setLayoutParams(lp);

        data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String address = data.getItemAtPosition(position).toString();
                if (!address.equals("אין מידע, שנה חיפוש")) {
                    Intent i = new Intent(getBaseContext(), mikvePage.class);
                    i.putExtra("id", closestData.getEvent(position).getSubject() + "+" +
                            closestData.getEvent(position).getAddress());
                    startActivity(i);
                    //mikveSearch.this.finish();
                }
            }
        });

        List settlements = Arrays.asList(getResources().getStringArray(R.array.settlements));

        wheelspinner = (WheelView) findViewById(R.id.search);

        wheelspinner.setVisibleItems(5);
        //country.setViewAdapter(new CountryAdapter(this));

        ArrayWheelAdapter<String> adapter =
                new ArrayWheelAdapter<String>(this, returnarray(settlements));
        adapter.setTextSize(18);
        wheelspinner.setViewAdapter(adapter);

        wheelspinner.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    //updateCities(city, cities, newValue);
//                    if(Data!=null)searchMore(Data);
                }
            }
        });

        wheelspinner.addScrollingListener( new OnWheelScrollListener() {
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }
            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                //updateCities(city, cities, country.getCurrentItem());
//                if(Data!=null)searchMore(Data);
            }
        });
/*
        search = (Spinner)findViewById(R.id.location);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, settlements);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        search.setAdapter(dataAdapter);
        lp = (RelativeLayout.LayoutParams) search.getLayoutParams();
        lp.width = width/3;
        search.setLayoutParams(lp);

            search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    Intent i = new Intent(getBaseContext(), mikvePage.class);
                    i.putExtra("id", LocatedData.getEvent(position).getSubject() + "+" +
                            LocatedData.getEvent(position).getAddress());
                    startActivity(i);
                    //mikveSearch.this.finish();
                }
            }
        });
    }

    public void refreshLists()
    {
        settlements = Arrays.asList(getResources().getStringArray(R.array.settlements));
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

    public void searchMore(CustomMikveList Data) {
        //Data = new CustomTfilotList(Eventarr, settlements);
        CustomList adapter;
        refreshLists();

        LocatedData = new CustomMikveList(Data.allToday(settlements.get(wheelspinner.getCurrentItem()).toString()),
                mikveSearch.this);

        adapter = new CustomList(mikveSearch.this, LocatedData.tostring(), LocatedData.Oclock(),
                3, LocatedData.getAllIds());
        moredata.setAdapter(adapter);
    }

//    @Override
//    public void GetDuration(String min) {
//        if(!min.equals("err")) {
//            String res[]=min.split(",");
//            Double time= Double.parseDouble(res[0])/60;
//            Data.AddDuration(time);
//        }
//        else
//            Data.AddDuration(10000.000);
//        if(Data.isfull()) {
//            MyLocation = Data.getClosetName();
//            hasData(true);
//
//            //adapter = new CustomList(tfilaSearch.this, Data.tostring());
//            //data.setAdapter(adapter);
//        }
//    }

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
        if(latitude!=0&&latitude!=0) {
            if (cityName == null || cityName == "empty") MyLocation = latitude + "," + longitude;
            else MyLocation = cityName;

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
            Toast.makeText(mikveSearch.this, "שגיאה בחיפוש מקום קרוב, אנא נסה מאוחר יותר.", Toast.LENGTH_LONG).show();
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