package com.cipnote.data;

import android.os.Parcel;
import android.os.Parcelable;

public class TextEntityData implements Parcelable {

    private float x;
    private float y;
    private String text;
    private String font;
    private int deg;
    private float scale;
    private int color;

    public TextEntityData() {

    }

    public TextEntityData(float x, float y, String text, String font, int deg, float scale, int c) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.font = font;
        this.deg = deg;
        this.scale = scale;
        this.color = c;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
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

    public static final Parcelable.Creator<TextEntityData> CREATOR = new Parcelable.Creator<TextEntityData>() {
        @Override
        public TextEntityData createFromParcel(Parcel in) {
            return new TextEntityData(in);
        }

        @Override
        public TextEntityData[] newArray(int size) {
            return new TextEntityData[size];
        }
    };

    protected TextEntityData(Parcel in) {
        x = in.readFloat();
        y = in.readFloat();
        text = in.readString();
        font = in.readString();
        deg = in.readInt();
        scale = in.readFloat();
        color = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeString(text);
        dest.writeString(font);
        dest.writeInt(deg);
        dest.writeFloat(scale);
        dest.writeInt(color);
    }

}