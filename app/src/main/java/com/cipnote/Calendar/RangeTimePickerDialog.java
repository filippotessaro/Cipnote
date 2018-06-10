package com.cipnote.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cipnote.R;

import java.lang.reflect.Field;

public class RangeTimePickerDialog extends DialogFragment
{
    public static String HOUR_START = "hourStart";
    public static String MINUTE_START = "minuteStart";
    public static String HOUR_END = "hourEnd";
    public static String MINUTE_END = "minuteEnd";
    private AlertDialog mAlertDialog;
    private boolean dialogDismissed;
    private TabLayout tabLayout;
    private TabItem tabItemStartTime, tabItemEndTime;
    private TimePicker timePickerStart, timePickerEnd;
    private Button btnPositive, btnNegative;

    private int colorTabUnselected = R.color.white;
    private int colorTabSelected = R.color.colorAccent;
    private int colorTextButton = R.color.colorAccent;
    private int colorBackgroundHeader = R.color.CyanWater;
    private int colorBackgroundTimePickerHeader = R.color.CyanWater;
    private boolean is24HourView = true;
    private String messageErrorRangeTime = "Error: set a end time greater than start time";
    private String textBtnPositive = "Ok";
    private String textBtnNegative = "Cancel";
    private String textTabStart = "Start time";
    private String textTabEnd = "End time";
    private int radiusDialog = 50; // Default 50
    private boolean validateRange = true;

    public interface ISelectedTime
    {
        void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd);
    }

    public RangeTimePickerDialog newInstance()
    {
        RangeTimePickerDialog f = new RangeTimePickerDialog();
        return f;
    }

    private ISelectedTime mCallback;

    /**
     * Create a new instance with own attributes (All color MUST BE in this format "R.color.my_color")
     * @param colorBackgroundHeader Color of Background header dialog and timePicker
     * @param colorTabUnselected Color of tab when unselected
     * @param colorTabSelected Color of tab when selected
     * @param colorTextButton Text color of button
     * @param is24HourView Indicates if the format should be 24 hours
     * @return
     */
    public RangeTimePickerDialog newInstance(int colorBackgroundHeader, int colorTabUnselected, int colorTabSelected, int colorTextButton, boolean is24HourView)
    {
        RangeTimePickerDialog f = new RangeTimePickerDialog();
        this.colorTabUnselected = colorTabUnselected;
        this.colorBackgroundHeader = colorBackgroundHeader;
        this.colorBackgroundTimePickerHeader = colorBackgroundHeader;
        this.colorTabSelected = colorTabSelected;
        this.colorTextButton = colorTextButton;
        this.is24HourView = is24HourView;
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.layout_custom_dialog, null);
        builder.setView(dialogView);
        tabLayout = dialogView.findViewById(R.id.tabLayout);
        tabItemStartTime = dialogView.findViewById(R.id.tabStartTime);
        tabItemEndTime = dialogView.findViewById(R.id.tabEndTime);
        timePickerStart = dialogView.findViewById(R.id.timePickerStart);
        timePickerEnd = dialogView.findViewById(R.id.timePickerEnd);
        btnPositive = dialogView.findViewById(R.id.btnPositiveDialog);
        btnNegative = dialogView.findViewById(R.id.btnNegativeDialog);
        CardView cardView = dialogView.findViewById(R.id.ly_root);

        // Set TimePicker header background color
        setTimePickerHeaderBackgroundColor(this, ContextCompat.getColor(getActivity(), colorBackgroundTimePickerHeader), "timePickerStart");
        setTimePickerHeaderBackgroundColor(this, ContextCompat.getColor(getActivity(), colorBackgroundTimePickerHeader), "timePickerEnd");

        // Set radius of dialog
        cardView.setRadius(radiusDialog);

        setColorTabLayout(colorTabSelected, colorTabUnselected, colorBackgroundHeader);

        timePickerStart.setIs24HourView(is24HourView);
        timePickerEnd.setIs24HourView(is24HourView);

        btnPositive.setTextColor(ContextCompat.getColor(getActivity(), colorTextButton));
        btnNegative.setTextColor(ContextCompat.getColor(getActivity(), colorTextButton));
        btnPositive.setText(textBtnPositive);
        btnNegative.setText(textBtnNegative);

        tabLayout.getTabAt(0).setText(textTabStart);
        tabLayout.getTabAt(1).setText(textTabEnd);

        // Create the AlertDialog object and return it
        mAlertDialog = builder.create();
        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
                {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab)
                    {
                        int tabIconColor = ContextCompat.getColor(getActivity(), colorTabSelected);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                        //tab.getIcon().setTint(Color.YELLOW);
                        if(tab.getPosition()==0)
                        {
                            timePickerStart.setVisibility(View.VISIBLE);
                            timePickerEnd.setVisibility(View.GONE);
                        }
                        else
                        {
                            timePickerStart.setVisibility(View.GONE);
                            timePickerEnd.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab)
                    {
                        int tabIconColor = ContextCompat.getColor(getActivity(), colorTabUnselected);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                        //tab.getIcon().setTint(Color.WHITE);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab)
                    {

                    }
                });

                btnNegative.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dismiss();
                    }
                });
                btnPositive.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        boolean flagCorrect;
                        int hourStart, minuteStart, hourEnd, minuteEnd;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            hourStart = timePickerStart.getHour();
                            minuteStart = timePickerStart.getMinute();
                            hourEnd = timePickerEnd.getHour();
                            minuteEnd = timePickerEnd.getMinute();
                        }
                        else
                        {
                            hourStart = timePickerStart.getCurrentHour();
                            minuteStart = timePickerStart.getCurrentMinute();
                            hourEnd = timePickerEnd.getCurrentHour();
                            minuteEnd = timePickerEnd.getCurrentMinute();
                        }
                        if(validateRange)
                        {
                            if(hourEnd>hourStart)
                            {
                                flagCorrect = true;
                            }
                            else if(hourEnd==hourStart && minuteEnd>minuteStart)
                            {
                                flagCorrect = true;
                            }
                            else
                            {
                                flagCorrect = false;
                            }
                        }
                        else
                        {
                            flagCorrect = true;
                        }
                        if(flagCorrect)
                        {
                            // Check if this dialog was called by a fragment
                            if (getTargetFragment()!=null)
                            {
                                // Return value to Fragment
                                Bundle bundle = new Bundle();
                                bundle.putInt(HOUR_START, hourStart);
                                bundle.putInt(MINUTE_START, minuteStart);
                                bundle.putInt(HOUR_END, hourEnd);
                                bundle.putInt(MINUTE_END, minuteEnd);
                                Intent intent = new Intent().putExtras(bundle);
                                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                            }
                            else
                            {
                                // Return value to activity
                                mCallback.onSelectedTime(hourStart,minuteStart,hourEnd,minuteEnd);
                            }
                            dismiss();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), messageErrorRangeTime,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return mAlertDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        dialogDismissed = true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (dialogDismissed && mAlertDialog != null)
        {
            mAlertDialog.dismiss();
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallback = (ISelectedTime) activity;
        }
        catch (ClassCastException e)
        {
            Log.d("MyDialog", "Activity doesn't implement the interface");
        }
    }

    /**
     * Set color of tab item when it is unselected
     * @param colorTabUnselected (eg. R.color.my_color)
     */
    public void setColorTabUnselected(int colorTabUnselected)
    {
        this.colorTabUnselected = colorTabUnselected;
    }

    /**
     * Set color of tab item when it is selected
     * @param colorTabSelected (eg. R.color.my_color)
     */
    public void setColorTabSelected(int colorTabSelected)
    {
        this.colorTabSelected = colorTabSelected;
    }

    /**
     * Set button text color
     * @param colorTextButton (eg. R.color.my_color)
     */
    public void setColorTextButton(int colorTextButton)
    {
        this.colorTextButton = colorTextButton;
    }

    /**
     * Set background color of header dialog
     * @param colorBackgroundHeader (eg. R.color.my_color)
     */
    public void setColorBackgroundHeader(int colorBackgroundHeader)
    {
        this.colorBackgroundHeader = colorBackgroundHeader;
    }

    /**
     * Set true if you want see time into 24 hour format
     * @param is24HourView true = 24 hour format, false = am/pm format
     */
    public void setIs24HourView(boolean is24HourView)
    {
        this.is24HourView = is24HourView;
    }

    /**
     * Set message error that appears when you select a end time greater than start time (only if validateRange is true)
     * @param messageErrorRangeTime String
     */
    public void setMessageErrorRangeTime(String messageErrorRangeTime)
    {
        this.messageErrorRangeTime = messageErrorRangeTime;
    }

    /**
     * Set positive button text
     * @param textBtnPositive (eg. Ok or Accept)
     */
    public void setTextBtnPositive(String textBtnPositive)
    {
        this.textBtnPositive = textBtnPositive;
    }

    /**
     * Set negative button text
     * @param textBtnNegative (eg. Cancel or Close)
     */
    public void setTextBtnNegative(String textBtnNegative)
    {
        this.textBtnNegative = textBtnNegative;
    }

    /**
     * Set dialog radius (default is 50)
     * @param radiusDialog Set to 0 if you want remove radius
     */
    public void setRadiusDialog(int radiusDialog)
    {
        this.radiusDialog = radiusDialog;
    }

    /**
     * Set tab start text
     * @param textTabStart (eg. Start time)
     */
    public void setTextTabStart(String textTabStart)
    {
        this.textTabStart = textTabStart;
    }

    /**
     * Set tab end text
     * @param textTabEnd (eg. End time)
     */
    public void setTextTabEnd(String textTabEnd)
    {
        this.textTabEnd = textTabEnd;
    }

    /**
     * Set true if you want validate the range time (start time < end time).
     * Set false if you want select any time
     * @param validateRange true = validation, false = no validation
     */
    public void setValidateRange(boolean validateRange)
    {
        this.validateRange = validateRange;
    }

    /**
     * Set background color of header timePicker
     * @param colorBackgroundTimePickerHeader (eg. R.color.my_color)
     */
    public void setColorBackgroundTimePickerHeader(int colorBackgroundTimePickerHeader)
    {
        this.colorBackgroundTimePickerHeader = colorBackgroundTimePickerHeader;
    }

    private void setColorTabLayout(int colorTabSelected, int colorTabUnselected,
                                   int colorBackgroundHeader)
    {
        tabLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), colorBackgroundHeader));
        // Set color header TabLayout
        tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), colorTabUnselected),
                ContextCompat.getColor(getActivity(), colorTabSelected));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(),
                colorTabSelected));
        // Use setColorFilter to avoid setTint because setTint is for API >= 21
        int tabIconColor = ContextCompat.getColor(getActivity(), colorTabSelected);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        //tabLayout.getTabAt(0).getIcon().setTint(Color.YELLOW);
        tabIconColor = ContextCompat.getColor(getActivity(), colorTabUnselected);
        tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        //tabLayout.getTabAt(1).getIcon().setTint(Color.WHITE);
    }

    /**
     * Set color of timePicker'header
     * @param rangeTimePickerDialog Dialog where is located the timePicker
     * @param color Color to set
     * @param nameTimePicker id of timePicker declared into xml (eg. my_time_picker [android:id="@+id/my_time_picker"])
     */
    private void setTimePickerHeaderBackgroundColor(RangeTimePickerDialog rangeTimePickerDialog,
                                                    int color, String nameTimePicker)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            try
            {
                Field mTimePickerField;
                mTimePickerField = RangeTimePickerDialog.class.getDeclaredField(nameTimePicker);
                mTimePickerField.setAccessible(true);
                final TimePicker mTimePicker = (TimePicker) mTimePickerField.get(
                        rangeTimePickerDialog);
                int headerId = Resources.getSystem().getIdentifier("time_header",
                        "id", "android");
                final View header = mTimePicker.findViewById(headerId);
                header.setBackgroundColor(color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    int headerTextId = Resources.getSystem().getIdentifier("input_header", "id", "android");
                    final View headerText = mTimePicker.findViewById(headerTextId);
                    headerText.setBackgroundColor(color);
                    headerText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
