package com.cipnote.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cipnote.R;
import com.cipnote.data.NoteEntityData;
import com.cipnote.multitouch.RecyclerItemTouchHelper;
import com.cipnote.profile.ProfileActivity;
import com.cipnote.ui.adapter.NoteListAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    String TAG = "NoteListActivity";

    DatabaseReference dbTextNotes;

    List<NoteEntityData> listNotes;
    FirebaseUser currentFirebaseUser;
    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;
    private NoteListAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    FirebaseAuth mAuth;
    ImageView imm_view;
    TextView txt_name, txt_email;
    private SearchView searchView;
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(Color.WHITE);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        recyclerView = findViewById(R.id.recycler_view);

        coordinatorLayout = findViewById(R.id.coordinator_layout);

        listNotes = new ArrayList<>();

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, (RecyclerItemTouchHelper.RecyclerItemTouchHelperListener) this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(1, ItemTouchHelper.RIGHT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(recyclerView);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.nav_settings:
                                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                                startActivity(i);
                                return true;
                            case R.id.nav_note:
                                Intent j = new Intent(getApplicationContext(), NoteActivity.class);
                                startActivity(j);
                                return true;
                        }
                        mDrawerLayout.closeDrawers();

                        return true;
                    }
                });

        View hView =  navigationView.getHeaderView(0);
        imm_view = (ImageView)hView.findViewById(R.id.profileImage);
        txt_email =(TextView) hView.findViewById(R.id.usermail) ;
        txt_name = (TextView)hView.findViewById(R.id.username);

        findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(i);

            }
        });


        mAuth = FirebaseAuth.getInstance();
        setUserProfileInformation();

    }



    private void setUserProfileInformation() {
        FirebaseUser user = mAuth.getCurrentUser();
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imm_view);
        txt_name.setText(user.getDisplayName());
        txt_email.setText(user.getEmail());

    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmerAnimation();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetDataFromFirebase().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        dbTextNotes = FirebaseDatabase.getInstance().getReference("textnote");
        dbTextNotes.keepSynced(true);
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

                mAdapter = new NoteListAdapter(NoteListActivity.this,listNotes);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.addItemDecoration(new DividerItemDecoration(NoteListActivity.this, DividerItemDecoration.VERTICAL));
                recyclerView.setAdapter(mAdapter);

                mShimmerViewContainer.stopShimmerAnimation();
                mShimmerViewContainer.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NoteListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = listNotes.get(viewHolder.getAdapterPosition()).getTitle();
            Log.i("DIR", "Direction: "+ direction);

            // backup of removed item for undo purpose
            final NoteEntityData deletedItem = listNotes.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            dbTextNotes.child(deletedItem.getId()).removeValue();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, name + " removed from List!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    dbTextNotes.child(deletedItem.getId()).setValue(deletedItem);
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }



    private class GetDataFromFirebase extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }
}
