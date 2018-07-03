package com.example.bgirac.badi;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter badiliste;
    private final static String AARBERG = "Schwimmbad Aarberg (BE)";
    private final static String ADELBODEN = "Schwimmbad Gruebi Adelboden (BE)";
    private final static String BERN = "Stadberner Baeder Bern (BE)";
    private ArrayList<String> activeFilters = new ArrayList<>();
    private ListView badis;



    //@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        // TODO remove filter!!!
        if(intent.hasExtra("kanton")) {
            if(!activeFilters.contains(intent.getStringExtra("kanton"))) {
                activeFilters.add(intent.getStringExtra("kanton"));
            }
        }
        init();



    }
    private void init() {
        ImageView img = (ImageView)findViewById(R.id.badilogo);
        img.setImageResource(R.mipmap.ic_launcher);
        addBadisToList();


        // intent.putExtra("badi","71");

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailActivity.class);
                String selected = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();
                String badii = "";
                final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
                for(ArrayList<String> b: allBadis) {
                    badii = b.get(5)+"-"+b.get(8);
                    if(badii.equals(selected)) {
                        intent.putExtra("badi",b.get(0));
                    }


                }
                //intent.putExtra("name", selected);
                startActivity(intent);

            }
        };
        badis.setOnItemClickListener(mListClickedHandler);




    }
    private void addBadisToList() {
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        badis= (ListView)findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        Intent intent = getIntent();

        for(ArrayList<String> b: allBadis) {
            if(intent.hasExtra("filter")) {

                if((    b.get(5).toLowerCase().contains(intent.getStringExtra("filter").toLowerCase()) ||
                        b.get(8).toLowerCase().contains(intent.getStringExtra("filter").toLowerCase()))) {
                    badiliste.add(b.get(5)+"-"+b.get(8));
                }

            }
            else if(intent.hasExtra("kanton")) {
                 for(String k: activeFilters) {
                     if(b.get(6).equals(k)) {
                         badiliste.add(b.get(5)+"-"+b.get(8));
                     }
                 }

            }else {
                badiliste.add(b.get(5)+"-"+b.get(8));
            }


        }

        badis.setAdapter(badiliste);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
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
        finish();

    }


    /*public void initSearchView()
    {
        final SearchView searchView =
                (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

        // Enable Submit button

        searchView.setSubmitButtonEnabled(true);

        // Change search close button image

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close);


        // Set hint and the text colors

        EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setHint("Search..");
        txtSearch.setHintTextColor(Color.DKGRAY);
        txtSearch.setTextColor(getResources().getColor(R.color.colorPrimary));


        // Set the cursor

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
*/
}
