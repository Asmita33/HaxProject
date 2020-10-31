package com.example.android.synonymsearch;

import java.io.Serializable;

public class wordIntent implements Serializable {

    String word;
    public wordIntent(String word) {
        this.word = word;
    }
}
