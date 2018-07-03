package com.example.bgirac.badi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class badis extends AppCompatActivity {


    ArrayAdapter badiliste;
    private ListView badis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badis);
        Intent intent = getIntent();
        String filter = intent.getStringExtra("filter");
        init(filter);
    }


    private void addBadisToList(String filter) {
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        badis= (ListView)findViewById(R.id.badiliste2);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        for(ArrayList<String> b: allBadis) {
            if(b.get(5).contains(filter)) {
                badiliste.add(b.get(5)+"-"+b.get(8));
            }

        }


        badis.setAdapter(badiliste);
    }

    private void init(String filter) {
        //ImageView img = (ImageView)findViewById(R.id.badilogo);
        //img.setImageResource(R.mipmap.ic_launcher);
        addBadisToList(filter);

        Intent intent = new Intent(getApplicationContext(), BadiDetailActivity.class);


        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailActivity.class);
                String selected = adapterView.getItemAtPosition(i).toString();
                // Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();
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
}
