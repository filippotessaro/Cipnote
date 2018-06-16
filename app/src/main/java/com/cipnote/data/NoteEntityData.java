package com.cipnote.data;
import java.util.ArrayList;
import java.util.List;

public class NoteEntityData {
    private String id, userId, title, description;

//    ImagesUrl images;
    private String dateCreation, dateModification;

    private int category;

    private String drawUrl;
    private String cloudPhotoUrl;
    private String localPhotoUrl;

    private List<TextEntityData> textEntityDataList;
    private List<ImageEntityData> imageEntityDataList;

    public NoteEntityData(String id, String u, String title, String description) {
        this.id = id;
        this.userId = u;
        this.title = title;
        this.description = description;
        this.dateCreation = String.valueOf(System.currentTimeMillis());
        this.dateModification = String.valueOf(System.currentTimeMillis());
//        images = null;
        this.textEntityDataList = new ArrayList<TextEntityData>();
        this.imageEntityDataList = new ArrayList<ImageEntityData>();
        this.category = 0;
        this.drawUrl = "";
        this.cloudPhotoUrl = "";
        this.localPhotoUrl = "";
    }

    public NoteEntityData() {
        textEntityDataList = new ArrayList<TextEntityData>();
        imageEntityDataList = new ArrayList<ImageEntityData>();
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

    public String getDrawUrl() {
        return drawUrl;
    }

    public void setDrawUrl(String drawUrl) {
        this.drawUrl = drawUrl;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void addTextElement(TextEntityData t){
        textEntityDataList.add(t);
    }

    public void addImageElement(ImageEntityData t){
        imageEntityDataList.add(t);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ImageEntityData> getImageEntityDataList() {
        return imageEntityDataList;
    }

    public void setImageEntityDataList(List<ImageEntityData> imageEntityDataList) {
        this.imageEntityDataList = imageEntityDataList;
    }

    public List<TextEntityData> getTextEntityDataList() {
        return textEntityDataList;
    }

    public void setTextEntityDataList(List<TextEntityData> textEntityDataList) {
        this.textEntityDataList = textEntityDataList;
    }
}
