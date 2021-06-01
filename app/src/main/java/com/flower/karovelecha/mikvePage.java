package com.flower.karovelecha;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class mikvePage extends AppCompatActivity {

    String id;
    Button diractionButton;
    ImageButton menu;
    TextView nameTextview, detailTextview, contactsTextview, openingHours, cost;
    private CustomMikveList mikveData;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mikve_page);

        DataClass appState = (DataClass) this.getApplication();
        mikveData = appState.getMikvearr();

        if(mikveData == null)
        {
            Intent reStart = new Intent(getBaseContext(), OpeningActivity.class);
            startActivity(reStart);
            finish();
        }

        Intent data = getIntent();
        id = data.getStringExtra("id");

        nameTextview = (TextView) findViewById(R.id.synagogaName);
        nameTextview.setText(mikveData.getMikveById(id).getSubject());

        detailTextview = (TextView) findViewById(R.id.detail);
        detailTextview.setText(mikveData.getMikveById(id).getLocation() + ", " +
                "כתובת: " + mikveData.getMikveById(id).getAddress() + ", " +
                mikveData.getMikveById(id).getGender());

        contactsTextview = (TextView) findViewById(R.id.data);
        openingHours = (TextView) findViewById(R.id.extra_data);
        cost = (TextView) findViewById(R.id.cost);

        String Timeforatoe = byReCalling(mikveData.getMikveById(id).getTimeforatoeText()),
                Timeforf = byReCalling(mikveData.getMikveById(id).getTimeforf()),
                Timeforg = byReCalling(mikveData.getMikveById(id).getTimeforg());



        contactsTextview.setText(mikveData.getMikveById(id).getPhone().replace("G",""));
        openingHours.setText("\n\n"+"שעות פתיחה:\nימי חול: " + Timeforatoe+
                "\n" + "ערב שבת: " + Timeforf +
                "\n" + "מוצ''ש: " + Timeforg);

        String ShekellTitle = " ש''ח.";
        if(mikveData.getMikveById(id).getPrice().equals("כניסה חופשית"))
            ShekellTitle = "";

        cost.setText("מחיר טבילה: " + mikveData.getMikveById(id).getPrice() + ShekellTitle);

        diractionButton = (Button) findViewById(R.id.diraction);
        diractionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(mikvePage.this, "הנחיות נסיעה",
                        Toast.LENGTH_SHORT).show();

                String []address = new String[2];
                address[0] = mikveData.getMikveById(id).getLocation();
                address[1] = mikveData.getMikveById(id).getAddress();

                if(address[0] != null && address[1] != null) {
                    String uri = "geo:0,0?q=" + address[0] + ", " + address[1] + "&navigate=yes";

                    //String uri = "geo://?q=" + address.substring(address.lastIndexOf("מיקום: ")+5, address.indexOf("בימים:")-2).replace(", ", "%20")+"&navigate=yes";
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                }
            }
        });

        menu = (ImageButton) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public String byReCalling(String timeString)
    {
        if(timeString.contains("בתאום מראש") || timeString.contains("בתיאום מראש"))
            return "בתאום מראש";
        else return timeString;
    }

}
