package com.cipnote.data;

public class TextEntityData {

    private String id;
    private float x,y;
    private String text;
    private String font;
    private int deg;
    private float scale;

    public TextEntityData(String _id,float x, float y, String text, String font, int deg, float scale) {
        this.id = _id;
        this.x = x;
        this.y = y;
        this.text = text;
        this.font = font;
        this.deg = deg;
        this.scale = scale;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public int getDeg() {
        return deg;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
