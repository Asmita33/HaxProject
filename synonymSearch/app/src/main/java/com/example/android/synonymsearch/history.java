package com.example.android.synonymsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class history extends AppCompatActivity {

    ArrayList<String> list;
    DatabaseReference reff;
    FirebaseDatabase rootnode ;
    ListView wordlistView;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        list=new ArrayList<>();
        wordlistView=(ListView)findViewById(R.id.list);

        rootnode=FirebaseDatabase.getInstance();
        reff = rootnode.getReference().child("Search History");
        adapter= new ArrayAdapter<String>(this,R.layout.history_list_items,list);
        wordlistView.setAdapter(adapter);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren())
                {
                    list.add(ds.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w( "Failed to read value.",databaseError.toException());
            }
        });

         wordlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClipboardManager clipboard=(ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                 ClipData clip=ClipData.newPlainText("", list.get(i));
                 clipboard.setPrimaryClip(clip);
                 Toast.makeText(history.this,"Copied to clipboard",Toast.LENGTH_SHORT).show();
             }
         });
    }
}