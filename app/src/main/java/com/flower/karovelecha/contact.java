package com.flower.karovelecha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class contact extends AppCompatActivity {

    EditText details[] = new EditText[4];
    Thread thread;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ImageButton menu= (ImageButton) findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        details[0] = (EditText)findViewById(R.id.name);
        details[1] = (EditText)findViewById(R.id.email);
        details[2] = (EditText)findViewById(R.id.phone);
        details[3] = (EditText)findViewById(R.id.data);

//        int i;
//        for(i = 0; i < details.length; i++) {
//            final int finalI = i;
//            details[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if(hasFocus){
//                        if(details[finalI].getText().toString().equals("שם מלא")||
//                                details[finalI].getText().toString().equals("אימייל")||
//                                details[finalI].getText().toString().equals("טלפון")||
//                                details[finalI].getText().toString().equals("הארות / הערות שחשוב שנדע"))
//                            details[finalI].setText("");
//                    }else {
//                    }
//                }
//            });
//        }

        Button send = (Button) this.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                for(EditText et : details)
                    if(et.getText()==null||et.getText().toString().equals("")) {
                        et.setText("שדה ריק");
                        Toast.makeText(contact.this,"שדה ריק! אנא מלא את כל השדות, תודה!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                pd=new ProgressDialog(contact.this);
                pd.setMessage("שולח בקשה..");
                pd.setCancelable(false);
                pd.show();
                thread.start();
            }
        });
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    try {
                        GMailSender sender = new GMailSender("karovelecha1@gmail.com", "herb2311");

                        sender.sendMail("פנייה מאפליקציה 'קרוב אליך'",
                                details[0].getText().toString() + " ," +
                                        details[1].getText().toString() + " ," +
                                        details[2].getText().toString() + " ," +
                                        details[3].getText().toString() + " ,",
                                "karovelecha1@gmail.com",
                                "mdatit1@gmail.com");
                        Log.e("SendMail", "try sending");
                        finishSending();
                    } catch (Exception e) {
                        Log.e("SendMail", e.getMessage(), e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void finishSending(){
        pd.cancel();
        Intent i = new Intent(getBaseContext(), feedBack.class);
        startActivity(i);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}
