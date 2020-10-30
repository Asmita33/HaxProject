package com.example.android.synonymsearch.fetchClasses;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class fetchMeaning
{

    final static String baseUrl = "https://api.dictionaryapi.dev/api/v2/entries";

    public static URL buildUrl(String language, String wordQuery)
    {
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendPath(language).appendPath(wordQuery).build();
        URL url = null;
        try
        {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

}
