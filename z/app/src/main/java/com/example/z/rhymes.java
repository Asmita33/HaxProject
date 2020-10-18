package com.example.z;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.URL;

public class rhymes extends AppCompatActivity {
    private EditText SearchBoxET;

    private TextView UrlDisplayTV;

    private TextView SearchResultsTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rhymes);
        SearchBoxET = (EditText) findViewById(R.id.et_search_box);
        UrlDisplayTV = (TextView) findViewById(R.id.tv_url_display);
        SearchResultsTV = (TextView) findViewById(R.id.tv_api_search_results);
    }
    private void makeWordSearchQuery()
    {
        String wordQuery = SearchBoxET.getText().toString();



        URL rhymeSearchUrl = fetchRhyme.buildUrl(wordQuery); // for printing rhymes
        UrlDisplayTV.append("\n"+rhymeSearchUrl.toString());
        new wordQueryTask().execute(rhymeSearchUrl);

    }

    public class wordQueryTask extends AsyncTask<URL, Void, String[] >
    {
        @Override
        protected String[] doInBackground(URL... urls)
        {
            URL searchUrl = urls[0];
            rhymeWord[] rhymeResults;

            try
            {
                rhymeResults = fetchRhyme.getResponseFromUrl(searchUrl);
                String[] resultsToPrint = new String[rhymeResults.length+1];
                resultsToPrint[0] = "\n\nRhymes:";

                int i=1;
                for (rhymeWord sr : rhymeResults)
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