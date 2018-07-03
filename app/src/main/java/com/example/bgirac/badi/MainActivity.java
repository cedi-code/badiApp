package com.example.bgirac.badi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter badiliste;
    private final static String AARBERG = "Schwimmbad Aarberg (BE)";
    private final static String ADELBODEN = "Schwimmbad Gruebi Adelboden (BE)";
    private final static String BERN = "Stadberner Baeder Bern (BE)";
    private ListView badis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }
    private void init() {
        ImageView img = (ImageView)findViewById(R.id.badilogo);
        img.setImageResource(R.mipmap.ic_launcher);
        addBadisToList();

        Intent intent = new Intent(getApplicationContext(), BadiDetailActivity.class);

        // intent.putExtra("badi","71");

        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailActivity.class);
                String selected = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();

                if (getString(R.string.badaarberg).equals(selected)) {
                    intent.putExtra("badi", "71");


                } else if (getString(R.string.badadelboden).equals(selected)) {
                    intent.putExtra("badi", "27");

                } else if (getString(R.string.badbern).equals(selected)) {
                    intent.putExtra("badi", "6");

                }
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
        /*badiliste.add(getString(R.string.badaarberg));
        badiliste.add(getString(R.string.badadelboden));
        badiliste.add(getString(R.string.badbern)); */

        for(ArrayList<String> b: allBadis) {
            badiliste.add(b.get(5)+"-"+b.get(8));


        }

        badis.setAdapter(badiliste);
    }
}
