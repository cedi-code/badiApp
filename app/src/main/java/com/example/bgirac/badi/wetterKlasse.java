package com.example.bgirac.badi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    private TextView wetterText;
    private ImageView wetterBild;



    public WetterKlasse(ListView lv, Context ct, String ort, TextView txt, ImageView img) {
        this.ct = ct;
        this.ort = ort;
        wetterprognose = lv;
        wetterText = txt;
        wetterBild = img;
    }
    public WetterKlasse() {

    }

    public void start(Context c) {
        mDialog= ProgressDialog.show(ct, "Lade Wetterprognose","Bitte warten...");
        getWetter("https://openweathermap.org/data/2.5/weather?q="+ort+",CH&appid=b6907d289e10d714a6e88b30761fae22", null, c);
    }

    public void getWetter(String url,final ListView ls, Context c ){
        final ArrayAdapter temps= new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1);

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

                    List<String> wetter = parseWetterprognose(result);
                    temps.addAll(wetter);
                    if(ls != null) {
                        ls.setAdapter(temps);

                    }else {
                        mDialog.dismiss();

                        wetterText.setText(wetter.get(0));

                        int id = Integer.parseInt(wetter.get(3));
                        if (id <= 232 & id >= 200) {
                            //Tonner Wätter
                            wetterBild.setImageResource(R.mipmap.thunderstrorm);
                        }else if (id <= 321 & id >= 300) {
                            //liächtä Rägä
                            wetterBild.setImageResource(R.mipmap.dizzle);
                        }else if (id <= 531 & id >= 500) {
                            //Rägä
                            wetterBild.setImageResource(R.mipmap.rain);
                        }else if (id <= 622 & id >= 600) {
                            //Schnee
                            wetterBild.setImageResource(R.mipmap.snow);
                        }else if (id <= 781 & id >= 701) {
                            //irgendwie Atmosphäre -- ke ahnig wieso
                            wetterBild.setImageResource(R.mipmap.mist);
                        }else if (id == 800) {
                            //Sunnä schiin :)
                            wetterBild.setImageResource(R.mipmap.clear_sky);
                        }else if (id <= 804 & id >= 801) {
                            //Wouchä am Himu
                            wetterBild.setImageResource(R.mipmap.brocken_clouds);
                        }

                    }



                }catch (JSONException e){
                    Log.v(TAG, e.toString());
                }

            }
            private List parseWetterprognose(String jonString)throws JSONException{
                ArrayList<String> resultList = new ArrayList<String>();
                JSONObject jsonObject = new JSONObject(jonString);
                JSONObject wetterTemp = jsonObject.getJSONObject("main");
                String hum = "Humidity: " + wetterTemp.getInt("humidity");
                double temp = wetterTemp.getDouble("temp");
                resultList.add("Weather: "+ (float)temp+" °C");
                resultList.add(hum);

                // Wetter icon scheiss für Cedi
                JSONArray statusWetter = jsonObject.getJSONArray("weather");
                JSONObject wetterStausObject = (JSONObject) statusWetter.get(0);
                String status = wetterStausObject.getString("main");
                String statusID = wetterStausObject.getString("id");
                String description = "Description: " + wetterStausObject.getString("description");
                resultList.add("Status: "+ status);
                resultList.add(statusID);
                resultList.add(description);

                //Wind
                JSONObject wetterWind = jsonObject.getJSONObject("wind");
                String speed = "Wind speed: " + wetterWind.getInt("speed");
                resultList.add(speed);

                //Clouds
                JSONObject wetterClouds = jsonObject.getJSONObject("clouds");
                String clouds = "Clouds: " + wetterClouds.getInt("all");
                resultList.add(clouds);

                return resultList;
            }

        }.execute(url);
    }
}
