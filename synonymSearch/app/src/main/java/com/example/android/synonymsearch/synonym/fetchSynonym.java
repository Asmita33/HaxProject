package com.example.android.synonymsearch.synonym;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class fetchSynonym
{
    final static String baseUrl = "https://api.datamuse.com/words";
    final static String wordQuery = "rel_syn";
    //String searchUrl = baseUrl+wordQuery;

    //to take input and build url.
    // There maybe another way.
    // TO get the string directly from editText, you can extend AppCompatActivity, and use findViewById
    // but this is easier
    public static URL buildUrl(String wordSearchQuery)
    {
        Uri builtUri  = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(wordQuery,wordSearchQuery).build();
        URL url = null;
        try
        {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // return array of synonyms
    public static synonymWord[] getResponseFromUrl(URL url) throws IOException
    {
        Gson gson = new GsonBuilder().create();
        //List<synonymWord> synonyms= new ArrayList<synonymWord>();
        //Type listType = new TypeToken<ArrayList<synonymWord>>(){}.getType();
        //ArrayList<synonymWord> synonyms = null;
        synonymWord[] synonyms = null;

        try
        {
            URLConnection urlcon = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

            synonyms = gson.fromJson(br, synonymWord[].class);
            //synonyms = gson.fromJson(br,listType);

        } catch (IOException e) { e.printStackTrace(); }
        catch (JsonSyntaxException e) { e.printStackTrace(); }
        catch (JsonIOException e) { e.printStackTrace(); }

        return synonyms;
    }


}
