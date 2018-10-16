package com.cipnote.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cipnote.Calendar.RangeTimePickerDialog;
import com.cipnote.UploadService.MyUploadService;
import com.cipnote.camera.CameraPermissionActivity;
import com.cipnote.camera.PhotoActivity;
import com.cipnote.camera.RunTimePermission;
import com.cipnote.data.CalendarEntity;
import com.cipnote.data.ImageEntityData;
import com.cipnote.data.NoteEntityData;
import com.cipnote.data.TextEntityData;
import com.cipnote.ui.adapter.ListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.transitionseverywhere.*;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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


public class NoteActivity extends AppCompatActivity
        implements TextEditorDialogFragment.OnTextLayerCallback, AIListener, RangeTimePickerDialog.ISelectedTime {

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
    private EditText editTextTitle, titleCalendar;

    //ELEMENTI DA NASCONDERE (ESCLUSO MENU VERTICALE)
    protected Button saveNoteButton;
    protected ImageButton recordButton;
    protected ImageButton deleteNoteButton;

    //Scroll View
    protected EditText edit_text_scroll_view;
    protected TextView text_scroll_view, text_date, text_time_start, text_time_end;
    protected ImageButton modify_scroll_view, add_check_box;

    //Calendar
    private Calendar myCalendarStart = Calendar.getInstance();
    private Calendar myCalendarEnd = Calendar.getInstance();
    private boolean check, checkbox;
    private CheckBox checkBoxDay;
    String stringTitleCalendar, stringDescription;
    private EditText txt_description, edit_text_checkbox;

    private GestureDetector gd;
    private Uri uri;
    private TextView calendarSticker;

    private ArrayList<RowItem> list_checkbox = new ArrayList<RowItem>();
    private ListView lst_check;
    private ListAdapter customAdapter;
    private String textCheckbox;


    //Inizializzaizone DialogFlow
    private AIService aiService;

    private String userId ;
    private SlidingUpPanelLayout slidingPanel;
    private FirebaseUser currentFirebaseUser;

    float dX;
    float dY;
    int lastAction;

    private NoteEntityData restoredNote = null;
    private  String restoredIdNote = "";
    private String localPhotoBackgroungUrl = "";

    private String cloudPhotoUrl = "";

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private CalendarEntity calendarEntity;

    //caching for draw bitmaps
    private LruCache<String, Bitmap> mMemoryCache;


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

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
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


        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
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

        slidingPanel = findViewById(R.id.sliding_layout);

        //----------------CALENDAR--------------
        gd = new GestureDetector(this,new OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.i("Single","Single Tap");
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                    float distanceY) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                // TODO Auto-generated method stub
                calendarSticker.setVisibility(View.GONE);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }
        });


        // set the on Double tap listener
        gd.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.i("TAP", "Doppio tap riconosciuto!");
                PutIntoCalendar(motionView, "u");
                return false;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // TODO Auto-generated method stub
                return false;
            }

        });

        recyclerView = findViewById(R.id.rv);

        Bundle p = getIntent().getExtras();
        if(p != null){
            restoredIdNote =p.getString("idNote");
            Log.i(TAG,"---------------------"+ restoredIdNote + "---------------------------");
            createNoteFromFirebase(restoredIdNote);
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        if(slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.save_note);
            builder.setMessage(R.string.doyouwant);

            // add a button
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SaveNote();
                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
//                    Intent intent = new Intent(NoteActivity.this, NoteListActivity.class);
//                    startActivity(intent);
                }
            });

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }

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
                alert.setTitle(R.string.brush);
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
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                //DONE BUTTON
                alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

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

        add_check_box = findViewById(R.id.add_check_box);
        add_check_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCheckBox(v);
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
                cloudPhotoUrl = "";
                localPhotoBackgroungUrl = "";
                Log.i(TAG,"allocato colorIndex");
                //Cambio con transizione da un colore all'altro
                setBackgroundColor(colorIndex);
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

        findViewById(R.id.load_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                RunTimePermission runTimePermission = new RunTimePermission(
                        NoteActivity.this);
                runTimePermission.requestPermission(new String[]{
                        Manifest.permission.WRITE_CALENDAR,
                        Manifest.permission.READ_CALENDAR
                }, new RunTimePermission.RunTimePermissionListener() {

                    @Override
                    public void permissionGranted() {
                        Log.i(TAG, "check calendar permission ok");
                        PutIntoCalendar(v,"a");
                    }

                    @Override
                    public void permissionDenied() { finish(); }
                });
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
                        motionView.getHeight(),stickerResId);
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

    private void setBackgroundColor(int color){

        TransitionManager.beginDelayedTransition(transitionMainViewContainer,
                new Recolor());

        MainLayout.setBackgroundDrawable(
                new ColorDrawable(Color.parseColor(allColors[color])));

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
                    if(url != null || !url.equals("")){
                        changePhotoBackground(url,"");
                        if(cloudPhotoUrl.equals("")){
                            cloudPhotoUrl = UUID.randomUUID().toString();
                        }else {
                            cloudPhotoUrl = restoredNote.getCloudPhotoUrl();
                        }
                    }

                }
            }
        }
    }

//    private void uploadPhotoOnFirebase(String url) {
//        File file = new File(url);
//
//        StorageReference ref = storageReference.child("images/"+ cloudPhotoUrl);
//        ref.putFile(Uri.fromFile(file))
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(NoteActivity.this, "Photo Uploaded", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                        Toast.makeText(NoteActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        finish();
//                    }
//                });
//
//
//    }

    private void changePhotoBackground(String locUrl, String cloudUrl) {
        Log.i(TAG, locUrl);
        File file = new File(locUrl);


        if(file.exists()){
            if(MainLayout!=null){
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    localPhotoBackgroungUrl = file.getAbsolutePath();
                    Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    MainLayout.setBackground(drawable);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }else {
            if (!cloudUrl.equals("")) {
                StorageReference gsReference = storage.getReferenceFromUrl("" +
                        "gs://drawingapp-28b20.appspot.com/images/" + cloudUrl);

                final long ONE_MEGABYTE = 1024 * 1024;
                gsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap workingBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Bitmap mBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Drawable drawable = new BitmapDrawable(getResources(), mBitmap);
                        MainLayout.setBackground(drawable);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.e(TAG, "Error on restore Draw" + exception);
                        Toast.makeText(NoteActivity.this, "Error on Restore Photo,\nPlease Check Internet Connection", Toast.LENGTH_LONG).show();
                    }
                });

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

    public String uploadDrawImage() {

        //final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setTitle(R.string.upload);
        //progressDialog.show();
        String url = "";

        /*if(restoredNote != null){
            if(restoredNote.getDrawUrl()!=""){
                url = restoredNote.getDrawUrl();
            }
        }else{*/
            url =UUID.randomUUID().toString();
        //}

        /*StorageReference ref = storageReference.child("draw/"+ url);
        ref.putBytes(paintView.convertBitmapToByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //progressDialog.dismiss();
                        Toast.makeText(NoteActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //progressDialog.dismiss();
                        Toast.makeText(NoteActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/
        addBitmapToMemoryCache(url, paintView.getDrawingCache());

        return url;
    }

    public void loadImagePaintView(String drawUrl){
        // Create a storage reference from our app
        // Create a reference to a file from a Google Cloud Storage URI
        StorageReference gsReference = storage.getReferenceFromUrl("" +
                "gs://drawingapp-28b20.appspot.com/draw/"+ drawUrl);

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
                Log.e(TAG, "Error on restore Draw" + exception);
                Toast.makeText(NoteActivity.this, "Error on Restore Draw,\nPlease Check Internet Connection", Toast.LENGTH_LONG).show();
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

    //Rimuove tutte le entità grafiche
    private void deleteAll(){
        motionView.deleteAllEntities();
        paintView.clear();
    }

    private void SaveNote() {
        //Algoritmo per ripristinare le text entities
        List<MotionEntity> l = motionView.getEntities();
        NoteEntityData n;
        String title = editTextTitle.getText().toString();
        if(title.equals("")){
            title = "No Title Note...";
        }

        if(!restoredIdNote.contentEquals("")){
            n = new NoteEntityData(restoredIdNote, userId , title, edit_text_scroll_view.getText().toString());
            n.setCategory(restoredNote.getCategory());
            n.setDateCreation(restoredNote.getDateCreation());
            n.setDateModification(String.valueOf(System.currentTimeMillis()));
        }else{
            n = new NoteEntityData("", userId , title, edit_text_scroll_view.getText().toString());
        }

        int deg= 0;
        float x = 0;
        float y = 0;
        float scale = 0;
        String font, contentText;
        int color;
        int idImage = 0;

        //TODO upload of a draw
        String drawUrl = uploadDrawImage();
        n.setDrawUrl(drawUrl);

        for (int i = 0; i < l.size(); i++){
            if(l.get(i) instanceof TextEntity){
                Log.i(TAG,"Text Sticker");
                deg = (int) l.get(i).getLayer().getRotationInDegrees();
                x = l.get(i).getLayer().getX();
                y = l.get(i).getLayer().getY();
                scale = l.get(i).getLayer().getScale();
                font  = ((TextEntity)l.get(i)).getLayer().getFont().getTypeface();
                color = ((TextEntity)l.get(i)).getLayer().getFont().getColor();
                contentText = ((TextEntity)l.get(i)).getLayer().getText();
                TextEntityData t = new TextEntityData(x, y,contentText,font, deg,scale, color);
                n.addTextElement(t);
            } else if (l.get(i) instanceof  ImageEntity){
                Log.i(TAG,"Image Sticker");
                deg = (int) l.get(i).getLayer().getRotationInDegrees();
                x = l.get(i).getLayer().getX();
                y = l.get(i).getLayer().getY();
                scale = l.get(i).getLayer().getScale();
                idImage = ((ImageEntity)l.get(i)).getStickerID();
                ImageEntityData t = new ImageEntityData(x, y, idImage, deg, scale);
                n.addImageElement(t);
            }
        }

        if(customAdapter!=null){
            n.setCheckboxList(customAdapter.getStepList());
        }


//        String child;
//        //Controllo se prima avevo creato l'entità
//        if(restoredIdNote!= ""){
//            child = restoredIdNote;
//        }else{
//            child = dbTextNotes.push().getKey();
//            n.setId(child);
//        }
        n.setBackgroundColorIndex(colorIndex);

        n.setLocalPhotoUrl(localPhotoBackgroungUrl);
        n.setCalendarEntity(calendarEntity);

        if(!cloudPhotoUrl.contentEquals("")){
            n.setCloudPhotoUrl(cloudPhotoUrl);
            //uploadPhotoOnFirebase(localPhotoBackgroungUrl);
        }

        //dbTextNotes.child(child).setValue(n);
        Toast.makeText(this, R.string.addnote, Toast.LENGTH_SHORT).show();

        startService(new Intent(this, MyUploadService.class)
                .putExtra("NOTE_PASSED", n)
                .setAction(MyUploadService.ACTION_UPLOAD));

        finish();


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
                addTextSticker(result.getResolvedQuery());
                break;

        }
    }

    @Override
    public void onError(AIError error) {
        Log.i(TAG,error.toString());
        Toast.makeText(this, R.string.connectionerror, Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            Intent intent = new Intent(NoteActivity.this, CameraPermissionActivity.class);
            startActivity(intent);

        }else{
            Intent intent = new Intent(NoteActivity.this, PhotoActivity.class);
            startActivityForResult(intent, PHOTO_REQUEST_CODE);
            //finish();
        }

    }

    private void AddCheckBox(View v) {
        final LayoutInflater inflater = getLayoutInflater();
        final View alertLayoutCalendar = inflater.inflate(R.layout.popup_checkbox, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayoutCalendar);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        edit_text_checkbox = alertLayoutCalendar.findViewById(R.id.edit_text_checkbox);

        //DONE BUTTON
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                textCheckbox = edit_text_checkbox.getText().toString();

                if(textCheckbox.equals("")) {
                    Toast.makeText(NoteActivity.this,R.string.emptytext,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    list_checkbox.add(new RowItem(false,textCheckbox));
                    UpdateCheckboxList();
                }
                dialog.dismiss();
            }
        });

        //CANCEL BUTTON
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void UpdateCheckboxList() {

        final RowItem rowItem = new RowItem(false, textCheckbox);
        Toast.makeText(NoteActivity.this, textCheckbox, Toast.LENGTH_SHORT).show();

        //To show at least one row
        if (list_checkbox == null || list_checkbox.size() == 0) {
            list_checkbox = new ArrayList<>();
            list_checkbox.add(rowItem);
        }


        customAdapter = new ListAdapter(list_checkbox, this);

        llm = new LinearLayoutManager(this);

        recyclerView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        recyclerView.setLayoutManager(llm);

    }


    @TargetApi(Build.VERSION_CODES.N)
    private void PutIntoCalendar(View v, final String s) {

        final LayoutInflater inflater = getLayoutInflater();
        final View alertLayoutCalendar = inflater.inflate(R.layout.popup_calendar, null);

        final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        if(s.equals("a"))
            alert.setTitle(R.string.calendartitle);
        else
            alert.setTitle(R.string.modify_calendartitle);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayoutCalendar);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        final RelativeLayout layoutButtonTime = alertLayoutCalendar.findViewById(R.id.layout_btn_time);
        final RelativeLayout layoutTimeView = alertLayoutCalendar.findViewById(R.id.layout_time_view);

        titleCalendar = alertLayoutCalendar.findViewById(R.id.edit_text_title_calendar);
        text_date = alertLayoutCalendar.findViewById(R.id.txt_date);
        text_time_start = alertLayoutCalendar.findViewById(R.id.txt_time_start);
        text_time_end = alertLayoutCalendar.findViewById(R.id.txt_time_end);
        txt_description = alertLayoutCalendar.findViewById(R.id.txt_description);

        if(s.equals("u")) {

            titleCalendar.setText(stringTitleCalendar);
            txt_description.setText(stringDescription);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ITALY);
            text_date.setText(dateFormat.format(myCalendarStart.getTime()));

            checkBoxDay = alertLayoutCalendar.findViewById(R.id.cb_day);
            if(checkbox == true) {
                checkBoxDay.setChecked(true);
                layoutButtonTime.setVisibility(alertLayoutCalendar.INVISIBLE);
                layoutTimeView.setVisibility(alertLayoutCalendar.INVISIBLE);
            }
            else {
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
                text_time_start.setText(timeFormat.format(myCalendarStart.getTime()));
                text_time_end.setText(timeFormat.format(myCalendarEnd.getTime()));
            }
        }
        else {
            checkBoxDay = alertLayoutCalendar.findViewById(R.id.cb_day);
        }

        checkBoxDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxDay.isChecked()) {
                    layoutButtonTime.setVisibility(alertLayoutCalendar.INVISIBLE);
                    layoutTimeView.setVisibility(alertLayoutCalendar.INVISIBLE);
                }
                else {
                    layoutButtonTime.setVisibility(alertLayoutCalendar.VISIBLE);
                    layoutTimeView.setVisibility(alertLayoutCalendar.VISIBLE);
                }
            }
        });

        final Button btn_setdata = alertLayoutCalendar.findViewById(R.id.btn_date);
        final Button btn_hours = alertLayoutCalendar.findViewById(R.id.btn_time);

        btn_setdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDatePicker();
            }
        });

        btn_hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTimePicker();
            }
        });

        check = true;

        //DONE BUTTON
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //CANCEL BUTTON
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = alert.create();
        dialog.show();

        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(titleCalendar.getText().toString().equals("") ||
                        ((text_date.getText().toString()).equals("No selected data"))) {
                    Toast.makeText(NoteActivity.this,R.string.noevent,
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!(checkBoxDay.isChecked())) {
                        if((text_time_end.getText().toString().equals("No selected time")) ||
                                ((text_time_start.getText().toString()).equals("No selected time")) ||
                                (myCalendarStart.getTimeInMillis() > myCalendarEnd.getTimeInMillis())) {

                            Toast.makeText(NoteActivity.this, R.string.noevent,
                                    Toast.LENGTH_SHORT).show();

                            check = false;
                        }
                        else
                            check = true;
                    }
                    else
                        check = true;

                    if (check == true){

                        switch (s) {
                            case "a":
                                AddCalendarEvent();
                                break;
                            case "u":
                                UpdateCalendarEvent();
                                break;
                        }
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    private void SetDatePicker() {

        final int mYear = Calendar.getInstance().get(Calendar.YEAR);
        final int mMonth = Calendar.getInstance().get(Calendar.MONTH);
        final int mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(NoteActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear,
                                          int selectedmonth, int selectedday) {

                        String date = selectedday + "/" + selectedmonth + "/" +selectedyear;
                        text_date.setText(date);
                        myCalendarStart.set(selectedyear,selectedmonth,selectedday);
                        myCalendarEnd.set(selectedyear,selectedmonth,selectedday);
                    }
                }, mYear, mMonth, mDay);
        mDatePicker.setTitle(R.string.selectdate);
        mDatePicker.show();
    }

    private void SetTimePicker() {

        RangeTimePickerDialog dialog = new RangeTimePickerDialog();
        dialog.newInstance();
        dialog.setIs24HourView(false);
        dialog.setRadiusDialog(20);
        dialog.setTextTabStart("START");
        dialog.setTextTabEnd("END");
        dialog.setTextBtnNegative("CANCEL");
        dialog.setTextBtnPositive("OK");

        dialog.setValidateRange(false);
        dialog.setColorBackgroundHeader(R.color.lightBlue);
        dialog.setColorBackgroundTimePickerHeader(R.color.lightBlue);
        dialog.setColorTextButton(R.color.colorPrimaryDark);
        FragmentManager fragmentManager = getFragmentManager();
        dialog.show(fragmentManager, "");
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {

        myCalendarStart.set(Calendar.HOUR_OF_DAY, hourStart);
        myCalendarStart.set(Calendar.MINUTE, minuteStart);
        myCalendarEnd.set(Calendar.HOUR_OF_DAY, hourEnd);
        myCalendarEnd.set(Calendar.MINUTE, minuteEnd);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        text_time_start.setText(sdf.format(myCalendarStart.getTime()));
        text_time_end.setText(sdf.format(myCalendarEnd.getTime()));
    }

    private void AddCalendarEvent() {

        stringTitleCalendar =  titleCalendar.getText().toString();
        stringDescription = txt_description.getText().toString();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.TITLE, stringTitleCalendar);
        values.put(CalendarContract.Events.DESCRIPTION, stringDescription);
        //  values.put(CalendarContract.Events.EVENT_LOCATION, "Somewhere");

        String calenderEmaillAddress = currentFirebaseUser.getEmail();
        int calenderId = 3;
        String[] projection = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME};
        Cursor cursor = cr.query(Uri.parse("content://com.android.calendar/calendars"), projection,
                CalendarContract.Calendars.ACCOUNT_NAME + "=? and (" +
                        CalendarContract.Calendars.NAME + "=? or " +
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + "=?)",
                new String[]{calenderEmaillAddress, calenderEmaillAddress,
                        calenderEmaillAddress}, null);
        if (cursor.moveToFirst()) {
            if (cursor.getString(1).equals(calenderEmaillAddress))
                calenderId=cursor.getInt(0); //youre calender id to be insered in above your code
        }
        values.put(CalendarContract.Events.CALENDAR_ID, calenderId);

        values.put(CalendarContract.Events.EVENT_TIMEZONE,
                Calendar.getInstance().getTimeZone().getID());

        if (checkBoxDay.isChecked()) {
            myCalendarStart.set(Calendar.HOUR, 0);
            myCalendarStart.set(Calendar.MINUTE, 0);
            myCalendarStart.set(Calendar.SECOND, 0);
            values.put(CalendarContract.Events.DTSTART, myCalendarStart.getTimeInMillis());
            values.put(CalendarContract.Events.DURATION,  "PT1D");
            values.put(CalendarContract.Events.ALL_DAY, 1);
            checkbox = true;

            calendarEntity = new CalendarEntity(1, myCalendarStart.getTimeInMillis(), stringTitleCalendar, stringDescription, "PT1D",
                    calenderId, Calendar.getInstance().getTimeZone().getID());
        }
        else {
            values.put(CalendarContract.Events.DTSTART, myCalendarStart.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, myCalendarEnd.getTimeInMillis());

            calendarEntity = new CalendarEntity(myCalendarStart.getTimeInMillis(), myCalendarEnd.getTimeInMillis(), stringTitleCalendar,
                    stringDescription, calenderId, Calendar.getInstance().getTimeZone().getID());

        }
        if (ActivityCompat.checkSelfPermission(NoteActivity.this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        AddStickerCalendar();
        calendarSticker.bringToFront();
    }

    private void UpdateCalendarEvent() {

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri = null;

        // The new title for the event
        values.put(CalendarContract.Events.TITLE, titleCalendar.getText().toString());
        values.put(CalendarContract.Events.DESCRIPTION, txt_description.getText().toString());
        //  values.put(CalendarContract.Events.EVENT_LOCATION, "Somewhere");

        if (checkBoxDay.isChecked()) {
            values.put(CalendarContract.Events.ALL_DAY, 1);
            myCalendarStart.set(Calendar.HOUR, 0);
            myCalendarStart.set(Calendar.MINUTE, 0);
            myCalendarStart.set(Calendar.SECOND, 0);
            values.put(CalendarContract.Events.DTSTART, myCalendarStart.getTimeInMillis());
            values.put(CalendarContract.Events.DURATION,  "PT1D");
        }
        else {
            values.put(CalendarContract.Events.DTSTART, myCalendarStart.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, myCalendarEnd.getTimeInMillis());
        }

        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);

        int rows = getContentResolver().update(updateUri, values, null, null);
        Log.i(TAG, "Rows updated: " + rows);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
        String date = timeFormat.format(myCalendarStart.getTime()) + " " +
                dateFormat.format(myCalendarStart.getTime());

        String newTitle = titleCalendar.getText().toString();
        String changeEvent ="<b>" + newTitle.toUpperCase() + "</b> " + "<br/>" + date;

        calendarSticker.setText(Html.fromHtml(changeEvent));
    }

    @SuppressLint({"ClickableViewAccessibility"})
    @TargetApi(Build.VERSION_CODES.N)
    private void AddStickerCalendar() {

        calendarSticker = new TextView(getApplicationContext());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String time = "";
        if(!(checkbox)) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
            time = timeFormat.format(myCalendarStart.getTime());
        }
        else
            time = "ALL DAY";

        String date = time + " " +
                dateFormat.format(myCalendarStart.getTime());

        String sourceString = "<b>" + stringTitleCalendar.toUpperCase() + "</b> " + "<br/>" + date;
        calendarSticker.setText(Html.fromHtml(sourceString));
        calendarSticker.setTextSize(20);
        calendarSticker.setPadding(20,20,20,20);
        calendarSticker.setTextColor(getResources().getColor(R.color.colorPrimary));


        calendarSticker.setBackgroundResource(R.drawable.button_background);

        calendarSticker.setGravity(Gravity.CENTER);
        calendarSticker.bringToFront();

//        calendarSticker.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                // TODO Auto-generated method stub
//
//                calendarSticker.setVisibility(View.GONE);
//                //((ConstraintLayout)calendarSticker.getParent()).removeView(calendarSticker);
//                return true;
//            }
//        });


        calendarSticker.setOnTouchListener(new View.OnTouchListener() {
            private static final int MIN_CLICK_DURATION = 1000;
            private long startClickTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        v.setY(event.getRawY() + dY);
                        v.setX(event.getRawX() + dX);
                        lastAction = MotionEvent.ACTION_MOVE;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN)
                        break;
                    default:

                }
                gd.onTouchEvent(event);

                return true;
            }
        });
        MainLayout.addView(calendarSticker);
    }

    public void createNoteFromFirebase(String id){
        DatabaseReference dbRef = dbTextNotes.child(restoredIdNote);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "+++++++++++++++" + dataSnapshot.toString());
                restoredNote = dataSnapshot.getValue(NoteEntityData.class);
                //createNote();
                editTextTitle.setText(restoredNote.getTitle());//Title
                edit_text_scroll_view.setText(restoredNote.getDescription());//Description in swipe view

                //List<TextEntityData> list = restoredNote.getTextEntityDataList();

                loadImagePaintView(restoredNote.getDrawUrl());
                if(restoredNote.getCheckboxList().size()>0){
                    customAdapter = new ListAdapter((ArrayList<RowItem>) restoredNote.getCheckboxList(), NoteActivity.this);
                    recyclerView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                    llm = new LinearLayoutManager(NoteActivity.this);
                    recyclerView.setLayoutManager(llm);
                }

                motionView.post(new Runnable() {
                    @Override
                    public void run() {
                        List<TextEntityData> list = restoredNote.getTextEntityDataList();
                        List<String> fonts = fontProvider.getFontNames();
                        int index = -1;
                        //Restore Text Entity
                        for(int i=0; i < list.size(); i++){

                            String searchString = list.get(i).getFont();

                            for (int j=0;j<fonts.size();j++) {
                                if (fonts.get(j).equals(searchString)) {
                                    index = j;
                                    break;
                                }
                            }



                            TextLayer textLayer = createTextLayer(list.get(i).getText());
                            textLayer.getFont().setTypeface(fonts.get(index));
                            textLayer.getFont().setColor(list.get(i).getColor());

                            TextEntity textEntity = new TextEntity(textLayer, motionView.getWidth(),
                                    motionView.getHeight(), fontProvider);

                            textEntity.getLayer().setScale(list.get(i).getScale());
                            textEntity.getLayer().setRotationInDegrees(list.get(i).getDeg());
                            textEntity.getLayer().setX(list.get(i).getX());
                            textEntity.getLayer().setY(list.get(i).getY());
                            textEntity.updateEntity();
                            motionView.addEntity(textEntity);

                            //motionView.invalidate();

                        }
                    }
                });

                //setto gli indirizzi per le foto
                localPhotoBackgroungUrl = restoredNote.getLocalPhotoUrl();
                cloudPhotoUrl = restoredNote.getCloudPhotoUrl();

                setBackgroundColor(restoredNote.getBackgroundColorIndex());
                colorIndex = restoredNote.getBackgroundColorIndex();

                if(restoredNote.getCalendarEntity()!=null){

                    if(restoredNote.getCalendarEntity().getAllDay() == 1){
                        calendarEntity = new CalendarEntity(1,
                                restoredNote.getCalendarEntity().getStartTime(),
                                restoredNote.getCalendarEntity().getTitleCalendar(),
                                restoredNote.getCalendarEntity().getDescription(),
                                restoredNote.getCalendarEntity().getEndTimeAllday(),
                                restoredNote.getCalendarEntity().getCalendarID(),
                                restoredNote.getCalendarEntity().getTimeZone());
                    }
                    else {
                        calendarEntity = new CalendarEntity(
                                restoredNote.getCalendarEntity().getStartTime(),
                                restoredNote.getCalendarEntity().getEndTime(),
                                restoredNote.getCalendarEntity().getTitleCalendar(),
                                restoredNote.getCalendarEntity().getDescription(),
                                restoredNote.getCalendarEntity().getCalendarID(),
                                restoredNote.getCalendarEntity().getTimeZone());
                    }

                    myCalendarStart.setTimeInMillis(calendarEntity.getStartTime());
                    myCalendarEnd.setTimeInMillis(calendarEntity.getEndTime());
                    stringTitleCalendar = calendarEntity.getTitleCalendar();
                    stringDescription = calendarEntity.getDescription();

                    AddStickerCalendar();
                }

                if(!localPhotoBackgroungUrl.equals("") && !cloudPhotoUrl.equals("")){
                    changePhotoBackground(localPhotoBackgroungUrl,cloudPhotoUrl);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });




    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


}
