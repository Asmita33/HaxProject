package Networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class fetchSynonym
{
    public static void main(String[] args)
    {
        String baseUrl = "https://api.datamuse.com/words?rel_syn=";

        System.out.println("Enter a word to find its synonym: ");
        Scanner sc = new Scanner(System.in);
        String word = sc.next();
        String searchUrl = baseUrl+word;

        Gson gson = new GsonBuilder().create();


        try
        {
            URL url = new URL(searchUrl);
            URLConnection urlcon = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

            synonym[] synonyms = gson.fromJson(br,synonym[].class);

            for(synonym x : synonyms)
            {
                System.out.print(x.getWord() + "  ");
                System.out.println(x.getScore());

            }

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
