package com.flower.karovelecha;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.flower.karovelecha.wheelViewLabary.OnWheelChangedListener;
import com.flower.karovelecha.wheelViewLabary.OnWheelScrollListener;
import com.flower.karovelecha.wheelViewLabary.WheelView;
import com.flower.karovelecha.wheelViewLabary.adapters.ArrayWheelAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ishuvSearch extends AppCompatActivity {

    private ListView moredata;
    private CustomTfilotList TfilaData, TfilaShearchMore;
    private CustomShiurimList ShiurData, ShiurShearchMore;
    private CustomMikveList MikveData, MikveShearchMore;
    private CustomSynagogaList SynagagaData;
    private String searchDate;
    private List settlements = new ArrayList();
    private List searchlist, subjects, kinds, shiurkinds;
    int height, width, lastSearchKind = 0;
    private DatabaseReference mDatabase;
    private ProgressDialog pd;
    private Boolean allData1 = false, allData2 = false;
    private ImageButton add, searchOne, searchDateButton;
    private boolean scrolling = false;
    private WheelView wheelspinner [] = new WheelView[4];
    private ImageView pass;
    private Context context;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ishuv_search);

        initialize(this);

        pd=new ProgressDialog(this);
        pd.setMessage("מחפש מידע..");
        pd.setCancelable(false);
        pd.show();

        DataClass appState = (DataClass) getApplicationContext();
        TfilaData = appState.getTfilaarr();
        ShiurData = appState.getShiurtarr();
        SynagagaData = appState.getSynagogaarr();
        MikveData = appState.getMikvearr();

        if(TfilaData == null || ShiurData == null || SynagagaData == null)
            mDatabase.child("allData").child("synagoga").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<SynagogaObject> synagogaarr = null;
                            // Get user value
                            //List<Event> ev = dataSnapshot.getValue(Event.class);
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //<Event> ev = dataSnapshot.getValue();
                                GenericTypeIndicator<List<SynagogaObject>> t = new GenericTypeIndicator<List<SynagogaObject>>() {
                                };
                                synagogaarr = dataSnapshot.getValue(t);
                            }
                            SynagagaData = new CustomSynagogaList(synagogaarr);
                            TfilaData = SynagagaData.getAllTfilot(getApplicationContext());
                            ShiurData = SynagagaData.getAllShiurim(getApplicationContext());

                            if(MikveData != null)
                                hasData(true);
                            else {
                                mDatabase.child("allData").child("mikve").addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                List<MikveObject> mikvearr = null;
                                                // Get user value
                                                //List<Event> ev = dataSnapshot.getValue(Event.class);
                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    //<Event> ev = dataSnapshot.getValue();
                                                    GenericTypeIndicator<List<MikveObject>> t = new GenericTypeIndicator<List<MikveObject>>() {
                                                    };
                                                    mikvearr = dataSnapshot.getValue(t);
                                                }
                                                MikveData = new CustomMikveList(mikvearr, context);

                                                if (mikvearr == null)
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
//                            if (synagogaarr == null)
//                                hasData(false);
//                            else
//                                hasData(true);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            // ...
                        }
                    });

        else if(MikveData != null)
            hasData(true);
        else {
            mDatabase.child("allData").child("mikve").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<MikveObject> mikvearr = null;
                            // Get user value
                            //List<Event> ev = dataSnapshot.getValue(Event.class);
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //<Event> ev = dataSnapshot.getValue();
                                GenericTypeIndicator<List<MikveObject>> t = new GenericTypeIndicator<List<MikveObject>>() {
                                };
                                mikvearr = dataSnapshot.getValue(t);
                            }
                            List settlements = ishuvSearch.this.settlements;
                            MikveData = new CustomMikveList(mikvearr, context);

                            if (mikvearr == null)
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

    public void hasData(Boolean data) {
        if(data && !allData1 && !allData2)
            allData1 = true;
        else if(data && allData1 && !allData2)
            allData2 = true;
        else if(data && allData1 && allData2) {
            //searchTfila();
        }
            if(data)
            {
            //if(TfilaData!=null)searchMore(TfilaData);
            pd.dismiss();
        }
    }

    public void initialize(Context c)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        add= (ImageButton) findViewById(R.id.menu);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
            }
        });

        searchOne = (ImageButton) findViewById(R.id.searchOne);
        searchOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateWheels();
            }
        });


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        height = metrics.heightPixels;
        width = metrics.widthPixels;

        context = c;

        settlements = Arrays.asList(getResources().getStringArray(R.array.settlements));

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
                mDatePicker = new DatePickerDialog(ishuvSearch.this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        searchDate = dateFormatter.format(newDate.getTime());
                        switch (wheelspinner[1].getCurrentItem()) {
                            case 0: if(TfilaData!=null)searchMore(TfilaData); break;
                            case 1: if(ShiurData!=null)searchMore(ShiurData); break;
                            case 2: if(MikveData!=null)searchMore(MikveData); break;
                        }
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                mDatePicker.setTitle("בחר תאריכים");
                mDatePicker.show();
            }
        });

        List settlements = Arrays.asList(getResources().getStringArray(R.array.settlements));

        searchlist = new ArrayList();
        searchlist.add("תפילה");
        searchlist.add("שיעור");
        searchlist.add("מקווה");
        //search.add("תפילה");

        subjects = new ArrayList();
        subjects.add("שחרית");
        subjects.add("מנחה");
        subjects.add("ערבית");

        kinds = new ArrayList();
        kinds.add("הכל");
        kinds.add("ספרד");
        kinds.add("אשכנז");
        kinds.add("עדות המזרח");

        shiurkinds = new ArrayList();
        //kinds.add("הכל");
        shiurkinds.add("הכל");
        shiurkinds.add("חסידות");
        shiurkinds.add("דף יומי");
        shiurkinds.add("הלכה");
        shiurkinds.add("עיון");
        shiurkinds.add("מדרש ואגדה");
        shiurkinds.add("לנשים בלבד");

        wheelspinner[0] = (WheelView) findViewById(R.id.settlements);
        wheelspinner[1] = (WheelView) findViewById(R.id.searchkind);
        wheelspinner[2] = (WheelView) findViewById(R.id.subjectsearch);
        wheelspinner[3] = (WheelView) findViewById(R.id.kind);

        wheelspinner[0].setVisibleItems(5);
        wheelspinner[1].setVisibleItems(5);
        wheelspinner[2].setVisibleItems(5);
        wheelspinner[3].setVisibleItems(5);

        pass = (ImageView)findViewById(R.id.imageView3);

        WheelView.color = 2;

        ArrayWheelAdapter<String> adapter =
                new ArrayWheelAdapter<String>(this, returnarray(settlements));
        adapter.setTextSize(18);
        wheelspinner[0].setViewAdapter(adapter);

        ArrayWheelAdapter<String> adapter2 =
                new ArrayWheelAdapter<String>(this, returnarray(searchlist));
        adapter2.setTextSize(18);
        wheelspinner[1].setViewAdapter(adapter2);

        ArrayWheelAdapter<String> adapter3 =
                new ArrayWheelAdapter<String>(this, returnarray(subjects));
        adapter3.setTextSize(18);
        wheelspinner[2].setViewAdapter(adapter3);

        ArrayWheelAdapter<String> adapter4 =
                new ArrayWheelAdapter<String>(this, returnarray(kinds));
        adapter4.setTextSize(18);
        wheelspinner[3].setViewAdapter(adapter4);

        //for(WheelView WV: wheelspinner)
        for(int i = 2; i < 4; i++)
        {
            wheelspinner[i].addChangingListener(new OnWheelChangedListener() {
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if (!scrolling) {
                        //updateCities(city, cities, newValue);
                        UpdateWheels();
                    }
                }
            });

            wheelspinner[i].addScrollingListener( new OnWheelScrollListener() {
                public void onScrollingStarted(WheelView wheel) {
                    scrolling = true;
                }
                public void onScrollingFinished(WheelView wheel) {
                    scrolling = false;
                    //updateCities(city, cities, country.getCurrentItem());
                    UpdateWheels();
                }
            });
        }
/*
        RelativeLayout.LayoutParams lp;
        search[0] = (Spinner)findViewById(R.id.settlements);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, settlements);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        search[0].setAdapter(dataAdapter);
        lp = (RelativeLayout.LayoutParams) search[0].getLayoutParams();
        lp.width = width/2;
        search[0].setLayoutParams(lp);

        search[1] = (Spinner)findViewById(R.id.searchkind);
        dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, searchlist);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        search[1].setAdapter(dataAdapter);
        lp = (RelativeLayout.LayoutParams) search[1].getLayoutParams();
        lp.width = width/2;
        search[1].setLayoutParams(lp);

        search[2] = (Spinner)findViewById(R.id.subjectsearch);
        search[3] = (Spinner)findViewById(R.id.kind);
        dataAdapter = new ArrayAdapter<String>(ishuvSearch.this,
                android.R.layout.simple_spinner_item, kinds);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        search[3].setAdapter(dataAdapter);
        lp = (RelativeLayout.LayoutParams) search[3].getLayoutParams();
        lp.width = width/2;
        search[3].setLayoutParams(lp);

        for(int i = 0; i < 2; i++)
            search[i].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    RelativeLayout.LayoutParams lp;
                    ArrayAdapter<String> dataAdapter;
                    switch (search[1].getSelectedItemPosition()) {
                        case 0:
                            dataAdapter = new ArrayAdapter<String>(ishuvSearch.this,
                                    android.R.layout.simple_spinner_item, subjects);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            search[2].setAdapter(dataAdapter);
                            lp = (RelativeLayout.LayoutParams) search[2].getLayoutParams();
                            lp.width = width/2;
                            search[2].setLayoutParams(lp);
                            search[2].setVisibility(View.VISIBLE);
                            search[3].setVisibility(View.VISIBLE);
                            searchTfila();
                            break;
                        case 1:
                            dataAdapter = new ArrayAdapter<String>(ishuvSearch.this,
                                    android.R.layout.simple_spinner_item, shiurkinds);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            search[2].setAdapter(dataAdapter);
                            lp = (RelativeLayout.LayoutParams) search[2].getLayoutParams();
                            lp.width = width/2;
                            search[2].setLayoutParams(lp);
                            search[2].setVisibility(View.VISIBLE);
                            search[3].setVisibility(View.GONE);
                            searchShiur();
                            break;
                        case 2:
                            search[2].setVisibility(View.GONE);
                            search[3].setVisibility(View.GONE);
                            searchMikve();
                            break;
                    }
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
                switch (wheelspinner[1].getCurrentItem())
                {
                    case 0:
                        if (!address.equals("אין מידע")) {
                            Intent i = new Intent(getBaseContext(), SynagogaPage.class);
                            i.putExtra("Id", TfilaShearchMore.getEvent(position).getLocation() +"+"+
                                    TfilaShearchMore.getEvent(position).getSynagogaName());
                            startActivity(i);
                            //ishuvSearch.this.finish();
                            break;
                        }
                    case 1:
                        if (!address.equals("אין מידע")) {
                            Intent i = new Intent(getBaseContext(), SynagogaPage.class);
                            i.putExtra("Id", ShiurShearchMore.getEvent(position).getLocation() +"+"+
                                    ShiurShearchMore.getEvent(position).getSynagogaName());
                            startActivity(i);
                            //ishuvSearch.this.finish();
                            break;
                        }
                    case 2:
                        if (!address.equals("אין מידע")) {
                            Intent i = new Intent(getBaseContext(), mikvePage.class);
                            i.putExtra("id", MikveShearchMore.getEvent(position).getSubject() + "+" +
                                    MikveShearchMore.getEvent(position).getAddress());
                            startActivity(i);
                            //ishuvSearch.this.finish();
                            break;
                        }
                }
            }
        });
    }

    public void UpdateWheels() {
        ArrayWheelAdapter<String> adapter;
        if(lastSearchKind != wheelspinner[1].getCurrentItem())
        {
            wheelspinner[2].setCurrentItem(0);
            wheelspinner[3].setCurrentItem(0);
        }

        switch (wheelspinner[1].getCurrentItem()) {
            case 0:
                lastSearchKind = 0;
                adapter =
                        new ArrayWheelAdapter<String>(ishuvSearch.this, returnarray(subjects));
                adapter.setTextSize(18);
                wheelspinner[2].setVisibleItems(3);
                wheelspinner[2].setViewAdapter(adapter);
                wheelspinner[2].setVisibility(View.VISIBLE);
                wheelspinner[3].setVisibility(View.VISIBLE);
                pass.setVisibility(View.VISIBLE);
                if (TfilaData != null) searchMore(TfilaData);
                //searchTfila();
                break;
            case 1:
                lastSearchKind = 1;
                adapter =
                        new ArrayWheelAdapter<String>(ishuvSearch.this, returnarray(shiurkinds));
                adapter.setTextSize(18);
                wheelspinner[2].setVisibleItems(3);
                wheelspinner[2].setViewAdapter(adapter);
                wheelspinner[2].setVisibility(View.VISIBLE);
                wheelspinner[3].setVisibility(View.GONE);
                if(ShiurData!=null)searchMore(ShiurData);
                //searchShiur();
                break;
            case 2:
                lastSearchKind = 2;
                wheelspinner[2].setVisibility(View.GONE);
                wheelspinner[3].setVisibility(View.GONE);
                pass.setVisibility(View.GONE);
                searchMikve();
                break;
        }

        //searchTfila();
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

    public void searchMikve() {
        if(MikveData!=null)searchMore(MikveData);
    }

    public void searchMore(CustomTfilotList tfilaData) {
        //Data = new CustomTfilotList(Eventarr, settlements);
        CustomList adapter;
        if(tfilaData.getEvents() == null) {
            String []empty = {"אין מידע"}, none = {""};
            adapter = new CustomList(this, empty, none, null);
            moredata.setAdapter(adapter);
            return;
        }
        refreshLists();
        tfilaData = new CustomTfilotList(tfilaData.allToday(kinds.get(wheelspinner[3].getCurrentItem()).toString(),
                subjects.get(wheelspinner[2].getCurrentItem()).toString(),
                settlements.get(wheelspinner[0].getCurrentItem()).toString(), searchDate), this);
        TfilaShearchMore = tfilaData;

        adapter = new CustomList(ishuvSearch.this, tfilaData.tostring(), tfilaData.Oclock(),
                1, tfilaData.getAllIds());
        moredata.setAdapter(adapter);
    }

    public void searchMore(CustomMikveList mikveData) {
        //Data = new CustomTfilotList(Eventarr, settlements);
        CustomList adapter;
        if(mikveData.getEvents() == null) {
            String []empty = {"אין מידע"}, none = {""};
            adapter = new CustomList(this, empty, none, null);
            moredata.setAdapter(adapter);
            return;
        }
        mikveData = new CustomMikveList(mikveData.allToday(settlements.get(wheelspinner[0].getCurrentItem()).toString()), context);
        MikveShearchMore = mikveData;

        adapter = new CustomList(ishuvSearch.this, mikveData.tostring(), mikveData.Oclock(), 3, mikveData.getAllIds());
        String []test = mikveData.Oclock(searchDate);
        moredata.setAdapter(adapter);
    }

    public void searchMore(CustomShiurimList shiurData) {
        //Data = new CustomTfilotList(Eventarr, settlements);
        CustomList adapter;
        if(shiurData.getEvents() == null) {
            String []empty = {"אין מידע"}, none = {""};
            adapter = new CustomList(this, empty, none, null);
            moredata.setAdapter(adapter);
            return;
        }
        refreshLists();

        shiurData = new CustomShiurimList(shiurData.allToday(shiurkinds.get(wheelspinner[2].getCurrentItem()).toString(),
                settlements.get(wheelspinner[0].getCurrentItem()).toString(), searchDate), this);
        ShiurShearchMore = shiurData;

        adapter = new CustomList(ishuvSearch.this, shiurData.tostring(), shiurData.Oclock(),
                2, shiurData.getAllIds());
        moredata.setAdapter(adapter);
    }

    public void refreshLists()
    {
        settlements = Arrays.asList(getResources().getStringArray(R.array.settlements));
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}
