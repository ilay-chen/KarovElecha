package com.flower.karovelecha;

/**
 * Created by ilay on 15/07/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] text;
    private final String[] high;
    private final String[] id;
    private CustomSynagogaList SynagagaData;
    private CustomMikveList  MikveData;
    private int color;
    private String ColorSet1, ColorSet2;

    public CustomList(Activity context,
                      String[] text, String []high, String []id) {
        super(context, R.layout.listtype, text);
        this.context = context;
        this.text = text;
        this.high = high;
        this.color = 1;
        this.id = id;
    }

    public CustomList(Activity context,
                      String[] text, String []high, int color, String []id) {
        super(context, R.layout.listtype, text);
        this.context = context;
        this.text = text;
        this.high = high;
        this.color = color;
        this.id = id;
    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View rowView= inflater.inflate(R.layout.listtype, null, true);

        switch (color)
        {
            case 1: ColorSet1 = "#c8eee8"; ColorSet2 = "#def5f1";
                break;
            case 2:ColorSet1 = "#f4dcda"; ColorSet2 = "#f9eae9";
                break;
            case 3: ColorSet1 = "#e4f4ff"; ColorSet2 = "#dcf1ff";
                break;
            case 4: ColorSet1 = "#00FFFFFF"; ColorSet2 = "#00FFFFFF";
                break;
            default: ColorSet1 = "#c8eee8"; ColorSet2 = "#def5f1";
                break;
        }

        if(position%2 ==0)
        rowView.setBackgroundColor(Color.parseColor(ColorSet1));
        else rowView.setBackgroundColor(Color.parseColor(ColorSet2));

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        TextView highTitle = (TextView) rowView.findViewById(R.id.highlight);
        Button directions = (Button) rowView.findViewById(R.id.diraction);
        txtTitle.setText(text[position].replace(high[position],""));

        if(text[position].equals("אין מידע, שנה חיפוש"))
            directions.setVisibility(View.GONE);

//        if(!high[position].equals(text[position]))
//            if(high[position].length()>15) {
//                highTitle.setTextSize(20);
//                highTitle.setText(high[position].split("-")[0]+"\n-"+high[position].split("-")[1]);
//            }
        if(high[position].contains(":"))
                highTitle.setText(high[position]);
        else if(!high[position].equals("")){
                highTitle.setText("");
                txtTitle.setText(high[position] + ", " + txtTitle.getText().toString());
        }
            else
        {
            highTitle.setText("");
            txtTitle.setText(txtTitle.getText().toString());
        }

        DataClass appState = (DataClass) context.getApplication();
        SynagagaData = appState.getSynagogaarr();
        MikveData = appState.getMikvearr();

        directions.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "הנחיות נסיעה",
                        Toast.LENGTH_SHORT).show();

                SynagogaObject thisSynagoga = SynagagaData.getSynagogaById(id[position]);

                if(thisSynagoga != null)
                {
                    String uri = "";
                    if (SynagagaData != null && SynagagaData.getEvents().size() != 0)
                            if (Double.parseDouble(thisSynagoga.getLatitude()) != 0.0
                                    && Double.parseDouble(thisSynagoga.getLongitude()) != 0.0) {
                                uri = "geo:0,0?q=" + thisSynagoga.getLatitude() +
                                        ", " + thisSynagoga.getLongitude() + "&navigate=yes";
                            } else
                                uri = "geo:0,0?q=" + thisSynagoga.getAddress() + ", "
                                        + thisSynagoga.getLocation() + "&navigate=yes";

                    //String uri = "geo://?q=" + address.substring(address.lastIndexOf("מיקום: ")+5, address.indexOf("בימים:")-2).replace(", ", "%20")+"&navigate=yes";
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(uri)));
                }
                else
                {
                    MikveObject thisMikve = MikveData.getMikveById(id[position]);
                    String uri = "";
                    if (thisMikve != null) {
                        if (Double.parseDouble(thisMikve.getLatitude()) != 0.0
                                && Double.parseDouble(thisMikve.getLongitude()) != 0.0) {
                            uri = "geo:0,0?q=" + thisMikve.getLatitude() +
                                    ", " + thisMikve.getLongitude() + "&navigate=yes";
                        } else
                            uri = "geo:0,0?q=" + thisMikve.getAddress() + ", "
                                    + thisMikve.getLocation() + "&navigate=yes";
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(uri)));
                        }
                }
            }
        });

        return rowView;
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}