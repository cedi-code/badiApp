package com.example.bgirac.badi;

import android.app.Activity;
import android.app.Application;
import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

public class SearchResultActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_result);
        Log.i("query","geits?");
        handleIntent(getIntent());

    }



    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            Log.i("query", query);
            Intent intentMain = new Intent(getApplicationContext(), MainActivity.class);
            intentMain.putExtra("filter", query);
            intentMain.setClass(SearchResultActivity.this, MainActivity.class);
            intentMain.setFlags(intentMain.FLAG_ACTIVITY_NEW_TASK | intentMain.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentMain);

            finish();


        }
    }



}
