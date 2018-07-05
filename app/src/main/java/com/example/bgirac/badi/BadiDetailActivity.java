package com.example.bgirac.badi;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class BadiDetailActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private static String TAG = "BadiInfo";
    private LocationManager locationManager;
    LocListener ll = new LocListener();
    private String badiId;
    private String badiOrt;
    public ArrayList<String> badiData;
    private ProgressDialog mDialog;
    ArrayAdapter badiliste;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badi_detail);
        Intent intent = getIntent();
        badiId = intent.getStringExtra("badi");
        badiData = intent.getStringArrayListExtra("badiData");
        badiOrt = badiData.get(5);

        this.overridePendingTransition(R.animator.animation_enter,
                R.animator.animation_leave);

        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar_filters);

        myChildToolbar.setTitle(badiData.get(5) + "-" + badiData.get(8));

        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        initCards();

 //       TextView txt = (TextView) findViewById(R.id.badiinfos);

//        txt.setText(badiData.get(5) + "-" + badiData.get(8));

        mDialog = ProgressDialog.show(this, "Lade Badi-Infos", "bitte warten...(*￣з￣)");

        getBadiTemp("http://www.wiewarm.ch/api/v1/bad.json/" + badiId);
        WetterKlasse wk = new WetterKlasse((ListView) findViewById(R.id.wetter), this, badiOrt, (TextView) findViewById(R.id.wetterText), (ImageView) findViewById(R.id.wetterImage));
        wk.start(this);

        ListView badidetails = (ListView) findViewById(R.id.badidetails);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        //Manifest.permission
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());

        double v = 0;
        double v1 = 0;

        for (ArrayList<String> b : allBadis) {
            if (badiId.equals(b.get(0))) {
                v = Double.parseDouble(b.get(10));
                v1 = Double.parseDouble(b.get(11));
                break;
            }
        }

        // -- Position von der Badi -- //
        LatLng ort = new LatLng(v, v1);

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(ort).title("Ort"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ort));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ort)     // Sets the center of the map to location user
                .zoom(14)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // -- Position vom Smarphone -- //
        LatLng myPos = new LatLng(ll.getLat(), ll.getLon());
        mMap.addMarker(new MarkerOptions().position(myPos).title("MyPos"));
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



                    msq = IOUtils.toString(conn.getInputStream());


                    Log.i(TAG, Integer.toString(code));

                }catch(Exception e) {
                    Log.v(TAG, e.toString());
                }
                return msq;
            }
            @Override
            public void onPostExecute(String result) {
                try {
                    List<String> badiInfos = parseBadiTemp(result);

                    ListView badidetails = (ListView) findViewById(R.id.badidetails);
                    TextView badiText = (TextView) findViewById(R.id.badiText);
                    badiText.setText(badiInfos.get(0));
                    mDialog.dismiss();
                    temps.addAll(badiInfos);
                    temps.add("typ: " + badiData.get(9));
                    temps.add("Adresse: " + badiData.get(12));

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
    private void initCards() {
        CardView card1 = (CardView) findViewById(R.id.card_view1);
        CardView card2 = (CardView) findViewById(R.id.card_view2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator stateListAnimator = AnimatorInflater
                    .loadStateListAnimator(getApplicationContext(), R.animator.lift_on_touch);
            StateListAnimator stateListAnimator2 = AnimatorInflater
                    .loadStateListAnimator(getApplicationContext(), R.animator.lift_on_touch);
            card1.setStateListAnimator(stateListAnimator);
            card2.setStateListAnimator(stateListAnimator2);
        }
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FullscreenDialogFragment newFragment = new FullscreenDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ort", badiOrt);
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();

            }
        });
    }
}
