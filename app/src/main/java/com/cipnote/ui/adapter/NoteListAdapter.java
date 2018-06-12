package com.cipnote.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cipnote.R;
import com.cipnote.data.NoteEntityData;
import com.cipnote.ui.NoteListActivity;

import java.util.List;

/**
 * Created by ravi on 26/09/17.
 */

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {
    private Context context;
    private List<NoteEntityData> noteList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description, price;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }


    public NoteListAdapter(Context context, List<NoteEntityData> cartList) {
        this.context = context;
        this.noteList = cartList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final NoteEntityData item = noteList.get(position);
        holder.name.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.price.setText(item.getDateCreation());


    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void removeItem(int position) {
        noteList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(NoteEntityData item, int position) {
        noteList.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}
