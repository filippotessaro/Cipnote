package com.cipnote.UploadService;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.LruCache;

import com.cipnote.R;
import com.cipnote.data.NoteEntityData;
import com.cipnote.ui.NoteListActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Service to handle uploading files to Firebase Storage.
 */
public class MyUploadService extends MyBaseTaskService {

    private static final String TAG = "MyUploadService";

    /** Intent Actions **/
    public static final String ACTION_UPLOAD = "action_upload";
    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";

    /** Intent Extras **/
    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

    private NoteEntityData EXTRA_NOTE;

    // [START declare_ref]
    private StorageReference mStorageRef;
    private DatabaseReference dbTextNotes = FirebaseDatabase.getInstance().getReference("textnote");
    private LruCache<String, Bitmap> mMemoryCache;


    // [END declare_ref]

    @Override
    public void onCreate() {
        super.onCreate();

        // [START get_storage_ref]
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // [END get_storage_ref]
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        if (ACTION_UPLOAD.equals(intent.getAction())) {
            EXTRA_NOTE = intent.getParcelableExtra("NOTE_PASSED");
            //Log.i("MYUPLOADSERVICE", EXTRA_NOTE.toString());

            //dbTextNotes.keepSynced(true);

            String child = "";
            //Controllo se prima avevo creato l'entità
            if(!EXTRA_NOTE.getId().contentEquals("")){
                child = EXTRA_NOTE.getId();
            }else{
                child = dbTextNotes.push().getKey();
                EXTRA_NOTE.setId(child);
            }

            //UPLOAD THE NOTE IN FIREBASE
            dbTextNotes.child(child).setValue(EXTRA_NOTE);

            if(!EXTRA_NOTE.getLocalPhotoUrl().equals(""))
                uploadFromUri(EXTRA_NOTE.getLocalPhotoUrl(), EXTRA_NOTE.getDrawUrl());
        }

        return START_REDELIVER_INTENT;
    }

    // [START upload_from_uri]
    private void uploadFromUri(final String url, String drawURL) {
        Log.d(TAG, "uploadFromUri:src:" + url);

        // [START_EXCLUDE]
        taskStarted();
        showProgressNotification(getString(R.string.app_name), 0, 0);
        // [END_EXCLUDE]

        // [START get_child_ref]
        // Get a reference to store file at photos/<FILENAME>.jpg
        final StorageReference photoRef = mStorageRef.child("images/"+ EXTRA_NOTE.getCloudPhotoUrl());
        // [END get_child_ref]
        final File file = new File(url);
        // Upload file to Firebase Storage
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(Uri.fromFile(file)).
                addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        showProgressNotification(getString(R.string.app_name),
                                taskSnapshot.getBytesTransferred(),
                                taskSnapshot.getTotalByteCount());
                    }
                })
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        // Forward any exceptions
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        Log.d(TAG, "uploadFromUri: upload success");

                        // Request the public download URL
                        return photoRef.getDownloadUrl();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri downloadUri) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri: getDownloadUri success");

                        // [START_EXCLUDE]
                        broadcastUploadFinished(downloadUri, Uri.fromFile(file));
                        showUploadFinishedNotification(downloadUri, Uri.fromFile(file));
                        taskCompleted();
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        // [START_EXCLUDE]
                        broadcastUploadFinished(null, Uri.fromFile(file));
                        showUploadFinishedNotification(null, Uri.fromFile(file));
                        taskCompleted();
                        // [END_EXCLUDE]
                    }
                });

            StorageReference ref = photoRef.child("draw/"+ drawURL);
            Bitmap drawBitmap = getBitmapFromMemCache(drawURL);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
        drawBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //progressDialog.dismiss();
                        //Toast.makeText(NoteActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //progressDialog.dismiss();
                        //Toast.makeText(NoteActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    });
    }
    // [END upload_from_uri]

    /**
     * Broadcast finished upload (success or failure).
     * @return true if a running receiver received the broadcast.
     */
    private boolean broadcastUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        boolean success = downloadUrl != null;

        String action = success ? UPLOAD_COMPLETED : UPLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);
    }

    /**
     * Show a notification for a finished upload.
     */
    private void showUploadFinishedNotification(@Nullable Uri downloadUrl, @Nullable Uri fileUri) {
        // Hide the progress notification
        dismissProgressNotification();

        // Make Intent to MainActivity
        Intent intent = new Intent(this, NoteListActivity.class)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        boolean success = downloadUrl != null;
        String caption = success ? "Success" : "Failed Upload :(";
        showFinishedNotification(caption, intent, success);
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPLOAD_COMPLETED);
        filter.addAction(UPLOAD_ERROR);

        return filter;
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }




}

