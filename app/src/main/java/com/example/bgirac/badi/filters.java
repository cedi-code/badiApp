package com.example.bgirac.badi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class filters extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        ListView kantoneView = (ListView) findViewById(R.id.kantone);

        addKantonsliste(kantoneView);
        initClickListener(kantoneView);

    }

    private void addKantonsliste(ListView kantoneView) {

        final ArrayList<ArrayList<String>> kantone = BadiData.allKantone(getApplicationContext());
        ArrayAdapter<String> kantonsListe = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        for(ArrayList<String> b: kantone) {
            kantonsListe.add(b.get(1));

        }

        kantoneView.setAdapter(kantonsListe);
    }
    private void initClickListener(ListView kantoneView) {


        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                String selected = adapterView.getItemAtPosition(i).toString();
                final ArrayList<ArrayList<String>> kantone = BadiData.allKantone(getApplicationContext());
                for(ArrayList<String> b: kantone) {
                    if(b.get(1).equals(selected)) {
                        intent.putExtra("kanton",b.get(0));
                    }

                }
                //intent.putExtra("name", selected);
                intent.setClass(filters.this, MainActivity.class);
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        };
        kantoneView.setOnItemClickListener(mListClickedHandler);
    }
}