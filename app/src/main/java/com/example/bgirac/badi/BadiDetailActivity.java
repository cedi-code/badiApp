package com.example.bgirac.badi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BadiDetailActivity extends AppCompatActivity {
    private static String TAG = "BadiInfo";
    private String badiId;
    private String name;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badi_detail);
        Intent intent = getIntent();
        badiId = intent.getStringExtra("badi");
        name = intent.getStringExtra("name");

        TextView txt = (TextView) findViewById(R.id.badiinfos);

        txt.setText(name);

        mDialog = ProgressDialog.show(this, "Lade Badi-Infos", "bitte warten...(*￣з￣)");

        getBadiTemp("http://www.wiewarm.ch/api/v1/bad.json/" + badiId);
    }
    private void getBadiTemp(String url) {
        final ArrayAdapter temps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        new AsyncTask<String,String,String>() {
            @Override protected String doInBackground(String[] badi) { //In der variable msg soll die Antwort der Seite wiewarm
                String msq= "";
                try {
                    URL url = new URL(badi[0]);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // webseite antwort lesen
                    int code = conn.getResponseCode();

                    mDialog.dismiss();

                    msq = IOUtils.toString(conn.getInputStream());

                    Log.i(TAG, Integer.toString(code));

                }catch(Exception e) {
                    Log.v(TAG, e.toString());
                }
                return msq;
            }
            public void onPostExecute(String result) {
                try {
                    List<String> badiInfos = parseBadiTemp(result);

                    ListView badidetails = (ListView) findViewById(R.id.badidetails);


                    temps.addAll(badiInfos);

                    badidetails.setAdapter(temps);

                }catch (JSONException e) {
                    Log.v(TAG, e.toString());
                }
            }
            private List parseBadiTemp(String jonString)throws JSONException {
                ArrayList<String> resultList = new ArrayList<String>();
                JSONObject jsonObj = jsonObj = new JSONObject(jonString);
                JSONObject becken = jsonObj.getJSONObject("becken");

                Iterator keys = becken.keys();

                while(keys.hasNext()) {
                    String key = (String) keys.next();

                    JSONObject subObj = becken.getJSONObject(key); //Wenn man die Antwort der Webseite anschaut, steckt

                    String name = subObj.getString("beckenname");

                    String temp = subObj.getString("temp");

                    resultList.add(name + ":" + temp + " Grad Celsius");
                }
                return resultList;
            }
        }.execute(url);
    }
}
