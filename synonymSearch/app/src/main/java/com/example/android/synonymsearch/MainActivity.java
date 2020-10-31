package com.example.android.synonymsearch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private MultiAutoCompleteTextView SearchBoxMACTV,MakeNotes;
    private FloatingActionButton fabMain;
    boolean isRotate = false;
    private FloatingActionButton fabSpeak;
    private FloatingActionButton fabHistory;
    private FloatingActionButton fabNote;
    private Spinner spinner;

    String language;

    FirebaseDatabase rootnode ,rootnodeS;
    DatabaseReference reference,referenceS;

    Button save,clear;
    String wordQuery;
    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchBoxMACTV = (MultiAutoCompleteTextView) findViewById(R.id.MAC_TV_search_box);
        MakeNotes=(MultiAutoCompleteTextView) findViewById(R.id.MAC_TV_notes);
        fabMain = (FloatingActionButton) findViewById(R.id.fab_main);
        fabSpeak = (FloatingActionButton) findViewById(R.id.fabSpeak);
        fabHistory = (FloatingActionButton) findViewById(R.id.fabHistory) ;
        fabNote = (FloatingActionButton) findViewById(R.id.fabnote) ;
        save=findViewById(R.id.save);
        clear=findViewById(R.id.clear);
        spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("en"); //English
        categories.add("hi"); //
        categories.add("es"); //
        categories.add("fr"); //
        categories.add("ja"); //
        categories.add("ru"); //
        categories.add("de"); //
        categories.add("it"); //
        categories.add("ko"); //
        categories.add("pt-BR"); //
        categories.add("ar"); //
        categories.add("tr");//

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MakeNotes.setText("");
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note=MakeNotes.getText().toString();
                rootnodeS = FirebaseDatabase.getInstance();
                referenceS=rootnodeS.getReference().child("Notes");
                referenceS.push().setValue(note);
                Toast.makeText(MainActivity.this,"Note Saved!!!!",Toast.LENGTH_LONG).show();

            }
        });



        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRotate = ViewAnimation.rotateFab(view, !isRotate);
                if(isRotate){
                    ViewAnimation.showOut(fabSpeak);
                    ViewAnimation.showOut(fabHistory);
                    ViewAnimation.showOut(fabNote);
                }else{
                    ViewAnimation.showIn(fabSpeak);
                    ViewAnimation.showIn(fabHistory);
                    ViewAnimation.showIn(fabNote);
                }
            }
        });
        fabSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toSpeak = SearchBoxMACTV.getText().toString();
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                isRotate = ViewAnimation.rotateFab(view, !isRotate);
            }
        });

        fabHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRotate = ViewAnimation.rotateFab(view, !isRotate);
                Intent i=new Intent(MainActivity.this,history.class);
                startActivity(i);
            }
        });
        fabNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRotate = ViewAnimation.rotateFab(view, !isRotate);
                Intent i=new Intent(MainActivity.this,notes.class);
                startActivity(i);
            }
        });

    /*    history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(MainActivity.this,history.class);
                startActivity(i);
            }
        }); */


        doMultiAutoComplete();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        String item = parent.getItemAtPosition(pos).toString();
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        language = item;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
        String item = parent.getItemAtPosition(0).toString();
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        language = item;

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

        MakeNotes.setAdapter(adapter);
        MakeNotes.setTokenizer(new SpaceTokenizer());

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
            langIntent l = new langIntent(language);
            i.putExtra("langKey", l);
            startActivity(i);


            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}