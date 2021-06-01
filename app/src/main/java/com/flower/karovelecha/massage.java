package com.flower.karovelecha;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class massage extends AppCompatActivity {
    String oldData;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);

        Intent notification = getIntent();
        saveNotification(notification);

        ImageButton menu= (ImageButton) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.message);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, final View arg1, final int position, long arg3) {
                if(listView.getSelectedItem()!= null)
                if(!listView.getSelectedItem().toString().equals("אין הודעות")) {

                    Log.i("hh", "onListItemClick id=" + position);
                    new AlertDialog.Builder(massage.this)
                            .setTitle("מחיקה")
                            .setMessage("האם למחוק אובייקט זה?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences sharedPref = getSharedPreferences(
                                            getString(R.string.notification),
                                            Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    oldData = oldData.replace(((TextView) arg1).getText().toString() + "D", "");
                                    editor.putString(getString(R.string.notificationData), oldData);
                                    //editor.putString(getString(R.string.silence), "true,10:30,1-2-3,true=1,false,true|");
                                    editor.commit();

                                    ShowData();

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    listView.invalidateViews();

                }
            }
        });

        ShowData();
    }

    public void ShowData() {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.notification),
                Context.MODE_PRIVATE);
        oldData = sharedPref.getString(getString(R.string.notificationData), "");
        if(oldData.equals(""))
            oldData="אין הודעות";

        //oldData = preferences.getString(getString(R.string.notificationData), "");

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, oldData.split("D"));

        listView.setAdapter(itemsAdapter);
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
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}
