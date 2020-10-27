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

public class fetchMeansLike
{
    final static String baseUrl = "https://api.datamuse.com/words";
    final static String wordQuery = "ml";

    public static URL buildUrl(String wordSearchQuery)
    {
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(wordQuery, wordSearchQuery).build();
        URL url = null;
        try
        {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static meansLike[] getResponseFromUrl(URL url) throws IOException
    {
        Gson gson = new GsonBuilder().create();
        meansLike[] meanings = null;

        try
        {
            URLConnection urlcon = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

            meanings = gson.fromJson(br, meansLike[].class);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        return meanings;

    }


}
