package com.cipnote.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.cipnote.camera.PhotoActivity;
import com.cipnote.camera.RunTimePermission;
import com.cipnote.data.NoteEntityData;
import com.cipnote.data.TextEntityData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.transitionseverywhere.*;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.cipnote.BuildConfig;
import com.cipnote.R;
import com.cipnote.ui.adapter.FontsAdapter;
import com.cipnote.utils.FontProvider;
import com.cipnote.viewmodel.Font;
import com.cipnote.viewmodel.Layer;
import com.cipnote.viewmodel.PaintView;
import com.cipnote.viewmodel.TextLayer;
import com.cipnote.widget.MotionView;
import com.cipnote.widget.entity.ImageEntity;
import com.cipnote.widget.entity.MotionEntity;
import com.cipnote.widget.entity.TextEntity;
import com.transitionseverywhere.Recolor;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;
import java.util.Map;


public class NoteActivity extends AppCompatActivity
        implements TextEditorDialogFragment.OnTextLayerCallback, AIListener {

    //Variabile per apertura popup nella modifica dello spessore del tratto di disegno
    Dialog strokeDialog;
    private RunTimePermission runTimePermission;

    //Variabile per creare Log all interno della Main Activity
    private static final String TAG = "MyActivity";

    public static final int SELECT_STICKER_REQUEST_CODE = 123;
    public static final int PHOTO_REQUEST_CODE = 124;


    //Array di interi contenente i colori di sfondo
    private Integer colorIndex = 0;//Indice per tenere salvato il colore attuale
    private String[] allColors;

    //Dichiaro tutte le variabili per la manipolazione dell'interfaccia grafica
    protected PaintView paintView;
    protected MotionView motionView;
    protected View textEntityEditPanel;
    protected ConstraintLayout MainLayout;

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference dbTextNotes;

    private FontProvider fontProvider;
    private ViewGroup transitionMainViewContainer;
    private LinearLayout verticalEditMenu;
    private EditText editTextTitle;

    //ELEMENTI DA NASCONDERE (ESCLUSO MENU VERTICALE)
    protected Button saveNoteButton;
    protected ImageButton recordButton;
    protected ImageButton deleteNoteButton;

    //Scroll View
    protected EditText edit_text_scroll_view;
    protected TextView text_scroll_view;
    protected ImageButton modify_scroll_view;

    //Inizializzaizone DialogFlow
    private AIService aiService;

    private String userId ;
    private SlidingUpPanelLayout d;


    private final MotionView.MotionViewCallback motionViewCallback =
            new MotionView.MotionViewCallback() {
        @Override
        public void onEntitySelected(@Nullable MotionEntity entity) {
            if (entity instanceof TextEntity ) {
                Log.i(TAG, "VISIBLE");
                Log.i(TAG, "Remove menu Panel");
                TransitionManager.beginDelayedTransition(transitionMainViewContainer);
                hideShowComponents();
                textEntityEditPanel.setVisibility(View.VISIBLE);
                textEntityEditPanel.bringToFront();
            } else {
                Log.i(TAG, "GONE");
                textEntityEditPanel.setVisibility(View.GONE);
                hideShowComponents();
            }
        }

        @Override
        public void onEntityDoubleTap(@NonNull MotionEntity entity) {
            startTextEntityEditing();
        }
    };
    private ProgressBar progressRecordBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        userId = currentFirebaseUser.getUid();
        Log.i(TAG,"UserId: " + userId);

        runTimePermission = new RunTimePermission(this);
        runTimePermission.requestPermission(new String[]{
                Manifest.permission.RECORD_AUDIO
        }, new RunTimePermission.RunTimePermissionListener() {

            @Override
            public void permissionGranted() {
                // First we need to check availability of play services
            }

            @Override
            public void permissionDenied() {
                finish();
            }
        });


        //Inizializzazione Firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        dbTextNotes = FirebaseDatabase.getInstance().getReference("textnote");

        //Inizializzazione servizio apiai
        final AIConfiguration config = new AIConfiguration("e28f9bbca842430b8ce82b59291e762f",
                AIConfiguration.SupportedLanguages.Italian,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        //Inizializzazione colori background
        allColors = getResources().getStringArray(R.array.colors);

        // Now get a handle to any View contained
        // within the main layout you are using

        // Find the root view
        MainLayout = findViewById(R.id.activity_note);
        verticalEditMenu = findViewById(R.id.vertical_menu_notes);
        transitionMainViewContainer = MainLayout;

        this.fontProvider = new FontProvider(getResources());

        //Inizializzazione della vista per fare i disegni
        paintView = findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

        //Inizializzazione contenitore di testi e di immamgini
        textEntityEditPanel = findViewById(R.id.main_motion_text_entity_edit_panel);
        motionView = findViewById(R.id.main_motion_view);
        motionView.setMotionViewCallback(motionViewCallback);
        motionView.bringToFront();

        //INIZIALIZZAZIONE BOTTONI
        strokeDialog = new Dialog(this);
        initTextEntitiesListeners();
        initPaintViewListeners();
        initEditMenuEntitiesListeners();
        initShowHideElement();
        initScrollViewElements();

//        d= findViewById(R.id.sliding_layout);
//        d.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//        Log.i(TAG,""+d.getPanelState());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(runTimePermission!=null){
            runTimePermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void initScrollViewElements() {
        edit_text_scroll_view = findViewById(R.id.edit_text_scroll_view);
        text_scroll_view = findViewById(R.id.text_scroll_view);
        modify_scroll_view = findViewById(R.id.modify_scroll_view);

        modify_scroll_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = modify_scroll_view.getDrawable();
                if (drawable.getConstantState().equals(getResources().getDrawable(R.drawable.ic_mode_edit).getConstantState())){
                    edit_text_scroll_view.setText(text_scroll_view.getText().toString());
                    text_scroll_view.setVisibility(View.GONE);
                    edit_text_scroll_view.setVisibility(View.VISIBLE);
                    modify_scroll_view.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_24dp));
                }else{
                    text_scroll_view.setText(edit_text_scroll_view.getText().toString());
                    text_scroll_view.setVisibility(View.VISIBLE);
                    edit_text_scroll_view.setVisibility(View.GONE);
                    modify_scroll_view.setImageDrawable(getResources().getDrawable(R.drawable.ic_mode_edit));
                }
            }
        });

    }

    private void initShowHideElement(){
        saveNoteButton = findViewById(R.id.saveNoteButton);
        recordButton = findViewById(R.id.recordButton);
        progressRecordBar = (ProgressBar) findViewById(R.id.progressRecordBar);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                aiService.startListening();
                progressRecordBar.setVisibility(View.VISIBLE);
                progressRecordBar.bringToFront();
                progressRecordBar.setIndeterminate(true);

            }
        });

        deleteNoteButton = findViewById(R.id.deleteNoteButton);
        deleteNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAll();
            }
        });
    }


    private void initPaintViewListeners() {

        findViewById(R.id.paintView_stroke_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Apertura stroke dialog");

                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.popuppaintview, null);

                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Spessore Tratto");
                // this is set the view from XML inside AlertDialog
                alert.setView(alertLayout);
                // disallow cancel of AlertDialog on click of back button and outside touch
                alert.setCancelable(false);

                //SEEKBAR FOR STROKE OF DRAW
                final SeekBar simpleSeekBar = alertLayout.findViewById(R.id.strokeBar);
                simpleSeekBar.setProgress(paintView.getstrokeWidth());
                final TextView strokeValue = alertLayout.findViewById(R.id.strokeValue);
                strokeValue.setText(""+ paintView.getstrokeWidth());

                simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progressChangedValue = 0;

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressChangedValue = progress;
                        strokeValue.setText(""+ progressChangedValue);
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }
                });

                //CANCEL BUTTON
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                //DONE BUTTON
                alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paintView.setStrokeWidth(simpleSeekBar.getProgress());
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
            }
        });

        findViewById(R.id.paintView_color_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePaintViewColor();
            }
        });

        findViewById(R.id.paintView_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Clear Disegno");
                paintView.clear();
            }
        });
        findViewById(R.id.paintView_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Remove menu Panel");
                TransitionManager.beginDelayedTransition(transitionMainViewContainer);
                hideShowComponents();
                Log.i(TAG, "Open PaintView for drawing");
                paintView.bringToFront();
                findViewById(R.id.main_motion_draw_entity_edit_panel).setVisibility(View.GONE);
                motionView.bringToFront();
                editTextTitle.bringToFront();
            }
        });

    }

    private void changePaintViewColor() {
        Log.i(TAG,"Cambio colore Disegno");

        int initialColor = paintView.getColor();

        ColorPickerDialogBuilder
                .with(NoteActivity.this)
                .setTitle(R.string.select_color)
                .initialColor(initialColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(4)
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if (paintView != null) {
                            paintView.setCurrentColor(selectedColor);//mi fa invalidate() già dentro il metodo
                            //In questo modo si aggiorna subito la vista e il colore di essa
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    //Inizializza gli eventListener del menu di creazione degli elementi della nota
    private void initEditMenuEntitiesListeners() {
        editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        editTextTitle.bringToFront();


        findViewById(R.id.startCamera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraActivity();
            }
        });
        findViewById(R.id.checkbox_Image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this, StickerSelectActivity.class);
                startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
            }
        });
        //Aggiungere un text sticker
        findViewById(R.id.add_new_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Text Entities inizializzato");
                addTextSticker("");
            }
        });

        //Cambio background color usando e agiornando il color index e lo String Array
        findViewById(R.id.change_background_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorIndex == allColors.length-1){
                    colorIndex = 0;
                }
                else {
                    colorIndex++;
                }
                Log.i(TAG,"allocato colorIndex");
                //Cambio con transizione da un colore all'altro
                TransitionManager.beginDelayedTransition(transitionMainViewContainer,
                       new Recolor());

                MainLayout.setBackgroundDrawable(
                        new ColorDrawable(Color.parseColor(allColors[colorIndex])));
            }
        });

        //aggiungi disegno
        findViewById(R.id.add_draw).setOnClickListener(new View.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Remove menu Panel");
                TransitionManager.beginDelayedTransition(transitionMainViewContainer);
                findViewById(R.id.main_motion_draw_entity_edit_panel).bringToFront();
                hideShowComponents();
                Log.i(TAG, "Open PaintView for drawing");
                paintView.bringToFront();
                findViewById(R.id.main_motion_draw_entity_edit_panel).setVisibility(View.VISIBLE);
                findViewById(R.id.main_motion_draw_entity_edit_panel).bringToFront();
            }
        });
    }

    private void addSticker(final int stickerResId) {
        motionView.post(new Runnable() {
            @Override
            public void run() {
                Layer layer = new Layer();
                Bitmap pica = BitmapFactory.decodeResource(getResources(), stickerResId);
                ImageEntity entity = new ImageEntity(layer, pica, motionView.getWidth(),
                        motionView.getHeight());
                motionView.addEntityAndPosition(entity);
            }
        });
    }

    //Inizializza il menu di modifica del Text Sticker
    private void initTextEntitiesListeners() {
        findViewById(R.id.text_entity_add_dot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDot();
            }
        });
        findViewById(R.id.text_entity_color_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "CAMBIO COLORE TESTO ATTIVATO!");
                changeTextEntityColor();
            }
        });
        findViewById(R.id.text_entity_font_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "CAMBIO FONT ATTIVATO");
                changeTextEntityFont();
            }
        });
        findViewById(R.id.text_entity_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTextEntityEditing();
            }
        });
    }

    private void changeTextEntityColor() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity == null) {
            return;
        }

        int initialColor = textEntity.getLayer().getFont().getColor();

        ColorPickerDialogBuilder
                .with(NoteActivity.this)
                .setTitle(R.string.select_color)
                .initialColor(initialColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(4)
                .setPositiveButton(R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        TextEntity textEntity = currentTextEntity();
                        if (textEntity != null) {
                            textEntity.getLayer().getFont().setColor(selectedColor);
                            textEntity.updateEntity();
                            motionView.invalidate();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void changeTextEntityFont() {
        final List<String> fonts = fontProvider.getFontNames();
        FontsAdapter fontsAdapter = new FontsAdapter(this, fonts, fontProvider);
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_font)
                .setAdapter(fontsAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        TextEntity textEntity = currentTextEntity();
                        if (textEntity != null) {
                            textEntity.getLayer().getFont().setTypeface(fonts.get(which));
                            textEntity.updateEntity();
                            motionView.invalidate();
                        }
                    }
                })
                .show();
    }

    private void startTextEntityEditing() {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextEditorDialogFragment fragment = TextEditorDialogFragment.getInstance(textEntity.getLayer().getText());
            fragment.show(getFragmentManager(), TextEditorDialogFragment.class.getName());
        }
    }

    @Nullable
    private TextEntity currentTextEntity() {
        if (motionView != null && motionView.getSelectedEntity() instanceof TextEntity) {
            return ((TextEntity) motionView.getSelectedEntity());
        } else {
            return null;
        }
    }

    protected void addTextSticker(String content) {

        TextLayer textLayer = createTextLayer(content);
        TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(),
                motionView.getHeight(), fontProvider);
        motionView.addEntityAndPosition(textEntity);

        // move text sticker up so that its not hidden under keyboard
        PointF center = textEntity.absoluteCenter();
        center.y = center.y * 0.5F;
        textEntity.moveCenterTo(center);

        // redraw
        motionView.invalidate();
        startTextEntityEditing();
    }

    private TextLayer createTextLayer(String text) {
        TextLayer textLayer = new TextLayer();
        Font font = new Font();

        font.setColor(TextLayer.Limits.INITIAL_FONT_COLOR);
        font.setSize(TextLayer.Limits.INITIAL_FONT_SIZE);
        font.setTypeface(fontProvider.getDefaultFontName());

        textLayer.setFont(font);

        if (BuildConfig.DEBUG) {
            textLayer.setText(text);
        }

        return textLayer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {//ritorno dall'activity dello sticker
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_STICKER_REQUEST_CODE) {
                if (data != null) {
                    int stickerId = data.getIntExtra(StickerSelectActivity.EXTRA_STICKER_ID, 0);
                    if (stickerId != 0) {
                        addSticker(stickerId);
                    }
                }
            } else if(requestCode == PHOTO_REQUEST_CODE){
                if(data!=null){
                    String url = data.getStringExtra("photoUrl");
                    Log.i(TAG,"Photo Url: " + url);
                    if(url!= null || url!=""){
                        changePhotoBackground(url);
                    }

                }
            }
        }
    }

    private void changePhotoBackground(String url) {
        Log.i(TAG, url);
        File file = new File(url);
        if(MainLayout!=null){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                MainLayout.setBackground(drawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void textChanged(@NonNull String text) {
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextLayer textLayer = textEntity.getLayer();
            if (!text.equals(textLayer.getText())) {
                textLayer.setText(text);
                textEntity.updateEntity();
                motionView.invalidate();
            }
        }
    }

    public void addDot(){
        TextEntity textEntity = currentTextEntity();
        if (textEntity != null) {
            TextLayer textLayer = textEntity.getLayer();
                textLayer.addDot();
                textEntity.updateEntity();
                motionView.invalidate();
        }
    }

    public void uploadImage(View v) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
        ref.putBytes(paintView.convertBitmapToByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(NoteActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(NoteActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }

    public void loadImagePaintView(View v){
        // Create a storage reference from our app
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl("" +
                "gs://drawingapp-28b20.appspot.com/images/049438e3-b73a-43ef-9f6b-4cd0922b9e98");

        final long ONE_MEGABYTE = 1024 * 1024;
        gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                paintView.drawByteArray(bytes);
//                paintView.invalidate();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    private void hideShowComponents(){
        TransitionManager.beginDelayedTransition(transitionMainViewContainer);
        if(verticalEditMenu.getVisibility()== View.GONE && currentTextEntity()==null){
            Log.i(TAG,"Show Elements");
            verticalEditMenu.setVisibility(View.VISIBLE);
            deleteNoteButton.setVisibility(View.VISIBLE);
            recordButton.setVisibility(View.VISIBLE);
            saveNoteButton.setVisibility(View.VISIBLE);
            editTextTitle.setVisibility(View.VISIBLE);
            findViewById(R.id.startCamera).setVisibility(View.VISIBLE);

            saveNoteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveNote();
                }
            });
        }else{
            verticalEditMenu.setVisibility(View.GONE);
            deleteNoteButton.setVisibility(View.GONE);
            recordButton.setVisibility(View.GONE);
            saveNoteButton.setVisibility(View.GONE);
            editTextTitle.setVisibility(View.GONE);
            findViewById(R.id.startCamera).setVisibility(View.GONE);


        }

    }

    private void deleteAll(){

    }

    private void getAllEntities(){

        //Algoritmo per ripristinare le text entities
        List<MotionEntity> l = motionView.getEntities();
//        Log.i(TAG,""+ l.size());
        if(l.size()>=1){
            Log.i(TAG, "" + l.get(0).getLayer().getRotationInDegrees());
            Log.i(TAG, "x: "+ l.get(0).getLayer().getX());
            Log.i(TAG, "y: "+ l.get(0).getLayer().getY());
            Log.i(TAG, "Scale: "+ l.get(0).getLayer().getScale());
            TextEntity textEntity = (TextEntity) l.get(0);
            Log.i(TAG, "Text: "+ textEntity.getLayer().getText());
            Log.i(TAG, "Size Font: "+ textEntity.getLayer().getFont().getTypeface());
            Log.i(TAG, "Size Font: "+ textEntity.getLayer().getFont().getSize());

        }

        final List<String> fonts = fontProvider.getFontNames();
        String searchString = "Lato";
        int index = -1;
        for (int i=0;i<fonts.size();i++) {
            if (fonts.get(i).equals(searchString)) {
                index = i;
                break;
            }
        }

        TextEntity t = (TextEntity) l.get(0);
        t.getLayer().setX((float) -0.02777778);
        t.getLayer().setY((float) 0.72161454);
        t.getLayer().getFont().setTypeface(fonts.get(index));
        t.updateEntity();
        t.getLayer().setRotationInDegrees(45);

        String id = "jLCqygWwccVyD6a8slzqX2j2unq2";
        String child = dbTextNotes.push().getKey();
//        TextEntityData te = new TextEntityData(id,(float) -0.02777778,(float) 0.72161454, t.getLayer().getText(), "Lato" , 45,t.getLayer().getScale() );
//        dbTextNotes.child(child).setValue(te);
        Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show();
        //Algoritmo per ripristinare le Sticker entity
//        ImageEntity pica = (ImageEntity)l.get(1);
//        pica.getLayer().setX((float) -0.02777778);
//        pica.getLayer().setY((float) 0.72161454);

        Query query = dbTextNotes.orderByChild("id").equalTo(id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot user: snapshot.getChildren()) {
                    Log.i(TAG,user.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });



        motionView.invalidate();

    }

    private void SaveNote() {
        //Algoritmo per ripristinare le text entities
        List<MotionEntity> l = motionView.getEntities();

        NoteEntityData n = new NoteEntityData("", userId , editTextTitle.getText().toString(), "descrizionfhkjfhfkjf");
        int deg= 0;
        float x = 0;
        float y = 0;
        float scale = 0;
        String font, contentText;
        int color;

        for (int i = 0; i < l.size(); i++){
            if(l.get(i) instanceof TextEntity){
                deg = (int) l.get(i).getLayer().getRotationInDegrees();
                x = l.get(i).getLayer().getX();
                y = l.get(i).getLayer().getY();
                scale = l.get(i).getLayer().getScale();
                font  = ((TextEntity)l.get(i)).getLayer().getFont().getTypeface();
                color = ((TextEntity)l.get(i)).getLayer().getFont().getColor();
                contentText = ((TextEntity)l.get(i)).getLayer().getText();
                TextEntityData t = new TextEntityData(x, y,contentText,font, deg,scale, color);
                n.addTextElement(t);
            } else {
                Log.i(TAG,"Dovrebbe essere un immagine");
            }


        }


        try{
            String child = dbTextNotes.push().getKey();
            n.setId(child);
            dbTextNotes.child(child).setValue(n);
            Toast.makeText(this, "Note Added", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(this, "Problem with Connection", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onResult(AIResponse response) {
        final Result result = response.getResult();
        final HashMap<String, JsonElement> params = result.getParameters();
        String action = "";
        String condition = "";

        if(params.get("action") != null){
            action = params.get("action").getAsString();
        }
        if(params.get("condition")!= null){
            condition = params.get("condition").getAsString();
        }

        Log.i("DIALOG",action);
        switch(action){
            case "addtext":
                if(params.get("condition")!= null){
                    if(result.getResolvedQuery().contains("con scritto")){
                        String[] split = result.getResolvedQuery().split("con scritto");
                        addTextSticker(split[1]);

                    } else if(result.getResolvedQuery().contains("con ")){
                        String[] split = result.getResolvedQuery().split("con ");
                        addTextSticker(split[1]);
                    }
                } else if (result.getResolvedQuery().contains("scrivi ")){

                    String[] split = result.getResolvedQuery().split("scrivi ");
                    addTextSticker(split[1]);
                } else if (result.getResolvedQuery().contains("Scrivi ")){

                    String[] split = result.getResolvedQuery().split("Scrivi ");
                    addTextSticker(split[1]);
                }

                break;
            case "camera":
                openCameraActivity();
                break;
            case "events":
                //TODO crea evento
                break;
            case "sticker":
                Intent intent = new Intent(NoteActivity.this, StickerSelectActivity.class);
                startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE);
                break;
            default:
                Log.i(TAG,"No action");
                //TODO crea testo con query result
                addTextSticker(result.getResolvedQuery());
                break;

        }
    }

    @Override
    public void onError(AIError error) {
        Log.i(TAG,error.toString());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {
        progressRecordBar.setIndeterminate(false);
        progressRecordBar.setVisibility(View.GONE);
    }

    public void openCameraActivity(){
        Log.i(TAG,"open camera activity");
        Intent intent = new Intent(NoteActivity.this, PhotoActivity.class);
        startActivityForResult(intent, PHOTO_REQUEST_CODE);
    }
}
