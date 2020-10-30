package com.example.android.synonymsearch.fetchClasses;

public class wordMeaning
{
    //public Meanings[] meanings;
    public String word;
    public Phonetics[] phonetics;
    public Meanings[] meanings;

    public class Phonetics{
        String text;
        String audio;
    }

    public class Meanings
    {
        public String partOfSpeech;
        public Defs[] definitions;
        public class Defs
        {
            public String definition;
            public String example;
            public String[] synonyms;
        }


    }
}
