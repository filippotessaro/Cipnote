package com.cipnote.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cipnote.R;

import java.util.ArrayList;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    Context mcontext;
    ArrayList<RowItem> msteps;

    public ListAdapter(ArrayList<RowItem> steps, Context context){
        try {
            this.msteps = steps;
            this.mcontext = context;
        }
        catch (Exception e){
            Log.e("CIPNOTE: ", "51: " + e.toString());
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageButton plus, minus, change;
        CheckBox step;

        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(final View itemView) {
            super(itemView);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            change = itemView.findViewById(R.id.modify);
            step = itemView.findViewById(R.id.txt_checkbox);

            minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    try {
                        msteps.remove(position);
                        notifyItemRemoved(position);
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                }
            });

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    LayoutInflater inflater = (LayoutInflater)
                            mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View alertLayoutCalendar = inflater.inflate(
                            R.layout.popup_checkbox, null);

                    final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                    // this is set the view from XML inside AlertDialog
                    alert.setView(alertLayoutCalendar);
                    // disallow cancel of AlertDialog on click of back button and outside touch
                    alert.setCancelable(false);

                    final EditText edit_text_checkbox =
                            alertLayoutCalendar.findViewById(R.id.edit_text_checkbox);

                    //DONE BUTTON
                    alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final String textCheckbox = edit_text_checkbox.getText().toString();

                            if(textCheckbox.equals("")) {
                                Toast.makeText(v.getContext(), "Errore nella digitazione",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                int position = getAdapterPosition();
                                // msteps.add(new RowItem(false,textCheckbox));
                                plusCheckBox(position, textCheckbox);
                            }
                            dialog.dismiss();
                        }
                    });

                    //CANCEL BUTTON
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
            });

      /*      step.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                  //  steps.set(getAdapterPosition(), s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });*/

          /*  final GestureDetector gestureDetector = new GestureDetector(
                    itemView.getContext(),new GestureDetector.SimpleOnGestureListener() {
                public boolean onDoubleTap(View v, MotionEvent e) {
                    int position = getAdapterPosition();
                    changeCheckBox(v, position);
                    return true;
                }
            });*/

            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    changeCheckBox(v, position);
                }
            });
        }
    }

    public void plusCheckBox(int pos, String textCheckbox) {
        try {
            RowItem rowItem = new RowItem(false,textCheckbox);
            msteps.add(pos + 1, rowItem);
            notifyItemInserted(pos + 1);
        }
        catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    public void changeCheckBox(final View v, final int pos) {
        Log.d("CIPNOTE: ", "onDoubleTap");

        LayoutInflater inflater = (LayoutInflater)
                mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View alertLayoutCalendar = inflater.inflate(
                R.layout.popup_checkbox, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(
                v.getContext());
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayoutCalendar);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        final EditText edit_text_checkbox =
                alertLayoutCalendar.findViewById(R.id.edit_text_checkbox);

        //DONE BUTTON
        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String textCheckbox = edit_text_checkbox.getText().toString();

                if(textCheckbox.equals("")) {
                    Toast.makeText(v.getContext(), "Errore nella digitazione",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    msteps.get(pos).setTask(textCheckbox);
                    notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });

        //CANCEL BUTTON
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return msteps.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.checkbox,
                viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final RowItem rowItem = msteps.get(position);
        final int[] tap_count = {0};

        holder.step.setText(rowItem.getTask());

        /*int x = holder.getLayoutPosition();

        if(!(msteps.get(x).toString().equals(""))) {
            holder.step.setText(msteps.get(x).toString());
        }
        else {
            holder.step.setText(null);
            holder.step.setHint("Next Step");
            holder.step.requestFocus();
        }*/

        //in some cases, it will prevent unwanted situations
        holder.step.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.step.setChecked(rowItem.isDone());
    }

    public ArrayList<RowItem> getStepList(){
        return msteps;
    }
}