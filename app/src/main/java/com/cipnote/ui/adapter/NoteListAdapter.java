package com.cipnote.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cipnote.R;
import com.cipnote.data.NoteEntityData;
import com.cipnote.data.TextEntityData;
import com.cipnote.ui.NoteListActivity;
import com.cipnote.widget.entity.TextEntity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
        public ImageButton moreButton;
        public ImageView category;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            moreButton = view.findViewById(R.id.itemoption);
            category = view.findViewById(R.id.category);
        }
    }


    public NoteListAdapter(Context context, List<NoteEntityData> notelist) {
        this.context = context;
        this.noteList = notelist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final NoteEntityData note = noteList.get(position);
        holder.name.setText(note.getTitle());
        holder.description.setText(note.getDescription());
        holder.price.setText(note.getDateCreation());

        setCategoryImage(holder, note.getCategory());

        holder.moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PopupMenu popup = new PopupMenu(context, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.more_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final int index;
                        switch (item.getItemId()) {
                            case R.id.categories:
                                //handle menu1 click

                                    setCategory(note, 0);
                                    Log.i("NoteListAdapter","Cliccato categories" + note.getTitle());

                                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View alertLayout = inflater.inflate(R.layout.popup_categories, null);

                                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                                alert.setTitle(R.string.setcategory);
                                // this is set the view from XML inside AlertDialog
                                alert.setView(alertLayout);
                                // disallow cancel of AlertDialog on click of back button and outside touch
                                alert.setCancelable(true);

                                index = note.getCategory();

                                final RadioGroup radioGroup = (RadioGroup) alertLayout.findViewById(R.id.radiogroup);
//                                radioGroup.check(index);
                                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                                {
                                    @Override
                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                                        // checkedId is the RadioButton selected
//                                        View radioButton = radioGroup.findViewById(checkedId);
//                                        index = radioGroup.indexOfChild(radioButton);
                                    }
                                });

                                //CANCEL BUTTON
                                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });

                                //DONE BUTTON
                                alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //paintView.setStrokeWidth(simpleSeekBar.getProgress());

                                        int selectedID = radioGroup.getCheckedRadioButtonId();
                                        View radioButton = radioGroup.findViewById(selectedID);
                                        int index = radioGroup.indexOfChild(radioButton);

                                        Log.i("Check", "" + index);
                                        setCategoryImage(holder, index);
                                        setCategory(note, index);
                                    }
                                });
                                AlertDialog dialog = alert.create();
                                dialog.show();

                                return true;
                            case R.id.sharenote:
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("text/plain");
                                share.putExtra(Intent.EXTRA_TEXT, allNoteText(note));
                                context.startActivity(Intent.createChooser(share, "Share using"));
                                //handle menu2 click
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });



    }

    private void setCategoryImage(MyViewHolder holder, int category) {
        switch (category){
            case 0:
                holder.category.setImageResource(R.drawable.ic_check_box);
                break;
            case 1:
                holder.category.setImageResource(R.drawable.ic_work);
                break;
            case 2:
                holder.category.setImageResource(R.drawable.ic_family);
                break;
            case 3:
                holder.category.setImageResource(R.drawable.ic_beach);
                break;
            case 4:
                holder.category.setImageResource(R.drawable.ic_school);
                break;
            case 5:
                holder.category.setImageResource(R.drawable.ic_sport);
                break;
            default:
                return;

        }
    }

    public void setCategory(NoteEntityData e, int category){
        e.setCategory(category);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("textnote");
        rootRef.child(e.getId()).child("category").setValue(category);
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

    public String allNoteText(NoteEntityData n){
        String s = "";
        s += n.getTitle() + "\n";
        s += n.getDescription() + "\n";
        List<TextEntityData> list = n.getTextEntityDataList();
        for(int i=0;i<list.size();i++){
            s += list.get(i).getText() + "\n";
        }
        return s;
    }


}
