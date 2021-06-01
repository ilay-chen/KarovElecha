package com.flower.karovelecha;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import java.util.List;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

/**
 * Created by עילאי חן on 14 נובמבר 2016.
 */

public class DataClass extends MultiDexApplication {

    private CustomTfilotList Tfilaarr = null;
    private CustomShiurimList Shiurtarr = null;
    private CustomMikveList mikvearr = null;
    private CustomSynagogaList synagogaarr = null;
    static protected double latitude, longitude;
    static String MyLocation = "empty";
    static Boolean locationChanged = true;

        @Override
        public void onCreate() {
            super.onCreate();
            //CalligraphyConfig.initDefault("fonts/your-font.ttf");
//            CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                    .setDefaultFontPath("afek-regular-aaa.otf")
//                    .setFontAttrId(R.attr.fontPath)
//                    .build()
//            );
            ViewPump.init(ViewPump.builder()
                    .addInterceptor(new CalligraphyInterceptor(
                            new CalligraphyConfig.Builder()
                                    .setDefaultFontPath("afek-regular-aaa.otf")
                                    .setFontAttrId(R.attr.fontPath)
                                    .build()))
                    .build());
        }

    public DataClass() {

    }

    public DataClass(CustomTfilotList Tfilaarr, CustomShiurimList Shiurtarr,
                    CustomMikveList mikvearr, CustomSynagogaList synagogaarr)
    {
        this.Tfilaarr = Tfilaarr;
        this.Shiurtarr = Shiurtarr;
        this.mikvearr = mikvearr;
        this.synagogaarr = synagogaarr;
    }

    public void Initialize(CustomTfilotList Tfilaarr, CustomShiurimList Shiurtarr,
                           CustomMikveList mikvearr, CustomSynagogaList synagogaarr)
    {
        this.Tfilaarr = Tfilaarr;
        this.Shiurtarr = Shiurtarr;
        this.mikvearr = mikvearr;
        this.synagogaarr = synagogaarr;
    }

    public CustomTfilotList getTfilaarr() { return this.Tfilaarr; }

    public CustomShiurimList getShiurtarr() { return this.Shiurtarr; }

    public CustomMikveList getMikvearr() { return this.mikvearr; }

    public CustomSynagogaList getSynagogaarr() { return this.synagogaarr; }
}
