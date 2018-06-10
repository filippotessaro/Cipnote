package com.cipnote.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cipnote.R;
import com.cipnote.data.NoteEntityData;
import com.cipnote.widget.entity.TextEntity;

import java.util.List;

public class FirebaseRow extends ArrayAdapter<NoteEntityData>{
    private Activity context;
    private List<NoteEntityData> listNote;

    public FirebaseRow(Activity context, List<NoteEntityData> listNote){
        super(context, R.layout.list_firebase_layout,listNote);
        this.context = context;
        this.listNote = listNote;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_firebase_layout, null, true);

        TextView textTitleNote = (TextView)listViewItem.findViewById(R.id.title_note_firebase);
        TextView textUuidNote = (TextView) listViewItem.findViewById(R.id.uuid_note_firebase);

        NoteEntityData note = listNote.get(position);
        textTitleNote.setText(note.getTitle());

        return listViewItem;
    }
}
