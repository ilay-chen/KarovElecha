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

public class SynagogaPage extends AppCompatActivity {

    String Id;
    Button diractionButton;
    ImageButton menu;
    TextView nameTextview, detailTextview, ravTextview, gabaiTextView;
    private CustomSynagogaList SynagagaData;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synagoga_page);

        DataClass appState = (DataClass) this.getApplication();
        SynagagaData = appState.getSynagogaarr();

        if(SynagagaData == null)
        {
            Intent reStart = new Intent(getBaseContext(), OpeningActivity.class);
            startActivity(reStart);
            finish();
        }

        Intent data = getIntent();
        Id = data.getStringExtra("Id");

        nameTextview = (TextView) findViewById(R.id.synagogaName);
        nameTextview.setText("שם בית הכנסת: " + SynagagaData.getSynagogaById(Id).getName());

        detailTextview = (TextView) findViewById(R.id.detail);
        detailTextview.setText(SynagagaData.getSynagogaById(Id).getLocation()
                + ", כתובת: " + SynagagaData.getSynagogaById(Id).getAddress());

        ravTextview = (TextView) findViewById(R.id.data);
        gabaiTextView = (TextView) findViewById(R.id.extra_data);

        String rav = SynagagaData.getSynagogaById(Id).getRav();
        String gabaiim = SynagagaData.getSynagogaById(Id).getGabaiimInText();

        if(rav!=null && !rav.equals("")) {
            ravTextview.setText(rav);
        }
        else ravTextview.setText("");

        if (gabaiim!=null &&! gabaiim.equals(""))
        {
            gabaiTextView.setText(gabaiim);
        }
        else    gabaiTextView.setText("");

        diractionButton = (Button) findViewById(R.id.diraction);
        diractionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(SynagogaPage.this, "הנחיות נסיעה",
                        Toast.LENGTH_SHORT).show();

                String []address = null;
                    address = SynagagaData.getSynagogaById(Id).toString().toString().replace("בית כנסת: ","").split(", ");

                if(address != null && address.length > 2) {
                    String uri = "geo:0,0?q=" + address[1] + ", " + address[2] + "&navigate=yes";
                    if (SynagagaData != null && SynagagaData.getEvents().size() != 0)
                        if (SynagagaData.getSynagogaById(address[2] + "+" + address[1]) != null)
                            if(Integer.parseInt(SynagagaData.getSynagogaById(address[2] + "+" + address[1]).getLatitude()) != 0
                                    && Integer.parseInt(SynagagaData.getSynagogaById(address[2] + "+" + address[1]).getLongitude()) != 0) {
                                uri = "geo:0,0?q=" + SynagagaData.getSynagogaById(address[2] + "+" + address[1]).getLatitude() +
                                        ", " + SynagagaData.getSynagogaById(address[2] + "+" + address[1]).getLongitude() + "&navigate=yes";
                            }
                            else
                                uri = "geo:0,0?q=" + address[3] + ", " + address[2] + "&navigate=yes";
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

}
