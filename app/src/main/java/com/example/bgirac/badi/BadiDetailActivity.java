package com.example.bgirac.badi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BadiDetailActivity extends FragmentActivity implements OnMapReadyCallback {
    private static String TAG = "BadiInfo";
    private String badiId;
    private String name;
    private ProgressDialog mDialog;
    ArrayAdapter badiliste;

    private GoogleMap mMap;
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());

        double v = 0;
        double v1 = 0;

        for(ArrayList<String> b: allBadis) {
            if(badiId.equals(b.get(0))) {
                v = Double.parseDouble(b.get(10));
                v1 = Double.parseDouble(b.get(11));
                break;
            }
        }

        LatLng ort = new LatLng(v, v1);

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(ort).title("Ort"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ort));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ort)     // Sets the center of the map to location user
                .zoom(14)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
