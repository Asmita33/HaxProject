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

    private ExpandableListView expandableResultsListView;
    private ExpandableListAdapter expandableListAdapter;
    List<String> expandableResultHeadings;
    HashMap<String, List<String>> expandableListDetail;

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchBoxMACTV = (MultiAutoCompleteTextView) findViewById(R.id.MAC_TV_search_box);
        expandableResultsListView = (ExpandableListView) findViewById(R.id.expandableListView);

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


    // ----------->>> for Text To Speech, do not delete this implementation you used, until new one is done

  /*  public void speakResults()
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
    }*/
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



    // ----------->>> for Results of synonyms, antonyms, rhymes etc
    private void makeWordSearchQuery()
    {
        expandableResultHeadings = new ArrayList<String>();  //creating list of headings
        expandableResultHeadings.add("Synonyms");
        expandableResultHeadings.add("Antonyms");
        expandableResultHeadings.add("Words with similar usages");
        expandableResultHeadings.add("Rhymes");
        expandableResultHeadings.add("Words that sound similar");
        expandableResultHeadings.add("Words triggered from this word");

        expandableListDetail = new HashMap<String, List<String>>();  // HashMap of headings to respective results

        expandableListAdapter = new CustomExpandableListAdapter(this,
                expandableResultHeadings, expandableListDetail);  // initializing the adapter

        String wordQuery = SearchBoxMACTV.getText().toString();

        // for printing synonyms
        URL synonymSearchUrl = fetchSynonym.buildUrl(wordQuery);
        new wordQueryTask(new asyncResponse() {
            @Override
            public void processFinish(ArrayList<String> output) {
                expandableListDetail.put(expandableResultHeadings.get(0), output);
                //expandableResultsListView.setAdapter(expandableListAdapter);
            }
        }).execute(synonymSearchUrl);

        // for printing antonyms
        URL antonymSearchUrl = fetchAntonym.buildUrl(wordQuery);
        new wordQueryTask(new asyncResponse() {
            @Override
            public void processFinish(ArrayList<String> output) {
                expandableListDetail.put(expandableResultHeadings.get(1), output);
            }
        }).execute(antonymSearchUrl);

        // for words with similar meanings
        URL meansLikeUrl = fetchMeansLike.buildUrl(wordQuery);
        new wordQueryTask(new asyncResponse() {
            @Override
            public void processFinish(ArrayList<String> output) {
                expandableListDetail.put(expandableResultHeadings.get(2), output);
            }
        }).execute(meansLikeUrl);

        // for printing rhymes
        URL rhymeSearchUrl = fetchRhyme.buildUrl(wordQuery);
        new wordQueryTask(new asyncResponse() {
            @Override
            public void processFinish(ArrayList<String> output) {
                expandableListDetail.put(expandableResultHeadings.get(3), output);
            }
        }).execute(rhymeSearchUrl);

        // for words with similar sounds
        URL similarSoundUrl = fetchSimilarSounds.buildUrl(wordQuery);
        new wordQueryTask(new asyncResponse() {
            @Override
            public void processFinish(ArrayList<String> output) {
                expandableListDetail.put(expandableResultHeadings.get(4), output);
            }
        }).execute(similarSoundUrl);

        // to print triggers
        URL triggersUrl = fetchTriggers.buildUrl(wordQuery);

        new wordQueryTask(new asyncResponse() {
            @Override
            public void processFinish(ArrayList<String> output) {
                expandableListDetail.put(expandableResultHeadings.get(5), output);
                expandableResultsListView.setAdapter(expandableListAdapter);
            }
        }).execute(triggersUrl);

        TTSListeners();

    }

    public void TTSListeners()
    {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i!=TextToSpeech.ERROR){
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        expandableResultsListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int position)
            {
                //String toSpeak = expandableResultsListView.getText().toString();
                //Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                //tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                String toSpeak = expandableResultHeadings.get(position);
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);


            }
        });

    }



    public class wordQueryTask extends AsyncTask<URL, Void, ArrayList<String> >
    {
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

        public asyncResponse outputFromAsync = null;
        public wordQueryTask(asyncResponse delegate){
            this.outputFromAsync = delegate;
        }
        @Override
        protected void onPostExecute(ArrayList<String> results)
        {
            outputFromAsync.processFinish(results);
        }

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