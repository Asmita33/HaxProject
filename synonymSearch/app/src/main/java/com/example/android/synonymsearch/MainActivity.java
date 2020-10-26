package com.example.android.synonymsearch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.synonymsearch.fetchClasses.fetchAntonym;
import com.example.android.synonymsearch.fetchClasses.fetchRhyme;
import com.example.android.synonymsearch.fetchClasses.fetchSynonym;
import com.example.android.synonymsearch.fetchClasses.synonymWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private MultiAutoCompleteTextView SearchBoxMACTV;
    private TextView UrlDisplayTV;
    private TextView SearchResultsTV;
    private Button speakButton;
    TextToSpeech tts;
    String[] resultsHeading = {"\n\nSynonyms:", "\n\nAntonyms:", "\n\nRhymes:"};
    int headingIndex=-1;  // currently showing only 1 result, coz then arrayIndexOutOfBounds coz headingIndex >3 then.
    // need a way to reset headingIndex to -1 after each search


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchBoxMACTV = (MultiAutoCompleteTextView) findViewById(R.id.MAC_TV_search_box);
        UrlDisplayTV = (TextView) findViewById(R.id.tv_url_display);
        SearchResultsTV = (TextView) findViewById(R.id.tv_api_search_results);
        speakButton = (Button) findViewById((R.id.button_speak));

        doMultiAutoComplete();
        speakResults();

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
                String toSpeak = SearchResultsTV.getText().toString();
                //Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
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
        String wordQuery = SearchBoxMACTV.getText().toString();

        URL synonymSearchUrl = fetchSynonym.buildUrl(wordQuery); // for printing synonyms
        UrlDisplayTV.setText(synonymSearchUrl.toString());
        new wordQueryTask().execute(synonymSearchUrl);

        URL antonymSearchUrl = fetchAntonym.buildUrl(wordQuery); // for printing antonyms
        UrlDisplayTV.append("\n" +antonymSearchUrl.toString());
        new wordQueryTask().execute(antonymSearchUrl);

        URL rhymeSearchUrl = fetchRhyme.buildUrl(wordQuery); // for printing rhymes
        UrlDisplayTV.append("\n" +rhymeSearchUrl.toString());
        new wordQueryTask().execute(rhymeSearchUrl);

    }

    public class wordQueryTask extends AsyncTask<URL, Void, String[] >
    {
        @Override
        protected String[] doInBackground(URL... urls)
        {
            URL searchUrl = urls[0];
            synonymWord[] synonymResults;
            headingIndex++;

            try
            {
                synonymResults = fetchSynonym.getResponseFromUrl(searchUrl);
                String[] resultsToPrint = new String[synonymResults.length+1];
                resultsToPrint[0] = resultsHeading[headingIndex];

                int i=1;
                for (synonymWord sr : synonymResults)
                {
                    resultsToPrint[i] = sr.getWord();
                    i++;
                }
                return resultsToPrint;
            }
            catch (IOException e) { e.printStackTrace(); return null; }
            
        }

        @Override
        protected void onPostExecute(String[] results)
        {
            if(results!=null)
            {
                for(String s : results)
                {
                    SearchResultsTV.append((s) + "\n\n");
                }
            }

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