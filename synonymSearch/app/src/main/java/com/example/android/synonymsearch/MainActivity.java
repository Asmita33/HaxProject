package com.example.android.synonymsearch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.synonymsearch.fetchClasses.fetchAntonym;
import com.example.android.synonymsearch.fetchClasses.fetchRhyme;
import com.example.android.synonymsearch.fetchClasses.fetchSynonym;
import com.example.android.synonymsearch.fetchClasses.synonymWord;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private EditText SearchBoxET;
    private TextView UrlDisplayTV;
    private TextView SearchResultsTV;
    private Button speakButton;
    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchBoxET = (EditText) findViewById(R.id.et_search_box);
        UrlDisplayTV = (TextView) findViewById(R.id.tv_url_display);
        SearchResultsTV = (TextView) findViewById(R.id.tv_api_search_results);
        speakButton = (Button) findViewById((R.id.button_speak));

        speakResults();

    }

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

    private void makeWordSearchQuery()
    {
        String wordQuery = SearchBoxET.getText().toString();

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

            try
            {
                synonymResults = fetchSynonym.getResponseFromUrl(searchUrl);
                String[] resultsToPrint = new String[synonymResults.length+1];
                resultsToPrint[0] = "\n\nSynonyms:";

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