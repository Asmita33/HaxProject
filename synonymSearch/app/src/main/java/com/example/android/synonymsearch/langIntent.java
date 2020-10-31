package com.example.android.synonymsearch;

import java.io.Serializable;

public class langIntent implements Serializable
{
    String word;
    public langIntent(String word) {
        this.word = word;
    }

}
