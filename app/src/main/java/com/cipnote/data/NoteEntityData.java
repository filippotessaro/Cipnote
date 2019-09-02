package com.cipnote.data;
import android.os.Parcel;
import android.os.Parcelable;

import com.cipnote.ui.RowItem;

import java.util.ArrayList;
import java.util.List;

public class NoteEntityData implements Parcelable {
    private String id;
    private String userId;
    private String title;
    private String description;

    private String dateCreation;
    private String  dateModification;
    private int category;

    private String drawUrl;
    private String cloudPhotoUrl;
    private String localPhotoUrl;
    private int backgroundColorIndex;

    private CalendarEntity calendarEntity;


    private List<TextEntityData> textEntityDataList;
    private List<ImageEntityData> imageEntityDataList;
    private List<RowItem> checkboxList;



    public NoteEntityData(String id, String u, String title, String description) {
        this.id = id;
        this.userId = u;
        this.title = title;
        this.description = description;
        this.dateCreation = String.valueOf(System.currentTimeMillis());
        this.dateModification = String.valueOf(System.currentTimeMillis());
        this.textEntityDataList = new ArrayList<TextEntityData>();
        this.imageEntityDataList = new ArrayList<ImageEntityData>();
        this.checkboxList = new ArrayList<RowItem>();
        this.category = 0;
        //this.drawUrl = "";
        //this.cloudPhotoUrl = "";
        //this.localPhotoUrl = "";
        this.backgroundColorIndex = 0;
        this.calendarEntity = null;
    }

    public NoteEntityData() {
        this.textEntityDataList = new ArrayList<TextEntityData>();
        this.imageEntityDataList = new ArrayList<ImageEntityData>();
        this.checkboxList = new ArrayList<RowItem>();
        this.calendarEntity = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getDateModification() {
        return dateModification;
    }

    public void setDateModification(String dateModification) {
        this.dateModification = dateModification;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDrawUrl() {
        return drawUrl;
    }

    public void setDrawUrl(String drawUrl) {
        this.drawUrl = drawUrl;
    }

    public String getCloudPhotoUrl() {
        return cloudPhotoUrl;
    }

    public void setCloudPhotoUrl(String cloudPhotoUrl) {
        this.cloudPhotoUrl = cloudPhotoUrl;
    }

    public String getLocalPhotoUrl() {
        return localPhotoUrl;
    }

    public void setLocalPhotoUrl(String localPhotoUrl) {
        this.localPhotoUrl = localPhotoUrl;
    }

    public int getBackgroundColorIndex() {
        return backgroundColorIndex;
    }

    public void setBackgroundColorIndex(int backgroundColorIndex) {
        this.backgroundColorIndex = backgroundColorIndex;
    }

    public CalendarEntity getCalendarEntity() {
        return calendarEntity;
    }

    public void setCalendarEntity(CalendarEntity calendarEntity) {
        this.calendarEntity = calendarEntity;
    }

    public List<TextEntityData> getTextEntityDataList() {
        return textEntityDataList;
    }

    public void setTextEntityDataList(List<TextEntityData> textEntityDataList) {
        this.textEntityDataList = textEntityDataList;
    }

    public List<ImageEntityData> getImageEntityDataList() {
        return imageEntityDataList;
    }

    public void setImageEntityDataList(List<ImageEntityData> imageEntityDataList) {
        this.imageEntityDataList = imageEntityDataList;
    }

    public List<RowItem> getCheckboxList() {
        return checkboxList;
    }

    public void setCheckboxList(List<RowItem> checkboxList) {
        this.checkboxList = checkboxList;
    }

    public void addTextElement(TextEntityData t){
        textEntityDataList.add(t);
    }

    public void addImageElement(ImageEntityData t){
        imageEntityDataList.add(t);
    }

    protected NoteEntityData(Parcel in) {
        id = in.readString();
        userId = in.readString();
        title = in.readString();
        description = in.readString();
        dateCreation = in.readString();
        dateModification = in.readString();
        category = in.readInt();
        drawUrl = in.readString();
        cloudPhotoUrl = in.readString();
        localPhotoUrl = in.readString();
        backgroundColorIndex = in.readInt();
        calendarEntity = (CalendarEntity) in.readValue(CalendarEntity.class.getClassLoader());
        if (in.readByte() == 0x01) {
            textEntityDataList = new ArrayList<TextEntityData>();
            in.readList(textEntityDataList, TextEntityData.class.getClassLoader());
        } else {
            textEntityDataList = null;
        }
        if (in.readByte() == 0x01) {
            imageEntityDataList = new ArrayList<ImageEntityData>();
            in.readList(imageEntityDataList, ImageEntityData.class.getClassLoader());
        } else {
            imageEntityDataList = null;
        }
        if (in.readByte() == 0x01) {
            checkboxList = new ArrayList<RowItem>();
            in.readList(checkboxList, RowItem.class.getClassLoader());
        } else {
            checkboxList = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(dateCreation);
        dest.writeString(dateModification);
        dest.writeInt(category);
        dest.writeString(drawUrl);
        dest.writeString(cloudPhotoUrl);
        dest.writeString(localPhotoUrl);
        dest.writeInt(backgroundColorIndex);
        dest.writeValue(calendarEntity);
        if (textEntityDataList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(textEntityDataList);
        }
        if (imageEntityDataList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(imageEntityDataList);
        }
        if (checkboxList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(checkboxList);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NoteEntityData> CREATOR = new Parcelable.Creator<NoteEntityData>() {
        @Override
        public NoteEntityData createFromParcel(Parcel in) {
            return new NoteEntityData(in);
        }

        @Override
        public NoteEntityData[] newArray(int size) {
            return new NoteEntityData[size];
        }
    };
}