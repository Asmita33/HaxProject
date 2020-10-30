package com.example.android.synonymsearch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.synonymsearch.fetchClasses.fetchAntonym;
import com.example.android.synonymsearch.fetchClasses.fetchMeaning;
import com.example.android.synonymsearch.fetchClasses.fetchRhyme;
import com.example.android.synonymsearch.fetchClasses.fetchSynonym;
import com.example.android.synonymsearch.fetchClasses.synonymWord;
import com.example.android.synonymsearch.fetchClasses.fetchMeansLike;
import com.example.android.synonymsearch.fetchClasses.fetchSimilarSounds;
import com.example.android.synonymsearch.fetchClasses.fetchTriggers;

import com.example.android.synonymsearch.fetchClasses.wordMeaning;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private MultiAutoCompleteTextView SearchBoxMACTV;

    FirebaseDatabase rootnode ;
    DatabaseReference reference;


    Button history;
    String wordQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        history=findViewById(R.id.history);
        SearchBoxMACTV = (MultiAutoCompleteTextView) findViewById(R.id.MAC_TV_search_box);









        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(MainActivity.this,history.class);
                startActivity(i);


            }
        });



        doMultiAutoComplete();
    }

    public void doMultiAutoComplete()
    {
        ArrayList<String> dictionaryAL = new ArrayList<>();
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new InputStreamReader(getAssets()
                    .open("dict.txt"), "UTF-8"));
            String currLine;
            while((currLine = br.readLine())!=null) dictionaryAL.add(currLine);

        }
        catch (UnsupportedEncodingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

        ArrayAdapter adapter = new
                ArrayAdapter(this, android.R.layout.simple_list_item_1, dictionaryAL);

        SearchBoxMACTV.setAdapter(adapter);
        SearchBoxMACTV.setTokenizer(new SpaceTokenizer());

    }

    // ----------->>> for Results of synonyms, antonyms, rhymes etc

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {

            rootnode = FirebaseDatabase.getInstance();
            reference=rootnode.getReference().child("Search History");
            reference.push().setValue(wordQuery);
            Toast.makeText(MainActivity.this,"Data inserted successfully",Toast.LENGTH_LONG).show();
            wordQuery = SearchBoxMACTV.getText().toString();
            Intent i=new Intent(getApplicationContext(),feature.class);
            wordIntent w=new wordIntent(wordQuery);
            i.putExtra("mykey", w);
            startActivity(i);


            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}