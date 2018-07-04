package com.example.bgirac.badi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class WetterKlasse {
    private  static String TAG="Wetterprognose";
    private String ort;
    private String badiId;
    private String name;
    private ProgressDialog mDialog;
    private String wetterArt;
    private Context ct;
    private ListView wetterprognose;


    public WetterKlasse(ListView lv, Context ct, String ort) {
        this.ct = ct;
        this.ort = ort;
        wetterprognose = lv;
    }

    public void start() {
        mDialog= ProgressDialog.show(ct, "Lade Wetterprognose","Bitte warten...");
        getWetter("https://openweathermap.org/data/2.5/weather?q=Bern,CH&appid=b6907d289e10d714a6e88b30761fae22");
    }

    private void getWetter(String url){
        final ArrayAdapter temps= new ArrayAdapter<String>(ct, android.R.layout.simple_list_item_1);

        new AsyncTask<String,String,String>(){
            @Override protected String doInBackground(String[] wetter){
                String msq="";
                try{
                    URL url = new URL(wetter[0]);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    int code = conn.getResponseCode();

                    msq = IOUtils.toString(conn.getInputStream());
                    Log.i(TAG, Integer.toString(code));

                }catch (Exception e){
                    Log.v(TAG, e.toString());
                }

                return msq;
            }
            public void onPostExecute(String result){
                try {
                    mDialog.dismiss();
                    List<String> wetter = parseWetterprognose(result);
                    temps.addAll(wetter);

                    wetterprognose.setAdapter(temps);
                }catch (JSONException e){
                    Log.v(TAG, e.toString());
                }

            }
            private List parseWetterprognose(String jonString)throws JSONException{
                ArrayList<String> resultList = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject(jonString);
                JSONObject wetterTemp = jsonObject.getJSONObject("main");

                double temp = wetterTemp.getDouble("temp");
                resultList.add("Wetter: "+ (float)temp+" °C");

                // Wetter icon scheiss für Cedi
                JSONArray statusWetter = jsonObject.getJSONArray("weather");
                JSONObject wetterStausObject = (JSONObject) statusWetter.get(0);
                String status = wetterStausObject.getString("main");
                resultList.add("Status: "+ status);

                return resultList;
            }

        }.execute(url);
    }
}
