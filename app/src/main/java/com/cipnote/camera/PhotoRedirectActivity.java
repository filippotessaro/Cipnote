package com.cipnote.camera;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cipnote.R;

/**
 * Created by sotsys016-2 on 13/8/16 in com.cnc3camera.
 */
public class PhotoRedirectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_redirect);


        init();

    }

    private void init() {

        ImageView imgShow = findViewById(R.id.imgShow);

        if(getIntent().getStringExtra("WHO").equalsIgnoreCase(
                "Image")) {

            imgShow.setVisibility(View.VISIBLE);

            Glide.with(PhotoRedirectActivity.this)
                    .load(getIntent().getStringExtra("PATH"))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).load(R.drawable.ic_photo_cont)
                    .into(imgShow);
        }
    }
}
