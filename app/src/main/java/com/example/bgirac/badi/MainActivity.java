package com.example.bgirac.badi;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback {

    ArrayAdapter badiliste;
    private final static String AARBERG = "Schwimmbad Aarberg (BE)";
    private final static String ADELBODEN = "Schwimmbad Gruebi Adelboden (BE)";
    private final static String BERN = "Stadberner Baeder Bern (BE)";
    private static final int MY_REQUEST_INT = 123;
    private String activeFilters = "";
    private ListView badis;
    private String filter = "";


    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // holt sich die Toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();

        // überpüft ob man schon einmal nach einem Kanton gefiltert hat
        if (intent.hasExtra("kanton")) {
            activeFilters = intent.getStringExtra("kanton");
        }


        // initzialisiert componentete
        init();


    }


    /**
     * initzialisiert die Badis
     */
    private void init() {

        // fügt alle badis hinzu
        addBadisToList();



        // fügt der List view Onclick und leitet sie zu BadiDetail weiter
        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailActivity.class);
                String selected = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();
                String badii = "";
                final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
                for (ArrayList<String> b : allBadis) {
                    badii = b.get(5) + "-" + b.get(8);
                    if (badii.equals(selected)) {
                        // übergibt im Extra noch die benötigten badidaten
                        intent.putExtra("badi", b.get(0));
                        intent.putStringArrayListExtra("badiData", b);
                    }


                }
                // die BadiDetail Activity wird gestartet
                startActivity(intent);

            }
        };
        badis.setOnItemClickListener(mListClickedHandler);


    }


    /**
     * Badis werden zur ListView hinzugefügt, dabei wird auch überprüft ob der Benutzer Filter oder nicht
     */
    public void addBadisToList() {

        // holt sich die Badis von der csv datei
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());

        badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);


        // intent von Filtern kantone
        Intent intent = getIntent();


        for (ArrayList<String> b : allBadis) {

            if (intent.hasExtra("kanton")) {
                // für jeden aktiven filter (funktioniert noch nicht richtig)

                    // suche, aber mit dem Aktiven filter der Kantone
                    if (b.get(6).equals(activeFilters)) {
                        if ((b.get(5).toLowerCase().contains(filter.toLowerCase()) ||
                                b.get(8).toLowerCase().contains(filter.toLowerCase()))) {
                            badiliste.add(b.get(5) + "-" + b.get(8));
                        }
                    }


            }


            if ((b.get(5).toLowerCase().contains(filter.toLowerCase()) ||
                    b.get(8).toLowerCase().contains(filter.toLowerCase()))) {

                // suche ohne Filter
                badiliste.add(b.get(5) + "-" + b.get(8));
            }



        }

        badis.setAdapter(badiliste);
    }


    // inizialisiert die Google Map da die Main Activity
    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;


 /*       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_REQUEST_INT);
            }


            return;
        }else {
            mMap.setMyLocationEnabled(true);
        } */








    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        /*SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName())); */

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter = s;
                badiliste.clear();
                addBadisToList();

                return false;
            }
        });
        /**
         * Wenn sich die Suchbar geändert wird (offen oder zu)
         *
         */
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                // die google Map verstecken
                hideMap();
                return true;

            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                filter = "";
                badiliste.clear();
                addBadisToList();

                // die Google map wieder ersichtlich machen
                final ImageButton btn = (ImageButton)findViewById(R.id.image_up_main);
                expandMapFragment(btn);

                return true;
            }
        });

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.filter:
                showFilters();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showFilters() {

        Intent intentFilter = new Intent(getApplicationContext(), filters.class);
        intentFilter.setClass(MainActivity.this, filters.class);

        // intentFilter.setFlags(intentFilter.FLAG_ACTIVITY_NEW_TASK | intentFilter.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentFilter);
        // finish();

    }

    private void hideMap() {
        final CardView card = (CardView) findViewById(R.id.main_map);
        ViewGroup.LayoutParams paramsListe = badis.getLayoutParams();
        ViewGroup.LayoutParams params = card.getLayoutParams();
        params.height = -1;

        card.setVisibility(View.GONE);

    }
    /**
     * Vergrössert die Map
     *
     * @param v immageButton
     */
    public void expandMapFragment(View v){
        final CardView card = (CardView) findViewById(R.id.main_map);
        final ImageButton btn = (ImageButton)findViewById(R.id.image_up_main);
        card.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = card.getLayoutParams();
        if(params.height != 90 && params.height != 0 ) {
            if(onTop) {
                params.height = 90;
            }else {
                params.height = 0;
            }



        }else {
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;

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
     * Schaut ob die Listview beim Obersten element ist,
     *
     * falls ja, wird die Map etwas grösser -> onTop =true
     *
     * falls nein, wird sie kleiner -> onTop = false
     *
     * onTop wird dan bei der Methode expandMapFragment verglichen
     */
    boolean onTop = true;
    /*AbsListView.OnScrollListener scroller = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if(onTop) {

            }

        }


        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem == 0) {
                // check if we reached the top or bottom of the list
                View v = badis.getChildAt(0);
                onTop = true;
                final ImageButton btn = (ImageButton)findViewById(R.id.image_up_main);
                expandMapFragment(btn);
                int offset = (v == null) ? 0 : v.getTop();
                if (offset == 0) {

                    return;
                }
            } else {
                if(onTop) {
                    onTop = false;

                    final ImageButton btn = (ImageButton)findViewById(R.id.image_up_main);
                    expandMapFragment(btn);
                }

            }
        }
    }; */




}
