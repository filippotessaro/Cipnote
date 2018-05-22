package com.cipnote.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cipnote.R;
import com.cipnote.camera.PhotoActivity;
import com.cipnote.login.MainActivity;
import com.cipnote.ui.NoteActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "simplifiedcoding";
    ImageView imm_view;
    TextView txt_name, txt_email;
    Button btn_logout, btn_deleteuser;
    FirebaseAuth mAuth;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        mAuth = FirebaseAuth.getInstance();
        imm_view = findViewById(R.id.imageView);
        txt_name = findViewById(R.id.textViewName);
        txt_email = findViewById(R.id.textViewEmail);
        btn_logout = findViewById(R.id.btn_logout);
        btn_deleteuser = findViewById(R.id.btn_delete);

        FirebaseUser user = mAuth.getCurrentUser();
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imm_view);

        txt_name.setText(user.getDisplayName());
        txt_email.setText(user.getEmail());

        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SignOut();
            }
        });
        btn_deleteuser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DeleteUser();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // Left to Right swipe action
                    if (x2 > x1)
                        ProfileMode();
                    break;
                }
        }

        return super.onTouchEvent(event);
    }

    private void ProfileMode() {
        startActivity(new Intent(this, NoteActivity.class));
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        //  finish();
    }

    public void SignOut() {
        mAuth.getInstance().signOut();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void DeleteUser() {
        mAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    }
                });
    }
}
