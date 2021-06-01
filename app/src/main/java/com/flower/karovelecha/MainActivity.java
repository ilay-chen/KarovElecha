package com.flower.karovelecha;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.flower.karovelecha.DataClass.locationChanged;

public class MainActivity extends AppCompatActivity implements LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    ImageButton tfilot, shiurim, ishuv, mikve, contact, massage, publish;
    Button info;
    ImageView moaza;
    private StorageReference mStorageRef;
    GoogleApiClient client;
    Location location; // location
    double latitude = DataClass.latitude; // latitude
    double longitude = DataClass.longitude; // longitude
    private String MyLocation = DataClass.MyLocation;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Intent intent = getIntent();
        if(intent.getBooleanExtra("massege", false))
        {
            Intent i = new Intent(getBaseContext(), massage.class);
            startActivity(i);
            finish();
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_INTERNET);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER )
                && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        buildGoogleApiClient();
        client.connect();


        tfilot = (ImageButton) findViewById(R.id.tfila_search);

        tfilot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), tfilaSearch.class);
                startActivity(i);
                finish();
            }
        });
//        lp = (RelativeLayout.LayoutParams) tfilot.getLayoutParams();
//        lp.height = width/2;
//        lp.width = width/2;
        //tfilot.setLayoutParams(lp);

        shiurim = (ImageButton) findViewById(R.id.shiur_search);

        shiurim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), shiurSearch.class);
                startActivity(i);
                finish();
            }
        });
//        lp = (RelativeLayout.LayoutParams) shiurim.getLayoutParams();
//        lp.height = width/2;
//        lp.width = width/2;
        //shiurim.setLayoutParams(lp);

        ishuv = (ImageButton) findViewById(R.id.ishuv_search);

        ishuv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ishuvSearch.class);
                startActivity(i);
                finish();
            }
        });
//        lp = (RelativeLayout.LayoutParams) ishuv.getLayoutParams();
//        lp.height = width/2;
//        lp.width = width/2;
        //ishuv.setLayoutParams(lp);

        mikve = (ImageButton) findViewById(R.id.mikve_search);

        mikve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), mikveSearch.class);
                startActivity(i);
                finish();
            }
        });
//        lp = (RelativeLayout.LayoutParams) mikve.getLayoutParams();
//        lp.height = width/2;
//        lp.width = width/2;
        //mikve.setLayoutParams(lp);

        contact = (ImageButton) findViewById(R.id.contact_button);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), contact.class);
                startActivity(i);
                finish();
            }
        });
//        lp = (RelativeLayout.LayoutParams) contact.getLayoutParams();
//        lp.height = width/2;
//        lp.width = width/2;
        //contact.setLayoutParams(lp);

        massage = (ImageButton) findViewById(R.id.massage_button);

        massage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), massage.class);
                startActivity(i);
                finish();
            }
        });
//        lp = (RelativeLayout.LayoutParams) massage.getLayoutParams();
//        lp.height = width/2;
//        lp.width = width/2;
        //massage.setLayoutParams(lp);

        publish = (ImageButton) findViewById(R.id.publish_button);

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), hakdasha.class);
                startActivity(i);
                finish();
            }
        });


        info = (Button) findViewById(R.id.setting);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), info.class);
                startActivity(i);
                finish();
            }
        });

//        lp = (RelativeLayout.LayoutParams) publish.getLayoutParams();
//        lp.height = width/2;
//        lp.width = width/2;
        //publish.setLayoutParams(lp);

        moaza = (ImageView) findViewById(R.id.moazaimage);

//        lp = (RelativeLayout.LayoutParams) moaza.getLayoutParams();
//        lp.height = width/2;
//        lp.width = width/2;
//        moaza.setLayoutParams(lp);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("image.png");

        File localFile = null;
        try {
            localFile = File.createTempFile("image", "png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File finalLocalFile = localFile;
        riversRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());

                        ImageView myImage = (ImageView) findViewById(R.id.publishimage);

                        //myImage.setImageBitmap(myBitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });


    }

    public void buildAlertMessageNoGps() {
        Toast.makeText(this, "בבקשה הדלק gps", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (client!=null)
        if(client.isConnected())
            stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                client, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (client!=null)
        if (client.isConnected()) {
            //getLocation();
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("יציאה")
                .setMessage("בטוח שברצונך לצאת?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private static final int REQUEST_INTERNET = 200;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_INTERNET) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //start audio recording or whatever you planned to do
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("הרשאה זו חשובה למציאת מיקומך.")
                            .setTitle("הרשאה חשובה נצרכת");
                    builder.setPositiveButton("אוקיי", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_INTERNET);
                        }
                    });
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_INTERNET);
                } else {
                    //Never ask again and handle your app without permission.
                }
            }
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
    public void onLocationChanged(Location location) {
        locationChanged = true;
        String cityName = "empty";
        this.location = location;
        if (location != null) {
            DataClass.latitude = location.getLatitude();
            DataClass.longitude = location.getLongitude();

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
        if(DataClass.latitude!=0&&DataClass.latitude!=0) {
            if (cityName == null) {
                DataClass.MyLocation = DataClass.latitude + "," + DataClass.longitude;
            } else if (cityName.equals("empty")) {
                DataClass.MyLocation = DataClass.latitude + "," + DataClass.longitude;
            } else {
                DataClass.MyLocation = cityName;
            }
        }
    }
}
