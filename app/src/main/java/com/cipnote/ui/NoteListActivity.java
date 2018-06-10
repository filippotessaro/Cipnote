package com.cipnote.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.cipnote.R;
import com.cipnote.data.NoteEntityData;
import com.cipnote.ui.adapter.FirebaseRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {
    String TAG = "NoteListActivity";

    DatabaseReference dbTextNotes;

    ListView listViewNotes;
    List<NoteEntityData> listNotes;
    FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
//        dbTextNotes = FirebaseDatabase.getInstance().getReference("textnote");



        listViewNotes = (ListView)findViewById(R.id.listFirebaseNotes);
        listNotes = new ArrayList<>();


    }

    @Override
    protected void onStart() {
        super.onStart();
        dbTextNotes = FirebaseDatabase.getInstance().getReference("textnote");
//        dbTextNotes.keepSynced(true);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String userId = currentFirebaseUser.getUid();
        Query query = dbTextNotes.orderByChild("userId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.i(TAG,"esiste");
                } else {
                    Log.i(TAG, "non esiste");
                }

                listNotes.clear();
                for(DataSnapshot noteSnapshot:dataSnapshot.getChildren()){
                    NoteEntityData note = new NoteEntityData();
                    note = noteSnapshot.getValue(NoteEntityData.class);
                    listNotes.add(note);

                }

                FirebaseRow adapter = new FirebaseRow(NoteListActivity.this, listNotes);
                listViewNotes.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
