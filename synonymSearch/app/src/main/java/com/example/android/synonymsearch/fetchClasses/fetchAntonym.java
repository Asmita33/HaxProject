package com.example.android.synonymsearch.fetchClasses;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class fetchAntonym
{
    final static String baseUrl = "https://api.datamuse.com/words";
    final static String wordQuery = "rel_ant";

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

    // return array of antonyms
    public static synonymWord[] getResponseFromUrl(URL url) throws IOException
    {
        Gson gson = new GsonBuilder().create();
        synonymWord[] antonyms=null;

        try
        {
            URLConnection urlcon = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

            antonyms = gson.fromJson(br,synonymWord[].class);

        } catch (IOException e) { e.printStackTrace(); }
        catch (JsonSyntaxException e) { e.printStackTrace(); }
        catch (JsonIOException e) { e.printStackTrace(); }

        return antonyms;
    }


}
