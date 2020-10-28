package com.example.android.synonymsearch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.synonymsearch.antonym.fetchAntonym;
import com.example.android.synonymsearch.rhyme.fetchRhyme;
import com.example.android.synonymsearch.synonym.fetchSynonym;
import com.example.android.synonymsearch.synonym.synonymWord;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private EditText SearchBoxET;

    private TextView UrlDisplayTV;

    private TextView SearchResultsTV;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SearchBoxET = (EditText) findViewById(R.id.et_search_box);
        UrlDisplayTV = (TextView) findViewById(R.id.tv_url_display);
        SearchResultsTV = (TextView) findViewById(R.id.tv_api_search_results);
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

    public class wordQueryTask extends AsyncTask<URL, Void, List<String> >
    {
        @Override
        protected List<String> doInBackground(URL... urls)
        {
            URL searchUrl = urls[0];
            synonymWord[] synonymResults;

            try
            {
                synonymResults = fetchSynonym.getResponseFromUrl(searchUrl);
                //String[] resultsToPrint = new String[synonymResults.length+1];
                //resultsToPrint[0] = "\n\nSynonyms:";
                List<String> resultsToPrint = new ArrayList<String>();
                resultsToPrint.add("\n\n");

                int i=1;
                for (synonymWord sr : synonymResults)
                {
                    resultsToPrint.add(sr.getWord());
                    i++;
                }
                return resultsToPrint;
            }
            catch (IOException e) { e.printStackTrace(); return null; }
            
        }

        @Override
        protected void onPostExecute(List<String> results)
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