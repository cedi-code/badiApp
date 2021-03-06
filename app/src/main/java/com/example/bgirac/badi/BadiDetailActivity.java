package com.example.bgirac.badi;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

import java.net.ConnectException;
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

        /**
         * Den Up button 'enablen'
         */
        ab.setDisplayHomeAsUpEnabled(true);
        initCards();

        /**
         * Hier werden die wetterdaten / badidaten angezeigt
         */
        mDialog = ProgressDialog.show(this, "Lade Badi-Infos", "bitte warten...(*￣з￣)");
        getBadiTemp("http://www.wiewarm.ch/api/v1/bad.json/" + badiId);
        WetterKlasse wk = new WetterKlasse(this, badiOrt, (TextView) findViewById(R.id.wetterText), (ImageView) findViewById(R.id.wetterImage));
        wk.start(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }


    /**
     * Hier wird der ort der Badi auf einer Google map angezeigt
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());

        double v = 0;
        double v1 = 0;

        /**
         * es wird die badi nach ID gesucht und im CSV file die koordinaten geholt
         */
        for (ArrayList<String> b : allBadis) {
            if (badiId.equals(b.get(0))) {
                v = Double.parseDouble(b.get(10)); // latitude - breitengrad
                v1 = Double.parseDouble(b.get(11)); // longitude - längengrad
                break;
            }
        }

        /**
         * Position der Badi
         */
        LatLng ort = new LatLng(v, v1);

        /**
         * Hier wird ein PinStecker hinzugefügt welcher direkt auf dem ort der Badi ist
         */
        mMap.addMarker(new MarkerOptions().position(ort).title("Ort"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ort));

        /**
         * hier wird die Camera perspektive auf die Badi gerichtet und hineingezoomed
         */
        CameraPosition cameraPosition = new CameraPosition.Builder().target(ort).zoom(14).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        /**
         * live position vom Benutzer (mit GPS daten des Smartphones)
         */
        LatLng myPos = new LatLng(ll.getLat(), ll.getLon());
        mMap.addMarker(new MarkerOptions().position(myPos).title("MyPos"));
    }

    /**
     * Vergrössert die Map auf dem Bildschirm
     *
     * @param v immageButton
     */
    boolean landscape = false;
    public void expandMap(View v){
        final CardView card = (CardView) findViewById(R.id.badi_maps);
        final ImageButton btn = (ImageButton)findViewById(R.id.button_up_map);
        ViewGroup.LayoutParams params = card.getLayoutParams();
        if(params.height != ViewGroup.LayoutParams.MATCH_PARENT) {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            landscape = false;

        }else if(params.width != ViewGroup.LayoutParams.MATCH_PARENT) {

            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            landscape = true;

        }else {
            if(landscape) {
                params.width = 0;
            }else {
                params.height = 0;
            }
        }

        PropertyValuesHolder pvhLeft = PropertyValuesHolder.ofInt("left", 0, 1);
        PropertyValuesHolder pvhTop = PropertyValuesHolder.ofInt("top", 0, 1);
        PropertyValuesHolder pvhRight = PropertyValuesHolder.ofInt("right", 0, 1);
        PropertyValuesHolder pvhBottom = PropertyValuesHolder.ofInt("bottom", 0, 1);


        final Animator collapseExpandAnim = ObjectAnimator.ofPropertyValuesHolder(card, pvhLeft, pvhTop,
                pvhRight, pvhBottom);
        collapseExpandAnim.setupStartValues();

        card.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                card.getViewTreeObserver().removeOnPreDrawListener(this);
                collapseExpandAnim.setupEndValues();
                collapseExpandAnim.start();
                return false;
            }
        });
        card.requestLayout();
    }

    /**
     * Hier werden alle Badi daten geholt und angezeigt
     * (geleiche funktionalität wie bei der WetterKlasse)
     *
     * @param url   die url braucht es um bei der webseite die Json datei zu holen mit den wetterdaten
     */
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
                    mDialog.dismiss();


                }
                return msq;
            }
            @Override
            public void onPostExecute(String result) {
                try {
                    List<String> badiInfos = parseBadiTemp(result);
                    TextView badiText = (TextView) findViewById(R.id.badiText);
                    badiText.setText(badiInfos.get(0));
                    mDialog.dismiss();

                }catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    errorConn();
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
    BadiDetailActivity mActivity = this;

    /**
     * Zeigt dem User ein popup an, das er keine Verbindung hat
     */
    private void errorConn() {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Es konnte keine Verbindung hergestellt werden");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "erneut versuchen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mActivity.finish();
                                // getIntent().setFlags(getIntent().FLAG_ACTIVITY_NEW_TASK | getIntent().FLAG_ACTIVITY_CLEAR_TASK);
                                mActivity.startActivity(getIntent());


                            }
                        });
                alertDialog.show();
            }
        });
    }

    /**
     * Hier befinden sich die onclick events der cardviews
     */
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
        /**
         * beim Onclick werden genauere wetterdaten mit einem Popup angezeigt (im FullscreenDialogFragment)
         */
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
