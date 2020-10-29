package com.example.android.synonymsearch;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.synonymsearch.fetchClasses.fetchAntonym;
import com.example.android.synonymsearch.fetchClasses.fetchRhyme;
import com.example.android.synonymsearch.fetchClasses.fetchSynonym;
import com.example.android.synonymsearch.fetchClasses.synonymWord;
import com.example.android.synonymsearch.fetchClasses.fetchMeansLike;
import com.example.android.synonymsearch.fetchClasses.fetchSimilarSounds;
import com.example.android.synonymsearch.fetchClasses.fetchTriggers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private MultiAutoCompleteTextView SearchBoxMACTV;
    private TextView UrlDisplayTV;

    private ExpandableListView expandableResultsListView;
    private ExpandableListAdapter expandableListAdapter;
    List<String> expandableResultHeadings;
    HashMap<String, List<String>> expandableListDetail;

    private Button speakButton;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchBoxMACTV = (MultiAutoCompleteTextView) findViewById(R.id.MAC_TV_search_box);
        UrlDisplayTV = (TextView) findViewById(R.id.tv_url_display);
        speakButton = (Button) findViewById((R.id.button_speak));
        expandableResultsListView = (ExpandableListView) findViewById(R.id.expandableListView);

        //populateMyList();



        doMultiAutoComplete();
        speakResults();

    }

    private void populateMyList()
    {
    /*    expandableResultHeadings = new ArrayList<String>();
        expandableResultHeadings.add("Synonyms");
        expandableResultHeadings.add("Antonyms");
        expandableResultHeadings.add("Words with similar usages");
        expandableResultHeadings.add("Rhymes");
        expandableResultHeadings.add("Words that sound similar");
        expandableResultHeadings.add("Words triggered from this word");

        expandableListDetail = new HashMap<String, String[]>(); */

        //String[] syns = new wordQueryTask

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

    // ----------->>> for Text To Speech
    public void speakResults()
    {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String toSpeak = expandableResultsListView.getText().toString();
                //Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                //tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }
    public void onPause()
    {
        if(tts!=null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
    // ----------->>> for Text To Speech



    // ----------->>> for Results of synonyms, antonyms, rhymes
    private void makeWordSearchQuery()
    {
    /*    expandableResultHeadings = new ArrayList<String>();
        expandableResultHeadings.add("Synonyms");
        expandableResultHeadings.add("Antonyms");
        expandableResultHeadings.add("Words with similar usages");
        expandableResultHeadings.add("Rhymes");
        expandableResultHeadings.add("Words that sound similar");
        expandableResultHeadings.add("Words triggered from this word");

        expandableListDetail = new HashMap<String, List<String>>();  */

        expandableResultHeadings = new ArrayList<String>();
        expandableResultHeadings.add("Synonyms");
        expandableListDetail = new HashMap<String, List<String>>();
        String wordQuery = SearchBoxMACTV.getText().toString();

        URL synonymSearchUrl = fetchSynonym.buildUrl(wordQuery); // for printing synonyms
        UrlDisplayTV.setText(synonymSearchUrl.toString());

        new wordQueryTask(new asyncResponse() {
            @Override
            public void processFinish(ArrayList<String> output) {
                List<String> syns = output;
                if(syns!=null){
                expandableListDetail.put(expandableResultHeadings.get(0), syns); }
            }
        }).execute(synonymSearchUrl);
        expandableListAdapter = new CustomExpandableListAdapter(this,
                expandableResultHeadings, expandableListDetail);
        expandableResultsListView.setAdapter(expandableListAdapter);

        //List<String> syns;
        //setResultsFromAsync(syns, );


     /*   URL antonymSearchUrl = fetchAntonym.buildUrl(wordQuery); // for printing antonyms
        UrlDisplayTV.append("\n" +antonymSearchUrl.toString());
        new wordQueryTask().execute(antonymSearchUrl);

        URL meansLikeUrl = fetchMeansLike.buildUrl(wordQuery); // for words with similar meanings
        UrlDisplayTV.append("\n" +meansLikeUrl.toString());
        new wordQueryTask().execute(meansLikeUrl);

        URL rhymeSearchUrl = fetchRhyme.buildUrl(wordQuery); // for printing rhymes
        UrlDisplayTV.append("\n" +rhymeSearchUrl.toString());
        new wordQueryTask().execute(rhymeSearchUrl);

        URL similarSoundUrl = fetchSimilarSounds.buildUrl(wordQuery); // for words with similar sounds
        UrlDisplayTV.append("\n" +similarSoundUrl.toString());
        new wordQueryTask().execute(similarSoundUrl);

        URL triggersUrl = fetchTriggers.buildUrl(wordQuery); // to print triggers
        UrlDisplayTV.append("\n" +triggersUrl.toString());
        new wordQueryTask().execute(triggersUrl);  */

    }

    public class wordQueryTask extends AsyncTask<URL, Void, ArrayList<String> >
    {
        public asyncResponse outputFromAsync = null;
        public wordQueryTask(asyncResponse delegate){
            this.outputFromAsync = delegate;
        }

        @Override
        protected ArrayList<String> doInBackground(URL... urls)
        {
            URL searchUrl = urls[0];
            synonymWord[] synonymResults;

            try
            {
                synonymResults = fetchSynonym.getResponseFromUrl(searchUrl);
                ArrayList<String> resultsToPrint = new ArrayList<String>();

                for (synonymWord sr : synonymResults)
                {
                    resultsToPrint.add(sr.getWord());
                }
                return resultsToPrint;
            }
            catch (IOException e) { e.printStackTrace(); return null; }
            
        }

        @Override
        protected void onPostExecute(ArrayList<String> results)
        {
            //if(results!=null){
            outputFromAsync.processFinish(results);   //}
        }

    }

    private void setResultsFromAsync(List<String> setTo, ArrayList<String> results)
    {
        setTo = results;
    }
    // ----------->>> for Results of synonyms, antonyms, rhymes

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.action_search) {
            makeWordSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}