package com.cipnote.data;

public class TextEntityData {
    private float x,y;
    private String text;
    private String font;
    private int deg;
    private float scale;

    public TextEntityData(float x, float y, String text, String font, int deg, float scale) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.font = font;
        this.deg = deg;
        this.scale = scale;
    }
}
