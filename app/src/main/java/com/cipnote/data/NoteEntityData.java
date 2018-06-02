package com.cipnote.data;
import java.util.ArrayList;
import java.util.List;

public class NoteEntityData {
    String id, userId, title, description;

//    ImagesUrl images;
    String dateCreation, dateModification;

    List<TextEntityData> textEntityDataList;

    public NoteEntityData(String id, String u, String title, String description) {
        this.id = id;
        this.userId = u;
        this.title = title;
        this.description = description;
        dateCreation = String.valueOf(System.currentTimeMillis());
        dateModification = String.valueOf(System.currentTimeMillis());
//        images = null;
        textEntityDataList = new ArrayList<TextEntityData>();
    }

    public void addTextElement(TextEntityData t){
        textEntityDataList.add(t);
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

//    public ImagesUrl getImages() {
//        return images;
//    }
//
//    public void setImages(ImagesUrl images) {
//        this.images = images;
//    }

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

    public List<TextEntityData> getTextEntityDataList() {
        return textEntityDataList;
    }

    public void setTextEntityDataList(List<TextEntityData> textEntityDataList) {
        this.textEntityDataList = textEntityDataList;
    }
}